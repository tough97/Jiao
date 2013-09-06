package com.cs.gang.db;

import com.cs.gang.db.excp.ColumnNotFoundException;
import com.cs.gang.db.excp.DBInitialException;
import com.cs.gang.proxy.Operateable;
import com.cs.gang.util.Logger;
import javassist.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/6/13
 * Time: 9:54 AM
 */
public final class Table {

    private static final ClassPool CLASS_POOL = ClassPool.getDefault();
    private static CtClass OPERATABLE_MODEL;

    static {
        try {
            OPERATABLE_MODEL = CLASS_POOL.get(Operateable.class.getCanonicalName());
        } catch (final NotFoundException e) {
            e.printStackTrace();
        }
    }
    private Database database;
    private String name;
    private Map<String, Column> columnMap;
    private Map<String, PrimaryKey> primaryKeyMap;
    private Map<String, ForeignKey> foreignKeyMap;
    private Map<Column, Integer> columnPositionMap;
    private Class<? extends Operateable> tableClass = null;

    Table(final Database database, final String name) {
        this.database = database;
        this.name = name;
        columnMap = new HashMap<String, Column>();
        columnPositionMap = new HashMap<Column, Integer>();
    }

    void addColumn(final Column column) throws DBInitialException{
        columnMap.put(column.getName(), column);
        columnPositionMap.put(column, new Integer(columnPositionMap.size()));
    }

    void initPrimaryKey(final Column column, final String primaryKeyName) throws DBInitialException {
        if (primaryKeyMap == null) {
            primaryKeyMap = new HashMap<String, PrimaryKey>();
        }
        if (!column.getTable().equals(this)) {
            final DBInitialException exception = new DBInitialException("Column " + column.getName() +
                    " does not belong to table " + getName());
            Logger.log(Table.class, Logger.Type.ERROR, exception);
            throw exception;
        }
        if (!columnMap.containsValue(column)) {
            final DBInitialException exception = new DBInitialException("Column " + column.getName() +
                    " was not previously defined in table " + getName());
            Logger.log(Table.class, Logger.Type.ERROR, exception);
            throw exception;
        }
        primaryKeyMap.put(column.getName(), new PrimaryKey(column, primaryKeyName));
    }

    /**
     * This method has to be called after primary key formation is done
     */
    void initForeignKey(final Column column, final PrimaryKey reference, final String foreignKeyName,
                        final ForeignKey.CascadeRule updateRule, final ForeignKey.CascadeRule deleteRule)
            throws DBInitialException {
        if (foreignKeyMap == null) {
            foreignKeyMap = new HashMap<String, ForeignKey>();
        }
        if (!column.getTable().equals(this)) {
            final DBInitialException exception = new DBInitialException("Column " + column.getName() +
                    " does not belong to table " + getName());
            Logger.log(Table.class, Logger.Type.ERROR, exception);
            throw exception;
        }
        if (!columnMap.containsValue(column)) {
            final DBInitialException exception = new DBInitialException("Column " + column.getName() +
                    " was not previously defined in table " + getName());
            Logger.log(Table.class, Logger.Type.ERROR, exception);
            throw exception;
        }
        foreignKeyMap.put(column.getName(), new ForeignKey(column, reference, foreignKeyName, updateRule, deleteRule));
    }

    boolean isPrimarykeyInitialized() {
        return primaryKeyMap != null;
    }

    boolean isForeignKeyInitialized() {
        return foreignKeyMap != null;
    }

    /**
     * ---------------------pblic methods------------------------------------------------------------------------------
     * @return
     */

    public int getColumnIndex(final Column column) throws ColumnNotFoundException{
        if(!columnPositionMap.containsKey(column)){
            throw new ColumnNotFoundException("Column "+column+" does not belong to table "+ name);
        }
        return columnPositionMap.get(column).intValue();
    }

    public int getPrimaryKeyCount() {
        return primaryKeyMap.size();
    }

    public int getForeignKeyCount() {
        return foreignKeyMap.size();
    }

    public Column getColumn(final String name) {
        return columnMap.get(name);
    }

