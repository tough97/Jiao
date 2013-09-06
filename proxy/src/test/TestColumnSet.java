package test;

import com.cs.gang.proxy.ColumnSet;
import com.cs.gang.proxy.excp.FieldNotFoundException;
import com.cs.gang.proxy.excp.ProxyInitException;
import com.cs.gang.proxy.excp.ProxyTypeMisMatchException;

import java.beans.BeanDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 6/3/13
 * Time: 8:57 AM
 */
public class TestColumnSet extends ColumnSet {

    private int a;
    private String id;
    private String name;
    private String surName;
    private float f;
    private double d;
    private char c;
    private java.util.Date dateOfBirth;

    /**
     * ColumnSet is used as Proxy between Database columns and Java Beans.
     * Java Bean for ColumnSet will only support the following field types
     * <p/>
     * 1 - Primitives
     * 2 - byte[] (for Blob, Clob ...etc)
     * 3 - String
     * 4 - java.sql.Date
     * 5 - java.sql.Time
     * 6 - java.sql.Timestamp
     * 7 - BigDecimal
     * 8 - BigInteger
     * 9 - URL
     * 10 - ColumnSet(one-to-one or one-to-many) queries
     * 11- List<ColumnSet> (many-to-one, many-to-many) queries
     *
     * @throws com.cs.gang.proxy.excp.ProxyTypeMisMatchException
     *
     * @throws com.cs.gang.proxy.excp.ProxyInitException
     *
     */
    public TestColumnSet() throws ProxyTypeMisMatchException, ProxyInitException {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public java.util.Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(java.util.Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public static void main(String[] args) throws ProxyTypeMisMatchException, ProxyInitException,
            FieldNotFoundException, NoSuchMethodException, NoSuchFieldException,
            InvocationTargetException, IllegalAccessException {
        final TestColumnSet testColumnSet = new TestColumnSet();
        testColumnSet.setFieldAsString("a", "1");
        System.out.println(testColumnSet.getFieldAsString("a"));
        testColumnSet.setFieldAsString("dateOfBirth", "1981-11-22");
        System.out.println(testColumnSet.getFieldAsString("dateOfBirth"));
        testColumnSet.setFieldAsString("f", "1.2");
        System.out.println(testColumnSet.getFieldAsString("f"));
        testColumnSet.setFieldAsString("c", "a");
        System.out.println(testColumnSet.getFieldAsString("c"));
//        final TestColumnSet tcs = new TestColumnSet();
//        final Field field = TestColumnSet.class.getDeclaredField("a");
//        final Method fieldSetter = Field.class.getDeclaredMethod("setInt", Object.class, int.class);
//        fieldSetter.invoke(field, tcs, 2);
//        System.out.println(Field.class.getDeclaredMethod("getInt", Object.class).invoke(field, tcs));
    }

}
