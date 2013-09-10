package com.cs.gang.db;

import com.cs.gang.db.excp.DBInitialException;
import com.cs.gang.db.excp.DBParsingException;
import com.cs.gang.proxy.Operateable;
import com.cs.gang.util.Logger;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import javassist.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * Created by IntelliJ IDEA.
 * Catalog level specification is ignored at this version, might be added latter
 * User: gang-liu
 * Date: 5/6/13
 * Time: 7:58 AM
 */
public final class Database {

    private static final int MAX_CONNECTION = 20;
    private static final int MIN_CONNECTION = 2;

    private BoneCP pool;
    private Map<String, Type> typeMap;
    private String catalog;
    private TableAccessory tableAccessory;
    private boolean hasView;
    private boolean hasStoredProcedure;

    private static final Map<String, Database> DATABASE_MAP = new HashMap<String, Database>();

    public static Database createDatabase(final String url, final String userName,
                                          final String password, final String driverClassName,
                                          final int... connCnt) throws DBParsingException, DBInitialException {
        return new Database(url, userName, password, driverClassName, connCnt);
    }

    private Database(final String url, final String userName, final String password,
                     final String driverClassName, final int... connCnt) throws DBParsingException, DBInitialException {
        if (connCnt.length != 0 && connCnt.length != 2) {
            final DBParsingException dbParsingException =
                    new DBParsingException(new IllegalArgumentException("Connection parameter should be zero or two"));
            Logger.log(Database.class, Logger.Type.ERROR, dbParsingException);
            throw dbParsingException;
        }
        try {
            Class.forName(driverClassName);
            final BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(url);
            config.setUsername(userName);
            config.setPassword(password);
            config.setPartitionCount(1);
            config.setMinConnectionsPerPartition(connCnt.length == 0 ? MIN_CONNECTION : connCnt[0]);
            config.setMaxConnectionsPerPartition(connCnt.length < 2 ? MAX_CONNECTION : connCnt[1]);
            pool = new BoneCP(config);
        } catch (final Exception e) {
            final DBParsingException dbParsingException = new DBParsingException(e);
            Logger.log(Database.class, Logger.Type.ERROR, dbParsingException);
            throw dbParsingException;
        }
        initTables();
    }

    private Database(final File configFile, final String sectionName, final String driverClassName)
            throws DBParsingException, DBInitialException {
        if (!configFile.exists()) {
            final DBParsingException exception = new DBParsingException(new FileNotFoundException(configFile.toString()));
            Logger.log(Database.class, Logger.Type.ERROR, exception);
            throw exception;
        }
        try {
            Class.forName(driverClassName);
            pool = new BoneCP(new BoneCPConfig(new FileInputStream(configFile), sectionName));
        } catch (final Exception e) {
            final DBParsingException dbParsingException = new DBParsingException(e);
            Logger.log(Database.class, Logger.Type.ERROR, dbParsingException);
            throw dbParsingException;
        }

        initTables();
    }

