package com.cs.gang.db;

import com.cs.gang.db.excp.DBParsingException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/6/13
 * Time: 10:11 AM
 */
public final class Type {

    /**
     * This default mapping map is defined according to JDBC specification
     * http://docs.oracle.com/javase/1.4.2/docs/guide/jdbc/getstart/mapping.html
     * further customizations should be possible
     */
    public static final Map<Integer, Class> DEFAULT_TYPE_MAPPING = new HashMap<Integer, Class>();

    static {
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.CHAR), String.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.VARCHAR), String.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.LONGVARCHAR), String.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.NUMERIC), java.math.BigDecimal.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.DECIMAL), java.math.BigDecimal.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.BIT), boolean.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.BOOLEAN), boolean.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.TINYINT), byte.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.SMALLINT), short.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.INTEGER), int.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.BIGINT), long.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.REAL), float.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.FLOAT), double.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.DOUBLE), double.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.BINARY), byte[].class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.VARBINARY), byte[].class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.LONGVARBINARY), byte[].class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.DATE), Date.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.TIME), java.sql.Time.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.TIMESTAMP), Timestamp.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.CLOB), Clob.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.BLOB), Blob.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.ARRAY), null);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.DISTINCT), null);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.STRUCT), null);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.REF), null);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.DATALINK), java.net.URL.class);
        DEFAULT_TYPE_MAPPING.put(new Integer(Types.JAVA_OBJECT), null);
    }

    public static final Map<Class, CtClass> PRIMITIVE_TYPES = new HashMap<Class, CtClass>();
    static{
        PRIMITIVE_TYPES.put(int.class, CtClass.intType);
        PRIMITIVE_TYPES.put(double.class, CtClass.doubleType);
        PRIMITIVE_TYPES.put(float.class, CtClass.floatType);
        PRIMITIVE_TYPES.put(long.class, CtClass.longType);
        PRIMITIVE_TYPES.put(short.class, CtClass.shortType);
        PRIMITIVE_TYPES.put(byte.class, CtClass.byteType);
        PRIMITIVE_TYPES.put(boolean.class, CtClass.booleanType);
        PRIMITIVE_TYPES.put(char.class, CtClass.charType);
    }

    private static final Map<Class, Method> BYTE_CONVERTERS = new HashMap<Class, Method>();
    private static final Map<Class, Method> TYPE_CONVERTERS = new HashMap<Class, Method>();
    private static final Set<Class> CONFERABLE_TYPES = new HashSet<Class>();
    static{
        CONFERABLE_TYPES.add(int.class); CONFERABLE_TYPES.add(float.class);
        CONFERABLE_TYPES.add(double.class); CONFERABLE_TYPES.add(char.class);
        CONFERABLE_TYPES.add(byte.class); CONFERABLE_TYPES.add(short.class);
        CONFERABLE_TYPES.add(long.class); CONFERABLE_TYPES.add(boolean.class);
        CONFERABLE_TYPES.add(String.class); CONFERABLE_TYPES.add(byte[].class);
        try {
            for(final Class typeClass : CONFERABLE_TYPES){
                BYTE_CONVERTERS.put(typeClass, getByteConverter(typeClass));
                TYPE_CONVERTERS.put(typeClass, getTypeConverter(typeClass));
            }
        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static Method getByteConverter(final Class parameterType) throws NoSuchMethodException {
        return Type.class.getDeclaredMethod("toByteArray", parameterType);
    }

    private static Method getTypeConverter(final Class type) throws NoSuchMethodException {
        String methodName;
        if(type.isPrimitive()){
            final char[] typeName = type.getName().toCharArray();
            typeName[0] -= 32;
            methodName = "to" + new String(typeName);
        } else if(type.isArray()){
            methodName = "toByteA";
        } else if(type.equals(String.class)){
            methodName = "toString";
        } else {
            throw new NoSuchMethodException(type.getName());
        }
        return Type.class.getDeclaredMethod(methodName, byte[].class);
    }

    public static enum Searchable {
        NOT_SEARCHABLE, TEXTURE,
        NUMERIC, SEARCHABLE;
    }

    ;

    private String typeName;
    private int typeIndex;
    private int precision;
    private int scale;
    private boolean signed;
    private boolean autoIncremented;
    private Searchable searchable;
    private boolean caseSensitive;
    private Class mappedClass;

    public Type(final String typeName, final int typeIndex, final int precision,
                final int scale, final boolean signed, final boolean autoIncremented,
                final Searchable searchable, final boolean caseSensitive) throws DBParsingException {
        this.typeName = typeName;
        this.typeIndex = typeIndex;
        this.precision = precision;
        this.scale = scale;
        this.signed = signed;
        this.autoIncremented = autoIncremented;
        this.searchable = searchable;
        this.caseSensitive = caseSensitive;
        mappedClass = DEFAULT_TYPE_MAPPING.get(new Integer(typeIndex));
        if (mappedClass == null) {
            throw new DBParsingException("Type " + typeName + " is not supported by Jiao");
        }
    }

    public String getTypeName() {
        return typeName;
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public int getPrecision() {
        return precision;
    }

    public int getScale() {
        return scale;
    }

    public boolean isSigned() {
        return signed;
    }

    public boolean getAutoIncremented() {
        return autoIncremented;
    }

    public Searchable getSearchable() {
        return searchable;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public CtClass getCtType() throws NotFoundException {
        if(mappedClass.isPrimitive()){
            return PRIMITIVE_TYPES.get(mappedClass);
        } else {
            return ClassPool.getDefault().getCtClass(mappedClass.getCanonicalName());
        }
    }

    private byte[] toByteArray(final byte data) {
        return new byte[]{data};
    }

    private byte[] toByteArray(final byte[] data) {
        return data;
    }

    /* ========================= */
    private byte[] toByteArray(final short data) {
        return new byte[]{
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 0) & 0xff),
        };
    }

    /* ========================= */
    private byte[] toByteArray(final char data) {
        return new byte[]{
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 0) & 0xff),
        };
    }

    private byte[] toByteArray(final char[] data) {
        if (data == null) return null;
// ----------
        byte[] byts = new byte[data.length * 2];
        for (int i = 0; i < data.length; i++)
            System.arraycopy(toByteArray(data[i]), 0, byts, i * 2, 2);
        return byts;
    }

    /* ========================= */
    private byte[] toByteArray(final int data) {
        return new byte[]{
                (byte) ((data >> 24) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 0) & 0xff),
        };
    }


    /* ========================= */
    private byte[] toByteArray(final long data) {
        return new byte[]{
                (byte) ((data >> 56) & 0xff),
                (byte) ((data >> 48) & 0xff),
                (byte) ((data >> 40) & 0xff),
                (byte) ((data >> 32) & 0xff),
                (byte) ((data >> 24) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 0) & 0xff),
        };
    }

    /* ========================= */
    private byte[] toByteArray(final float data) {
        return toByteArray(Float.floatToRawIntBits(data));
    }


    /* ========================= */
    private byte[] toByteArray(final double data) {
        return toByteArray(Double.doubleToRawLongBits(data));
    }

    /* ========================= */
    private byte[] toByteArray(final boolean data) {
        return new byte[]{(byte) (data ? 0x01 : 0x00)}; // bool -> {1 byte}
    }

    /* ========================= */
    private byte[] toByteArray(final String data) {
        return (data == null) ? null : data.getBytes();
    }

    /* ========================= */
