package test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 8/27/13
 * Time: 1:57 PM
 */
public class TestReflectPerformance {

    private static final int INT_VALUE = 100;
    private static final String STR_VALUE = "100";
    private static final byte[] BYTE_DATA = new byte[100];
    private static final Date DATE_DATA = new Date();

    public static final Map<String, Field> NAMED_FIELD = new HashMap<String, Field>();
    public static final Map<String, Method> FIELD_GETTER = new HashMap<String, Method>();
    public static final Map<String, Method> FIELD_SETTER = new HashMap<String, Method>();

    static {
        try {
            NAMED_FIELD.put("intField", TestReflectPerformance.class.getDeclaredField("intField"));
            NAMED_FIELD.put("strField", TestReflectPerformance.class.getDeclaredField("strField"));
            NAMED_FIELD.put("data", TestReflectPerformance.class.getDeclaredField("data"));
            NAMED_FIELD.put("date", TestReflectPerformance.class.getDeclaredField("date"));


            FIELD_GETTER.put("intField", TestReflectPerformance.class.getDeclaredMethod("getIntField"));
            FIELD_SETTER.put("intField", TestReflectPerformance.class.getDeclaredMethod("setIntField", int.class));
            FIELD_GETTER.put("strField", TestReflectPerformance.class.getDeclaredMethod("getStrField"));
            FIELD_SETTER.put("strField", TestReflectPerformance.class.getDeclaredMethod("setStrField", String.class));
            FIELD_GETTER.put("data", TestReflectPerformance.class.getDeclaredMethod("getData"));
            FIELD_SETTER.put("data", TestReflectPerformance.class.getDeclaredMethod("setData", byte[].class));
            FIELD_GETTER.put("date", TestReflectPerformance.class.getDeclaredMethod("getDate"));
            FIELD_SETTER.put("date", TestReflectPerformance.class.getDeclaredMethod("setDate", Date.class));

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private int intField;
    private String strField;
    private byte[] data;
    private Date date;

    public static Map<String, Field> getNamedField() {
        return NAMED_FIELD;
    }

    public int getIntField() {
        return intField;
    }

    public void setIntField(int intField) {
        this.intField = intField;
    }

    public String getStrField() {
        return strField;
    }

    public void setStrField(String strField) {
        this.strField = strField;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * private int intField;
     * private String strField;
     * private byte[] data;
     * private Date date;
     */

    public void setAllFieldsUsingBuiltInFieldAccess(final int iteration) {
        final long start = System.currentTimeMillis();
        for (int cnt = 0; cnt < iteration; cnt++) {
            try {
                final Field intField = this.getClass().getDeclaredField("intField");
                intField.setAccessible(true);
                intField.set(this, INT_VALUE);
                final Field strField = this.getClass().getDeclaredField("strField");
                strField.setAccessible(true);
                strField.set(this, STR_VALUE);
                final Field dataField = this.getClass().getDeclaredField("data");
                dataField.setAccessible(true);
                dataField.set(this, BYTE_DATA);
                final Field dateField = this.getClass().getDeclaredField("date");
                dateField.setAccessible(true);
                dateField.set(this, DATE_DATA);
            } catch (NoSuchFieldException e) {
            } catch (IllegalAccessException e) {
            }
        }
        final long end = System.currentTimeMillis();
        System.out.println("setAllFieldsUsingBuiltInFieldAccess uses " + (end - start) + " ms to set");
    }

    public void getAllFieldsusingBuiltInFieldAccess(final int iteration) {
        final long start = System.currentTimeMillis();
        for (int cnt = 0; cnt < iteration; cnt++) {
            try {
                final Field intField = this.getClass().getDeclaredField("intField");
                intField.setAccessible(true);
                intField.get(this);
                final Field strField = this.getClass().getDeclaredField("strField");
                strField.setAccessible(true);
                strField.get(this);
                final Field dataField = this.getClass().getDeclaredField("data");
                dataField.setAccessible(true);
                dataField.get(this);
                final Field dateField = this.getClass().getDeclaredField("date");
                dateField.setAccessible(true);
                dateField.get(this);
            } catch (NoSuchFieldException e) {
            } catch (IllegalAccessException e) {
            }
        }
        final long end = System.currentTimeMillis();
        System.out.println("getAllFieldsusingBuiltInFieldAccess uses " + (end - start) + " ms to get");
    }

    public void setAllFieldsUsingNamedMap(final int iteration) {
        final long start = System.currentTimeMillis();
        for (int cnt = 0; cnt < iteration; cnt++) {
            try {
                final Field intField = NAMED_FIELD.get("intField");
                intField.setAccessible(true);
                intField.set(this, INT_VALUE);
                final Field strField = NAMED_FIELD.get("strField");
                strField.setAccessible(true);
                strField.set(this, STR_VALUE);
                final Field dataField = NAMED_FIELD.get("data");
                dataField.setAccessible(true);
                dataField.set(this, BYTE_DATA);
                final Field dateField = NAMED_FIELD.get("date");
                dateField.setAccessible(true);
                dateField.set(this, DATE_DATA);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        final long end = System.currentTimeMillis();
        System.out.println("setAllFieldsUsingNamedMap uses " + (end - start) + " ms to set");
    }


    public void getAllFieldsUsingNamedMap(final int iterations) {
        final long start = System.currentTimeMillis();
        for (int cnt = 0; cnt < iterations; cnt++) {
            try {
                final Field intField = NAMED_FIELD.get("intField");
                intField.setAccessible(true);
                intField.get(this);
                final Field strField = NAMED_FIELD.get("strField");
                strField.setAccessible(true);
                strField.get(this);
                final Field dataField = NAMED_FIELD.get("data");
                dataField.setAccessible(true);
                dataField.get(this);
                final Field dateField = NAMED_FIELD.get("date");
                dateField.setAccessible(true);
                dateField.get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        final long end = System.currentTimeMillis();
        System.out.println("getAllFieldsUsingNamedMap uses " + (end - start) + " ms to get");
    }


    public void setAllFieldsUsingReflectedSetter(final int iteration) {
        final long start = System.currentTimeMillis();
        for (int cnt = 0; cnt < iteration; cnt++) {
            try {
                FIELD_SETTER.get("intField").invoke(this, INT_VALUE);
                FIELD_SETTER.get("strField").invoke(this, STR_VALUE);
                FIELD_SETTER.get("data").invoke(this, BYTE_DATA);
                FIELD_SETTER.get("date").invoke(this, DATE_DATA);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        final long end = System.currentTimeMillis();
        System.out.println("setAllFieldsUsingReflectedGetter uses " + (end - start) + " ms to set");
    }

    public void getAllFieldsUsingReflectedGetter(final int iteration) {
        final long start = System.currentTimeMillis();
        for (int cnt = 0; cnt < iteration; cnt++) {
            try {
                FIELD_GETTER.get("intField").invoke(this);
                FIELD_GETTER.get("strField").invoke(this);
                FIELD_GETTER.get("data").invoke(this);
                FIELD_GETTER.get("date").invoke(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        final long end = System.currentTimeMillis();
        System.out.println("setAllFieldsUsingReflectedGetter uses " + (end - start) + " ms to set");
    }

    public void setAllFieldsUsingBuildInSetter(final int iteration) {
        final long start = System.currentTimeMillis();
        for (int cnt = 0; cnt < iteration; cnt++) {
            try {
                TestReflectPerformance.class.getDeclaredMethod("setIntField", int.class).invoke(this, INT_VALUE);
                TestReflectPerformance.class.getDeclaredMethod("setStrField", String.class).invoke(this, STR_VALUE);
                TestReflectPerformance.class.getDeclaredMethod("setData", byte[].class).invoke(this, BYTE_DATA);
                TestReflectPerformance.class.getDeclaredMethod("setDate", Date.class).invoke(this, DATE_DATA);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        final long end = System.currentTimeMillis();
        System.out.println("setAllFieldsUsingBuildInGetter uses " + (end - start) + " ms to set");
    }

    public void getAllFieldsUsingBuildInGetter(final int iteration) {
        final long start = System.currentTimeMillis();
        for (int cnt = 0; cnt < iteration; cnt++) {
            try {
                TestReflectPerformance.class.getDeclaredMethod("getStrField").invoke(this);
                TestReflectPerformance.class.getDeclaredMethod("getIntField").invoke(this);
                TestReflectPerformance.class.getDeclaredMethod("getData").invoke(this);
                TestReflectPerformance.class.getDeclaredMethod("getDate").invoke(this);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        final long end = System.currentTimeMillis();
        System.out.println("getAllFieldsUsingBuildInGetter uses " + (end - start) + " ms to get");
    }

    public static void main(String[] args) {
        final int I = 10000;
        TestReflectPerformance t = new TestReflectPerformance();
        t.getAllFieldsUsingBuildInGetter(I);
        t.setAllFieldsUsingBuildInSetter(I);
        System.out.println("---------");
        t.getAllFieldsusingBuiltInFieldAccess(I);
        t.setAllFieldsUsingBuiltInFieldAccess(I);
        System.out.println("-----------");
        t.getAllFieldsUsingNamedMap(I);
        t.setAllFieldsUsingNamedMap(I);
        System.out.println("-----------");
        t.getAllFieldsUsingReflectedGetter(I);
        t.setAllFieldsUsingReflectedSetter(I);
    }

}
