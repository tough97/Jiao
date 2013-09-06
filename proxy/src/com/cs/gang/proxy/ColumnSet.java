package com.cs.gang.proxy;

import com.cs.gang.proxy.excp.FieldNotFoundException;
import com.cs.gang.proxy.excp.ProxyInitException;
import com.cs.gang.proxy.excp.ProxyTypeMisMatchException;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * ColumnSet is used as Proxy between Database columns and Java Beans.
 * Java Bean for ColumnSet will only support the following field types
 * <p/>
 * for one-to-one relations, all fields should be included in this class
 * for one-to-many relations, this class should contain a List or Set of
 * <p/>
 * <p/>
 * 1  - Primitives
 * 2  - byte[] (for Blob, Clob ...etc)
 * 3  - String
 * 4  - java.util.Date
 * 5  - BigDecimal
 * 6  - BigInteger
 * 7  - URL
 * 8  - Other ColumnSet
 * 9  - ColumnSet(one-to-one or one-to-many) queries
 * 10 - List<ColumnSet> (many-to-one, many-to-many) queries
 *
 * @throws ProxyTypeMisMatchException
 * @throws ProxyInitException
 * User: gang-liu
 * Date: 5/8/13
 * Time: 10:32 AM
 */
public abstract class ColumnSet implements Serializable {

    private transient ColumnSetAccessory columnSetAccessories;

    protected ColumnSet() throws ProxyTypeMisMatchException{
        columnSetAccessories = ColumnSetAccessory.getColumnAccessory(this.getClass());
    }

    public String getFieldAsString(final String name)
            throws ProxyTypeMisMatchException, FieldNotFoundException {
        return columnSetAccessories.getFieldValueAsString(name, this);
    }

    public void setFieldAsString(final String fieldName, final String value)
            throws ProxyTypeMisMatchException, FieldNotFoundException {
        columnSetAccessories.setFieldValueAsString(fieldName, value, this);
    }


}
