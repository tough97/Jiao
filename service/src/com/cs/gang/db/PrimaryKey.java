package com.cs.gang.db;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/6/13
 * Time: 2:59 PM
 */
public final class PrimaryKey extends Column{

    private String pkName;
    private Set<ForeignKey> references;

    PrimaryKey(final Column column, final String pkName) {
        super(column.getName(), column.getType(), column.getTable()
                , column.getNullable(), column.getAutoIncrement());
        references = new HashSet<ForeignKey>();
        this.pkName = pkName;
    }

    void addReference(final ForeignKey foreignKey){
        references.add(foreignKey);
    }

    boolean sameColumn(final Column column){
        return column.getTable().equals(getTable())
                && column.getName().equals(getName());
    }


    public String getPrimaryKeyName() {
        return pkName == null ? getName() : pkName;
    }

    public Set<ForeignKey> getReferences() {
        return references;
    }
}