    private void initTables() throws DBInitialException {
        /**
         * Initialize Types, All types supported by this database is retrieved
         */
        typeMap = new HashMap<String, Type>();
        final Map<String, Table> tableMap = new HashMap<String, Table>();
        final Connection con = getConnection();
        DatabaseMetaData databaseMetaData;
        ResultSet typeResultSet = null;
        ResultSet tableResultSet = null;
        try {
            catalog = con.getCatalog();
            if (catalog == null) {
                Logger.log(Database.class, Logger.Type.ERROR, "Database " + getJdbcURL() + " has no catalog configuration");
                throw new DBInitialException("Database " + getJdbcURL() + " has no catalog configuration");
            }
            databaseMetaData = con.getMetaData();

            /**
             * Retrieves All types of the database
             */
            typeResultSet = databaseMetaData.getTypeInfo();
            while (typeResultSet.next()) {
                final String typeName = typeResultSet.getString("TYPE_NAME");
                final int typeIndex = typeResultSet.getInt("DATA_TYPE");
                final int precision = typeResultSet.getInt("PRECISION");
                final int scale = typeResultSet.getInt("MINIMUM_SCALE");
                Type.Searchable searchable = null;
                switch (typeResultSet.getShort("SEARCHABLE")) {
                    case DatabaseMetaData.typePredNone:
                        searchable = Type.Searchable.NOT_SEARCHABLE;
                        break;
                    case DatabaseMetaData.typeSearchable:
                        searchable = Type.Searchable.SEARCHABLE;
                        break;
                    case DatabaseMetaData.typePredChar:
                        searchable = Type.Searchable.TEXTURE;
                        break;
                    case DatabaseMetaData.typePredBasic:
                        searchable = Type.Searchable.NUMERIC;
                        break;
                }
                final boolean signed = !typeResultSet.getBoolean("UNSIGNED_ATTRIBUTE");
                final boolean autoIncrement = typeResultSet.getBoolean("AUTO_INCREMENT");
                final boolean caseSensitive = typeResultSet.getBoolean("CASE_SENSITIVE");
                final Type type =
                        new Type(typeName, typeIndex, precision, scale, signed, autoIncrement, searchable, caseSensitive);
                typeMap.put(typeName, type);
            }

            /**
             * Retrieves columns for each table
             * 1- for each table in database
             * 2- add columns from this table
             */
            tableResultSet = databaseMetaData.getTables(catalog, null, null, new String[]{"TABLE"});
            while (tableResultSet.next()) {
                final String tableName = tableResultSet.getString("TABLE_NAME");
                Table table = tableMap.get(tableName);
                if (table == null) {
                    table = new Table(this, tableName);
                    tableMap.put(tableName, table);
                }

                final ResultSet columnResultSet = databaseMetaData.getColumns(catalog, null, table.getName(), null);
                while (columnResultSet.next()) {
                    final Type type = typeMap.get(columnResultSet.getString("TYPE_NAME"));
                    if (type == null) {
                        final DBInitialException exception = new DBInitialException("Type " +
                                columnResultSet.getString("TYPE_NAME") + " is not found");
                        Logger.log(Database.class, Logger.Type.ERROR, exception);
                        exception.printStackTrace();
                        throw exception;
                    }

                    final String columnName = columnResultSet.getString("COLUMN_NAME");

                    Column.Nullable nullable = null;
                    switch (columnResultSet.getInt("NULLABLE")) {
                        case DatabaseMetaData.columnNoNulls:
                            nullable = Column.Nullable.NOT_NULL;
                            break;
                        case DatabaseMetaData.columnNullable:
                            nullable = Column.Nullable.NULLABLE;
                            break;
                        case DatabaseMetaData.columnNullableUnknown:
                            nullable = Column.Nullable.UN_DEFINED;
                            break;
                    }

                    final String isAutoIncrease = columnResultSet.getString("IS_AUTOINCREMENT");
                    final Column.AutoIncrement autoIncrement = isAutoIncrease.length() == 0 ?
                            Column.AutoIncrement.UN_DEFINED : isAutoIncrease.equalsIgnoreCase("YES") ?
                            Column.AutoIncrement.AUTO_INCREMENT : Column.AutoIncrement.NON_AUTO;
                    table.addColumn(new Column(columnName, type, table, nullable, autoIncrement));
                }

                columnResultSet.close();
            }

            /**
             * Parse primary key
             */
            for (final Table table : tableMap.values()) {
                final ResultSet primaryKeyResult = databaseMetaData.getPrimaryKeys(catalog, null, table.getName());
                while (primaryKeyResult.next()) {
                    final String columnName = primaryKeyResult.getString("COLUMN_NAME");
                    final Column column = table.getColumn(columnName);
                    if (column == null) {
                        final DBInitialException exception = new DBInitialException("Column " + columnName
                                + " object was not initialized in " + table.getName());
                        Logger.log(Database.class, Logger.Type.ERROR, exception);
                        throw exception;
                    }
                    table.initPrimaryKey(column, primaryKeyResult.getString("PK_NAME"));
                }
                primaryKeyResult.close();
            }

            /**
             * Parse foreign key and their references
             */
            for (final Table table : tableMap.values()) {
                final ResultSet foreignKeysResult = databaseMetaData.getImportedKeys(catalog, null, table.getName());
                while (foreignKeysResult.next()) {
                    final String referredCatalogName = foreignKeysResult.getString("PKTABLE_CAT");
                    if (!referredCatalogName.equals(catalog)) {
                        final DBInitialException exception = new DBInitialException("Reference cross multiple " +
                                "catalogs found (" + catalog + ", " + referredCatalogName + ")");
                        Logger.log(Database.class, Logger.Type.ERROR, exception);
                        exception.printStackTrace();
                        throw exception;
                    }
                    //retrieves column in this table
                    final String columnName = foreignKeysResult.getString("FKCOLUMN_NAME");
                    final Column column = table.getColumn(columnName);
                    if (columnName == null) {
                        final DBInitialException exception = new DBInitialException("Column " + columnName
                                + " object was not initialized in " + table.getName());
                        Logger.log(Database.class, Logger.Type.ERROR, exception);
                        exception.printStackTrace();
                        throw exception;
                    }

                    final String referredTableName = foreignKeysResult.getString("PKTABLE_NAME");
                    final Table referredTable = tableMap.get(referredTableName);
                    if (referredTable == null) {
                        final DBInitialException exception = new DBInitialException("Table " + referredCatalogName +
                                " is not instantiated in Database");
                        Logger.log(Database.class, Logger.Type.ERROR, exception);
                        exception.printStackTrace();
                        throw exception;
                    }
                    final String referredColumName = foreignKeysResult.getString("PKCOLUMN_NAME");
                    final PrimaryKey primaryKey = referredTable.getPrimaryKey(referredColumName);
                    if (primaryKey == null) {
                        final DBInitialException exception = new DBInitialException("Column " + referredColumName +
                                " is not instantiated in Table " + referredTable.getName());
                        Logger.log(Database.class, Logger.Type.ERROR, exception);
                        exception.printStackTrace();
                        throw exception;
                    }
                    table.initForeignKey(column, primaryKey, foreignKeysResult.getString("FK_NAME"),
                            getCascadeRule(foreignKeysResult.getInt("UPDATE_RULE")),
                            getCascadeRule(foreignKeysResult.getInt("DELETE_RULE")));
                    primaryKey.addReference(table.getForeignKey(columnName));
                }
                foreignKeysResult.close();
            }
            tableAccessory = new TableAccessory(tableMap);

        } catch (final SQLException e) {
            Logger.log(Database.class, Logger.Type.ERROR, e);
            throw new DBInitialException(e);
        } catch (final DBParsingException e) {
            Logger.log(Database.class, Logger.Type.ERROR, e);
            throw new DBInitialException(e);
        } finally {
            if (con != null) {
                try {
                    typeResultSet.close();
                    con.close();
                } catch (final Exception e) {
                    Logger.log(Database.class, Logger.Type.FATAL, e);
                }
            }
        }
    }

