package com.cs.gang.proxy;

import com.cs.gang.proxy.excp.FieldNotFoundException;
import com.cs.gang.proxy.excp.ProxyTypeMisMatchException;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
 * @throws com.cs.gang.proxy.excp.ProxyInitException
 * User: gang-liu
 * Date: 5/8/13
 * Time: 10:32 AM
 */
public final class ColumnSetAccessory {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final Map<Class<? extends ColumnSet>, ColumnSetAccessory>
            COLUMN_SET_ACCESSSORY_MAP = new HashMap<Class<? extends ColumnSet>, ColumnSetAccessory>();

    private static final Set<Class> SUPPORTED_CLASSES = new HashSet<Class>();
    private static final Set<Class> SUPPORTED_ASSIGNABLE_CALSS = new HashSet<Class>();
    private static final Map<Class, Method> FIELD_VALUE_TO_STR = new HashMap<Class, Method>();
    private static final Map<Class, Method> STR_TO_FIELD_VALUE = new HashMap<Class, Method>();

    static {
        SUPPORTED_CLASSES.add(int.class);
        SUPPORTED_CLASSES.add(Integer.class);
        SUPPORTED_CLASSES.add(float.class);
        SUPPORTED_CLASSES.add(Float.class);
        SUPPORTED_CLASSES.add(double.class);
        SUPPORTED_CLASSES.add(Double.class);
        SUPPORTED_CLASSES.add(byte.class);
        SUPPORTED_CLASSES.add(Byte.class);
        SUPPORTED_CLASSES.add(long.class);
        SUPPORTED_CLASSES.add(Long.class);
        SUPPORTED_CLASSES.add(short.class);
        SUPPORTED_CLASSES.add(Short.class);
        SUPPORTED_CLASSES.add(boolean.class);
        SUPPORTED_CLASSES.add(Boolean.class);

        SUPPORTED_CLASSES.add(char.class);
        SUPPORTED_CLASSES.add(Character.class);
        SUPPORTED_CLASSES.add(String.class);
        SUPPORTED_CLASSES.add(BigDecimal.class);
        SUPPORTED_CLASSES.add(BigInteger.class);
        SUPPORTED_CLASSES.add(URL.class);

        SUPPORTED_ASSIGNABLE_CALSS.add(Date.class);
        SUPPORTED_ASSIGNABLE_CALSS.add(ColumnSet.class);
        SUPPORTED_ASSIGNABLE_CALSS.add(List.class);
        SUPPORTED_ASSIGNABLE_CALSS.add(Set.class);

        try {

            FIELD_VALUE_TO_STR.put(int.class, String.class.getDeclaredMethod("valueOf", int.class));
            STR_TO_FIELD_VALUE.put(int.class, Integer.class.getDeclaredMethod("parseInt", String.class));
            FIELD_VALUE_TO_STR.put(Integer.class, String.class.getDeclaredMethod("valueOf", int.class));
            STR_TO_FIELD_VALUE.put(Integer.class, Integer.class.getDeclaredMethod("parseInt", String.class));
            FIELD_VALUE_TO_STR.put(float.class, String.class.getDeclaredMethod("valueOf", float.class));
            STR_TO_FIELD_VALUE.put(float.class, Float.class.getDeclaredMethod("parseFloat", String.class));
            FIELD_VALUE_TO_STR.put(Float.class, String.class.getDeclaredMethod("valueOf", float.class));
            STR_TO_FIELD_VALUE.put(Float.class, Float.class.getDeclaredMethod("parseFloat", String.class));
            FIELD_VALUE_TO_STR.put(double.class, String.class.getDeclaredMethod("valueOf", double.class));
            STR_TO_FIELD_VALUE.put(double.class, Double.class.getDeclaredMethod("parseDouble", String.class));
            FIELD_VALUE_TO_STR.put(Double.class, String.class.getDeclaredMethod("valueOf", double.class));
            STR_TO_FIELD_VALUE.put(Double.class, Double.class.getDeclaredMethod("parseDouble", String.class));
            FIELD_VALUE_TO_STR.put(byte.class, Byte.class.getDeclaredMethod("valueOf", byte.class));
            STR_TO_FIELD_VALUE.put(byte.class, Byte.class.getDeclaredMethod("parseByte", String.class));
            FIELD_VALUE_TO_STR.put(Byte.class, Byte.class.getDeclaredMethod("valueOf", byte.class));
            STR_TO_FIELD_VALUE.put(Byte.class, Byte.class.getDeclaredMethod("parseByte", String.class));
            FIELD_VALUE_TO_STR.put(long.class, String.class.getDeclaredMethod("valueOf", long.class));
            STR_TO_FIELD_VALUE.put(long.class, Long.class.getDeclaredMethod("parseLong", String.class));
            FIELD_VALUE_TO_STR.put(Long.class, String.class.getDeclaredMethod("valueOf", long.class));
            STR_TO_FIELD_VALUE.put(Long.class, Long.class.getDeclaredMethod("parseLong", String.class));
            FIELD_VALUE_TO_STR.put(short.class, Short.class.getDeclaredMethod("valueOf", short.class));
            STR_TO_FIELD_VALUE.put(short.class, Short.class.getDeclaredMethod("parseShort", String.class));
            FIELD_VALUE_TO_STR.put(Short.class, Short.class.getDeclaredMethod("valueOf", short.class));
            STR_TO_FIELD_VALUE.put(Short.class, Short.class.getDeclaredMethod("parseShort", String.class));
            FIELD_VALUE_TO_STR.put(boolean.class, String.class.getDeclaredMethod("valueOf", boolean.class));
            STR_TO_FIELD_VALUE.put(boolean.class, Boolean.class.getDeclaredMethod("parseBoolean", String.class));
            FIELD_VALUE_TO_STR.put(Boolean.class, String.class.getDeclaredMethod("valueOf", boolean.class));
            STR_TO_FIELD_VALUE.put(Boolean.class, Boolean.class.getDeclaredMethod("parseBoolean", String.class));
            FIELD_VALUE_TO_STR.put(char.class, String.class.getDeclaredMethod("valueOf", char.class));
            STR_TO_FIELD_VALUE.put(char.class, ColumnSetAccessory.class.getDeclaredMethod("parseChar", String.class));

            FIELD_VALUE_TO_STR.put(Character.class, String.class.getDeclaredMethod("valueOf", char.class));
            STR_TO_FIELD_VALUE.put(Character.class, ColumnSetAccessory.class.getDeclaredMethod("parseChar", String.class));
            FIELD_VALUE_TO_STR.put(BigDecimal.class, ColumnSetAccessory.class.getDeclaredMethod("valueOf", BigDecimal.class));
            STR_TO_FIELD_VALUE.put(BigDecimal.class, ColumnSetAccessory.class.getDeclaredMethod("parseBigDecimal", String.class));
            FIELD_VALUE_TO_STR.put(BigInteger.class, ColumnSetAccessory.class.getDeclaredMethod("valueOf", BigInteger.class));
            STR_TO_FIELD_VALUE.put(BigInteger.class, ColumnSetAccessory.class.getDeclaredMethod("parseBigInteger", String.class));
            FIELD_VALUE_TO_STR.put(URL.class, ColumnSetAccessory.class.getDeclaredMethod("parseURL", String.class));
            STR_TO_FIELD_VALUE.put(URL.class, ColumnSetAccessory.class.getDeclaredMethod("valueOf", URL.class));
            FIELD_VALUE_TO_STR.put(java.util.Date.class, ColumnSetAccessory.class.getDeclaredMethod("valueOf", Object[].class));
            STR_TO_FIELD_VALUE.put(java.util.Date.class, ColumnSetAccessory.class.getDeclaredMethod("parseDate", String[].class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /*
        SUPPORTED_ASSIGNABLE_CALSS.add(Date.class);
     */
    private static URL parseURL(final String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private static String valueOf(final URL url) {
        return url.toString();
    }

    private static char parseChar(final String value) {
        if (value == null || value.length() == 0) {
            return "".charAt(0);
        } else {
            return value.charAt(0);
        }
    }

    private static String valueOf(final char character) {
        return String.valueOf(character);
    }

    private static BigDecimal parseBigDecimal(final String decimal) {
        return new BigDecimal(decimal);
    }

    private static String valueOf(final BigDecimal bigDecimal) {
        return bigDecimal == null ? "0.0" : bigDecimal.toString();
    }

    private static BigInteger parseBigInteger(final String integer) {
        return new BigInteger(integer);
    }

    private static String valueOf(final BigInteger bigInteger) {
        return bigInteger == null ? "0" : bigInteger.toString();
    }

    private static Date parseDate(final String... dateFormat) {
        try {
            return new SimpleDateFormat(dateFormat.length == 2 ? dateFormat[1] : DEFAULT_DATE_FORMAT).parse(dateFormat[0]);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String valueOf(final Object... data){
        if(data.length == 1 && Date.class.isAssignableFrom(data[0].getClass())){
            return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(data[0]);
        } else if(data.length == 2 && Date.class.isAssignableFrom(data[0].getClass()) && data[1] instanceof String){
            return new SimpleDateFormat((String) data[1]).format(data[0]);
        } else {
            return "";
        }
    }

    private static String valueOf(final Date date, final String... format) {
        return new SimpleDateFormat(format.length == 0 ? DEFAULT_DATE_FORMAT : format[0]).format(date);
    }

    private transient Class<? extends ColumnSet> columnSetTarget;
    private transient Map<String, Field> fieldNameMap;

    private ColumnSetAccessory(final Class<? extends ColumnSet> columnSetTarget) throws ProxyTypeMisMatchException {
        this.columnSetTarget = columnSetTarget;
        fieldNameMap = new HashMap<String, Field>();
        for (final Field field : columnSetTarget.getDeclaredFields()) {
            parseField(field);
        }
    }

    private void parseField(final Field field) throws ProxyTypeMisMatchException {
        if(Modifier.isPublic(field.getModifiers())
                || Modifier.isStatic(field.getModifiers())
                || Modifier.isTransient(field.getModifiers())){
            return;
        }
        if (!field.getDeclaringClass().equals(columnSetTarget)) {
            throw new ProxyTypeMisMatchException("Field " + field.getName() + " is not declared by " + columnSetTarget);
        }
        if (!SUPPORTED_CLASSES.contains(field.getType())) {
            for (final Class clazz : SUPPORTED_ASSIGNABLE_CALSS) {
                if (clazz.isAssignableFrom(field.getType())) {
                    fieldNameMap.put(field.getName(), field);
                    return;
                }
            }
            throw new ProxyTypeMisMatchException("Type " + field.getType().getName() + " is not supported");
        } else {
            fieldNameMap.put(field.getName(), field);
        }
    }

    private String getFieldFormat(final Field field) throws ProxyTypeMisMatchException {
        if (!field.getDeclaringClass().equals(columnSetTarget)) {
            throw new ProxyTypeMisMatchException("Field " + field.getName() + " is not declared by " + columnSetTarget.getName());
        }
        final com.cs.gang.proxy.anno.Column column = field.getAnnotation(com.cs.gang.proxy.anno.Column.class);
        return column == null ? DEFAULT_DATE_FORMAT : column.format().length() == 0 ? DEFAULT_DATE_FORMAT : column.format();
    }

    /**
     * Protected method
     */
    protected static ColumnSetAccessory getColumnAccessory(final Class<? extends ColumnSet> claz) throws ProxyTypeMisMatchException {
        if (claz == null) {
            throw new ProxyTypeMisMatchException("Column Set target can not be null");
        }
        ColumnSetAccessory accessory = COLUMN_SET_ACCESSSORY_MAP.get(claz);
        if (accessory == null) {
            accessory = new ColumnSetAccessory(claz);
            COLUMN_SET_ACCESSSORY_MAP.put(claz, accessory);
        }
        return accessory;
    }

    protected Class<? extends ColumnSet> getColumnSetTarget() {
        return columnSetTarget;
    }

    protected Set<String> getColumnNames() {
        return fieldNameMap.keySet();
    }

    protected String getFieldValueAsString(final String fieldName, final ColumnSet cs)
            throws ProxyTypeMisMatchException, FieldNotFoundException {
        final Field field = fieldNameMap.get(fieldName);
        if (field == null) {
            throw new FieldNotFoundException("Field " + fieldName + " is not defined in " + columnSetTarget.getName());
        }
        final Class fieldType = field.getType();
        final Method convertor = FIELD_VALUE_TO_STR.get(fieldType);
        if (convertor == null) {
            throw new ProxyTypeMisMatchException("Field " + field.getName() + " is not convertable to String");
        }
        field.setAccessible(true);
        try {
            return (String) convertor.invoke(null, Date.class.isAssignableFrom(fieldType) ?
                    new Object[]{field.get(cs), getFieldFormat(field)} : field.get(cs));
        } catch (final Exception e) {
            throw new ProxyTypeMisMatchException(e);
        }
    }

    protected void setFieldValueAsString(final String fieldName, final String value, final ColumnSet cs)
            throws ProxyTypeMisMatchException, FieldNotFoundException {
        final Field field = fieldNameMap.get(fieldName);
        if (field == null) {
            throw new FieldNotFoundException("Field " + fieldName + " is not defined in " + columnSetTarget.getName());
        }
        final Class fieldType = field.getType();
        final Method convertor = STR_TO_FIELD_VALUE.get(fieldType);
        if (convertor == null) {
            throw new ProxyTypeMisMatchException("Field " + field.getName() + " is not assignable from String");
        }
        field.setAccessible(true);
        try {
            final Object convertedValue = convertor.invoke(null, Date.class.isAssignableFrom(fieldType)
                    ? new String[]{value, getFieldFormat(field)} : value);
            field.set(cs, convertedValue);
        } catch (final Exception e) {
            throw new ProxyTypeMisMatchException(e);
        }
    }


}