    public Set<String> getColumnNames() {
        return columnMap.keySet();
    }

    public Collection<Column> getColumns() {
        return columnMap.values();
    }

    public PrimaryKey getPrimaryKey(final String name) {
        return primaryKeyMap.get(name);
    }

    public ForeignKey getForeignKey(final String name) {
        return foreignKeyMap.get(name);
    }

    public int getColumnCount() {
        return columnMap.size();
    }

    public Database getDatabase() {
        return database;
    }

    public String getName() {
        return name;
    }

    public Class getTableClass() throws DBInitialException {
        if(tableClass == null){
            parseTable();
        }
        return tableClass;
    }

    private void parseTable() throws DBInitialException {
        final CtClass tableModelClass = CLASS_POOL.
                makeClass("com.cs.gang.proxy." + toClassName(getName()), OPERATABLE_MODEL);
        try {
            CtNewConstructor.defaultConstructor(tableModelClass);

            for (final Column column : getColumns()) {
                final String memberName = toClassName(column.getName());
                final CtClass type = column.getType().getCtType();
                //Create Field
                final CtField field = new CtField(type, decapitalize(memberName), tableModelClass);
                field.setModifiers(Modifier.PRIVATE);
                tableModelClass.addField(field);

                //Create static filed flag
                final CtField fieldFlag = new CtField(CtClass.intType, memberName.toUpperCase(), tableModelClass);
                fieldFlag.setModifiers(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC);
                tableModelClass.addField(fieldFlag, CtField.Initializer.byExpr(field.getName()));


                //Create Getter
                final CtMethod getter = CtNewMethod.getter(
                        type.equals(CtClass.booleanType) ? "is" : "get" + memberName, field);
                getter.setModifiers(Modifier.PUBLIC);
                //Create setter
                final CtMethod setter = CtNewMethod.setter("set" + memberName, field);
                setter.setModifiers(Modifier.PUBLIC);
                tableModelClass.addMethod(getter);
                tableModelClass.addMethod(setter);
            }
            tableClass = tableModelClass.toClass();
        } catch (final Exception ex) {
            throw new DBInitialException(ex);
        }
    }

    private String decapitalize(final String data) {
        final char[] dataArr = data.toCharArray();
        if (Character.isUpperCase(dataArr[0])) {
            dataArr[0] = Character.toLowerCase(dataArr[0]);
        }
        return new String(dataArr);
    }

    private String toClassName(final String tableName) {
        final char[] originalTableName = tableName.toCharArray();
        final StringBuilder nameBuilder = new StringBuilder();
        for (int index = 0; index < originalTableName.length; index++) {
            final char currentChar = originalTableName[index];
            if (Character.isLetter(currentChar)) {
                if (index == 0 || (!Character.isLetter(originalTableName[index - 1]))) {
                    nameBuilder.append(Character.isLowerCase(originalTableName[index]) ?
                            Character.toUpperCase(originalTableName[index]) : originalTableName[index]);
                } else {
                    nameBuilder.append(Character.isUpperCase(originalTableName[index]) ?
                            Character.toLowerCase(originalTableName[index]) : originalTableName[index]);
                }
            }
        }
        return nameBuilder.toString();
    }

    /**
     * Retrieves those table who this table's foreign keys referres to
     *
     * @return
     */
    public Set<Table> getLinkedToTables() {
        final Set<Table> tables = new HashSet<Table>();
        for (final ForeignKey foreignKey : foreignKeyMap.values()) {
            tables.add(foreignKey.getTable());
        }
        return tables;
    }

    public Set<Table> getLinkedInTables() {
        final Set<Table> tables = new HashSet<Table>();
        for (final PrimaryKey primaryKey : primaryKeyMap.values()) {
            for (final ForeignKey foreignKey : primaryKey.getReferences()) {
                tables.add(foreignKey.getTable());
            }
        }
        return tables;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Table)) return false;

        Table table = (Table) o;

        if (!database.equals(table.database)) return false;
        if (!name.equals(table.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = database.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
