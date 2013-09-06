package com.cs.gang.db;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/6/13
 * Time: 10:09 AM
 */
public class Column {

    public static enum AutoIncrement{
        AUTO_INCREMENT, NON_AUTO, UN_DEFINED;
    };

    public static enum Nullable{
        NOT_NULL, NULLABLE, UN_DEFINED;
    }

    private String name;
    private Type type;
    private Table table;
    private Nullable nullable;
    private AutoIncrement autoIncrement;

    Column(final String name, final Type type, final Table table,
           final Nullable nullable, final AutoIncrement autoIncrement) {
        this.name = name;
        this.type = type;
        this.table = table;
        this.nullable = nullable;
        this.autoIncrement = autoIncrement;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Table getTable() {
        return table;
    }

    public Nullable getNullable() {
        return nullable;
    }

    public AutoIncrement getAutoIncrement() {
        return autoIncrement;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Column)) return false;

        final Column column = (Column) o;

        if (!name.equals(column.name)) return false;
        if (!table.equals(column.table)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + table.hashCode();
        return result;
    }
}
