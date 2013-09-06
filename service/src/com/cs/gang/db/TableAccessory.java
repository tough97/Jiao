package com.cs.gang.db;

import com.cs.gang.db.excp.DBInitialException;
import com.cs.gang.db.excp.TableNotFoundException;
import com.cs.gang.proxy.Operateable;
import javassist.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to create
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/30/13
 * Time: 9:18 AM
 */
public final class TableAccessory {

    private static final ClassPool CLASS_POOL = ClassPool.getDefault();
    private static CtClass OPERATABLE_MODEL;

    static {
        try {
            OPERATABLE_MODEL = CLASS_POOL.get(Operateable.class.getCanonicalName());
        } catch (final NotFoundException e) {
            e.printStackTrace();
        }
    }

    private Map<Class<? extends Operateable>, Table> classTableMap;
    private Map<Table, Class<? extends Operateable>> tableClassMap;
    private Map<String, Table> tableNameMap;
    private Map<Class<? extends Operateable>, String> insertStatement;
    private Set<CtClass> classMeta;

    TableAccessory(final Map<String, Table> tableNameMap) throws DBInitialException {
        this.tableNameMap = tableNameMap;
        classTableMap = new HashMap<Class<? extends Operateable>, Table>();
        tableClassMap = new HashMap<Table, Class<? extends Operateable>>();
        insertStatement = new HashMap<Class<? extends Operateable>, String>();
        classMeta = new HashSet<CtClass>();
        for(final Table table : tableNameMap.values()){
            parseTable(table);
        }
    }

    private void parseTable(final Table table) throws DBInitialException {
        final CtClass tableModelClass = CLASS_POOL.
                makeClass("com.cs.gang.proxy." + toClassName(table.getName()), OPERATABLE_MODEL);
        try {
            for (final Column column : table.getColumns()) {
                final String memberName = toClassName(column.getName());
                final CtClass type = column.getType().getCtType();

                //Create Name flag
                final CtField fieldFlag = new CtField(ClassPool.getDefault()
                        .getCtClass(String.class.getCanonicalName()), memberName.toUpperCase(), tableModelClass);
                fieldFlag.setModifiers(Modifier.PUBLIC|Modifier.STATIC|Modifier.FINAL);
                tableModelClass.addField(fieldFlag, CtField.Initializer.constant(memberName));

                //Create Field
                final CtField field = new CtField(type, decapitalize(memberName), tableModelClass);
                field.setModifiers(Modifier.PRIVATE);
                tableModelClass.addField(field);

                //Create Getter
                final CtMethod getter = CtNewMethod.getter(
                        type.equals(CtClass.booleanType) ? "is" : "get" + memberName, field);
                getter.setModifiers(Modifier.PUBLIC);
                final CtMethod setter = CtNewMethod.setter("set" + memberName, field);
                setter.setModifiers(Modifier.PUBLIC);
                tableModelClass.addMethod(getter);
                tableModelClass.addMethod(setter);
            }
            final Class<? extends Operateable> tableClass = tableModelClass.toClass();
            tableClassMap.put(table, tableClass);
            classTableMap.put(tableClass, table);
            classMeta.add(tableModelClass);
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

    Set<CtClass> getClassMeta(){
        return classMeta;
    }

    public Class<? extends Operateable> getTableClass(final String tableName) throws TableNotFoundException{
        final Class<? extends Operateable> result = tableClassMap.get(tableNameMap.get(tableName));
        if(result == null){
            throw new TableNotFoundException("table "+tableName+" not found");
        }
        return result;
    }

    public Class<? extends Operateable> getTableClass(final Table table) throws TableNotFoundException{
        final Class<? extends Operateable> result = tableClassMap.get(table);
        if(result == null){
            throw new TableNotFoundException("table "+table.getName()+" not found");
        }
        return result;
    }

    public Set<String> getTableNames(){
        return tableNameMap.keySet();
    }

    public Set<Class<? extends Operateable>> getTableClasses(){
        return classTableMap.keySet();
    }

    public Set<Table> getTables(){
        return tableClassMap.keySet();
    }

}