/* "byte[] data --> primitive type" Methods */
/* ========================= */
    private byte toByte(final byte[] data) {
        return (data == null || data.length == 0) ? 0x0 : data[0];
    }

    private byte[] toByteA(byte[] data) {
        return data;
    }

    /* ========================= */
    private short toShort(final byte[] data) {
        if (data == null || data.length != 2) return 0x0;
// ----------
        return (short) (
                (0xff & data[0]) << 8 |
                        (0xff & data[1]) << 0
        );
    }

    /* ========================= */
    private char toChar(final byte[] data) {
        if (data == null || data.length != 2) return 0x0;
// ----------
        return (char) (
                (0xff & data[0]) << 8 |
                        (0xff & data[1]) << 0
        );
    }

    private String toString(final byte[] data) {
        if (data == null) return null;
// ----------
        byte[] byts = new byte[data.length * 2];
        for (int i = 0; i < data.length; i++)
            System.arraycopy(data[i], 0, byts, i * 2, 2);
        return new String(byts);
    }

    /* ========================= */
    private int toInt(final byte[] data) {
        if (data == null || data.length != 4) return 0x0;
// ----------
        return (int) ( // NOTE: type cast not necessary for int
                (0xff & data[0]) << 24 |
                        (0xff & data[1]) << 16 |
                        (0xff & data[2]) << 8 |
                        (0xff & data[3]) << 0);
    }

    /* ========================= */
    private long toLong(final byte[] data) {
        if (data == null || data.length != 8) return 0x0;
// ----------
        return (long) (
// (Below) convert to longs before shift because digits
// are lost with ints beyond the 32-bit limit
                (long) (0xff & data[0]) << 56 |
                        (long) (0xff & data[1]) << 48 |
                        (long) (0xff & data[2]) << 40 |
                        (long) (0xff & data[3]) << 32 |
                        (long) (0xff & data[4]) << 24 |
                        (long) (0xff & data[5]) << 16 |
                        (long) (0xff & data[6]) << 8 |
                        (long) (0xff & data[7]) << 0);
    }

    /* ========================= */
    private float toFloat(final byte[] data) {
        if (data == null || data.length != 4) return 0x0;
// ---------- simple:
        return Float.intBitsToFloat(toInt(data));
    }


    /* ========================= */
    private double toDouble(byte[] data) {
        if (data == null || data.length != 8) return 0x0;
// ---------- simple:
        return Double.longBitsToDouble(toLong(data));
    }

    /* ========================= */
    private boolean toBoolean(final byte[] data) {
        return (data == null || data.length == 0) ? false : data[0] != 0x00;
    }


    public static void main(String[] args) {
        System.out.println("Hello World");
        System.out.println("Hello World2");
    }

}
