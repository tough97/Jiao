package com.cs.gang.db;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/6/13
 * Time: 2:26 PM
 */
public final class ForeignKey extends Column{

    public static enum CascadeRule{
        NO_ACTION, CASCADE, SET_NULL, SET_DEFAULT, RESTRICT
    };

    private PrimaryKey primaryKey;
    private CascadeRule updateRule;
    private CascadeRule deleteRule;
    private String foreignKeyName;

    ForeignKey(final Column column, final PrimaryKey primaryKey, final String foreignKeyName,
               final CascadeRule updateRule, final CascadeRule deleteRule) {
        super(column.getName(), column.getType(), column.getTable(),
                column.getNullable(), column.getAutoIncrement());
        this.primaryKey = primaryKey;
        this.updateRule = updateRule;
        this.deleteRule = deleteRule;
        this.foreignKeyName = foreignKeyName;
    }

    void setPrimaryKey(final PrimaryKey primaryKey){
        this.primaryKey = primaryKey;
    }

    boolean sameColumn(final Column column){
        return column.getTable().equals(getTable())
                && column.getName().equals(getName());
    }

    public String getForeignKeyName(){
        return foreignKeyName == null ? getName() : foreignKeyName;
    }


}