    private ForeignKey.CascadeRule getCascadeRule(final int type) {
        ForeignKey.CascadeRule ret = null;
        switch (type) {
            case DatabaseMetaData.importedKeyNoAction:
                ret = ForeignKey.CascadeRule.NO_ACTION;
                break;
            case DatabaseMetaData.importedKeyRestrict:
                ret = ForeignKey.CascadeRule.RESTRICT;
                break;
            case DatabaseMetaData.importedKeySetDefault:
                ret = ForeignKey.CascadeRule.SET_DEFAULT;
                break;
            case DatabaseMetaData.importedKeySetNull:
                ret = ForeignKey.CascadeRule.SET_NULL;
                break;
            case DatabaseMetaData.importedKeyCascade:
                ret = ForeignKey.CascadeRule.CASCADE;
                break;
        }
        return ret;
    }

    public Set<String> getTypeNames() {
        return typeMap.keySet();
    }

    public Type getType(final String name) throws DBParsingException {
        final Type ret = typeMap.get(name);
        if (ret == null) {
            final DBParsingException e = new DBParsingException("Type name not found " + name);
            Logger.log(Database.class, Logger.Type.ERROR, e);
            throw e;
        }
        return ret;
    }

    public String getJdbcURL() {
        return pool.getConfig().getJdbcUrl();
    }

    public Connection getConnection() throws DBInitialException {
        try {
            return pool.getConnection();
        } catch (final SQLException e) {
            final DBInitialException exception = new DBInitialException(e);
            Logger.log(Database.class, Logger.Type.ERROR, e);
            throw exception;
        }
    }

    public TableAccessory getTableAccessory(){
        return tableAccessory;
    }

    public void writeDatabaseFile(final File dir) throws DBParsingException {
        if(dir == null || !dir.isDirectory()){
            throw new DBParsingException(dir+" should be a directory in order to create database file");
        }

        try{
            final JarOutputStream writer = new JarOutputStream(new FileOutputStream(new File(dir, catalog+".jar")));
            for(final CtClass tableClasses : getTableAccessory().getClassMeta()){
                final JarEntry entry = new JarEntry(tableClasses.getName().replace('.','\\')+".class");
                writer.putNextEntry(entry);
                writer.flush();
                writer.closeEntry();
            }
            writer.close();
        } catch(final Exception ex){
            throw new DBParsingException(ex);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Database)) return false;
        Database database = (Database) o;
        return getJdbcURL().equals(database.getJdbcURL());
    }

    @Override
    public int hashCode() {
        return getJdbcURL().hashCode();
    }

    public static void main(String[] args) throws DBParsingException, DBInitialException {
        final String url = "jdbc:mysql://localhost:3306/test";
        final String username = "root";
        final String password = "admin";
        final String driverClassName = "com.mysql.jdbc.Driver";
        final Database db = new Database(url, username, password, driverClassName);
        for(final Class tabelClass : db.getTableAccessory().getTableClasses()){
            System.out.println("Package name = "+tabelClass.getPackage().getName());
            System.out.println("Table name = "+tabelClass.getCanonicalName());
            System.out.println("----------Field-------------------");
            for(final Field field : tabelClass.getDeclaredFields()){
                System.out.println("   "+Modifier.toString(field.getModifiers())+" "+field.getType()+" "+field.getName());
            }
            System.out.println("----------Methods-------------------");
            for(final Method method : tabelClass.getDeclaredMethods()){
                final StringBuilder sb = new StringBuilder("   ").append(Modifier.toString(method.getModifiers()))
                        .append(" ").append(method.getReturnType().getSimpleName()).append(" ").append(method.getName())
                        .append("(");
                for(final Class type : method.getParameterTypes()){
                    sb.append(type.getSimpleName()).append(" ");
                }
                sb.append(")");
                System.out.println(sb.toString());
            }
        }
        System.out.println("here");
        db.writeDatabaseFile(new File("/Users/develop/Java/temp"));
    }


}
