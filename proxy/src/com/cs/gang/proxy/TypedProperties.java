package com.cs.gang.proxy;

import com.cs.gang.proxy.excp.FieldNotFoundException;
import com.cs.gang.proxy.excp.ProxyTypeMisMatchException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/19/13
 * Time: 9:29 AM
 */
public final class TypedProperties {

    static final Set<Class> SUPPORTED_TYPES = new HashSet<Class>();
    static{
        SUPPORTED_TYPES.add(String.class); SUPPORTED_TYPES.add(java.sql.Timestamp.class);
        SUPPORTED_TYPES.add(java.sql.Date.class); SUPPORTED_TYPES.add(java.sql.Time.class);
        SUPPORTED_TYPES.add(BigDecimal.class); SUPPORTED_TYPES.add(BigInteger.class);
        SUPPORTED_TYPES.add(URL.class); SUPPORTED_TYPES.add(byte[].class);
    }

    private static Map<Class, Set<Field>> getTypedFieldMap(){
        final Map<Class, Set<Field>> ret = new HashMap<Class, Set<Field>>();
        ret.put(int.class, new HashSet<Field>());
        ret.put(float.class, new HashSet<Field>());
        ret.put(short.class, new HashSet<Field>());
        ret.put(long.class, new HashSet<Field>());
        ret.put(double.class, new HashSet<Field>());
        ret.put(char.class, new HashSet<Field>());
        ret.put(byte.class, new HashSet<Field>());
        ret.put(boolean.class, new HashSet<Field>());
        for(final Class type : SUPPORTED_TYPES){
            ret.put(type, new HashSet<Field>());
        }
        ret.put(List.class, new HashSet<Field>());
        ret.put(ColumnSet.class, new HashSet<Field>());
        return ret;
    }

    private Class<? extends ColumnSet> host;
    private Map<String, Field> namedFields = new HashMap<String, Field>();
    private Map<Class, Set<Field>> typeFieldMap = getTypedFieldMap();
    private Map<Field, Class> fieldTypeMap = new HashMap<Field, Class>();


    TypedProperties(final Class<? extends ColumnSet> host) throws ProxyTypeMisMatchException {
        this.host = host;
        for(final Field field : host.getDeclaredFields()){
            parseField(field);
        }
    }

    public Set<String> getFieldNames(){
        return namedFields.keySet();
    }

    public Set<String> getTypedFieldNames(final Class type) throws ProxyTypeMisMatchException{
        if(!isSupportsType(type)){
            throw new ProxyTypeMisMatchException("Type "+type.getName()+" is not supported");
        }
        final Set<String> names = new HashSet<String>();
        for(final Field field : typeFieldMap.get(type)){
            names.add(field.getName());
        }
        return names;
    }



    /**
     * @param name
     * @param type
     * @return
     * @throws com.cs.gang.proxy.excp.FieldNotFoundException
     * @throws com.cs.gang.proxy.excp.ProxyTypeMisMatchException
     */
    Field getTypedField(final String name, final Class type)
            throws FieldNotFoundException, ProxyTypeMisMatchException {
        if (type == null) {
            throw new ProxyTypeMisMatchException("Field examining host can not be null");
        }
        final Field field = namedFields.get(name);
        if (field == null) {
            throw new FieldNotFoundException("Field " + name + " not defined in " + this.getClass().getName());
        }
        if (!field.getType().equals(type)) {
            throw new ProxyTypeMisMatchException("Field host " + field.getType() + " can not have int value output");
        }
        return field;
    }

    Field getNamedField(final String name) throws FieldNotFoundException{
        final Field ret = namedFields.get(name);
        if(ret == null){
            throw new FieldNotFoundException("Field "+name
                    +" was not found in Type "+ host.getCanonicalName());
        }
        return ret;
    }

    Class parseFieldType(final Field field) throws ProxyTypeMisMatchException {
        if(!field.getDeclaringClass().equals(host)){
            throw new ProxyTypeMisMatchException("Field "+field.getName()
                    +" is not declared by "+ host.getCanonicalName());
        }
        if (field.getType().isPrimitive() || (field.getType().isArray()
                && field.getType().getComponentType().equals(byte.class)) ||
                field.getType().equals(String.class) ||
                field.getType().getSuperclass().equals(Date.class) ||
                field.getType().equals(BigDecimal.class) ||
                field.getType().equals(BigInteger.class) ||
                field.getType().equals(URL.class) ||
                field.getType().equals(BigDecimal.class) ||
                field.getType().equals(BigInteger.class) ||
                field.getType().equals(URL.class)) {
            return field.getType();
        } else if (List.class.isAssignableFrom(field.getType()) &&
                ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0] instanceof ColumnSet) {
            return List.class;
        } else if(field.getType().isAssignableFrom(ColumnSet.class)){
            return field.getType();
        } else {
            throw new ProxyTypeMisMatchException("Type not supported " + field.getType());
        }
    }

    static boolean isSupportsType(final Class type){
        if(type.isPrimitive()){
            return true;
        } else if(SUPPORTED_TYPES.contains(type)){
            return true;
        } else {
            return List.class.isAssignableFrom(type)
                    || ColumnSet.class.isAssignableFrom(type);
        }
    }



    /**
     * makes sure each field is valid
     * <p/>
     * I might be changing ColumnSet to Queriable latter since only Query will use
     * table relationship
     */
    private void parseField(final Field field) throws ProxyTypeMisMatchException {
        final Class fieldType = parseFieldType(field);
        typeFieldMap.get(fieldType).add(field);
        fieldTypeMap.put(field, fieldType);
        namedFields.put(field.getName(), field);
    }

}
