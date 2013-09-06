package com.cs.gang.proxy;

import com.cs.gang.proxy.excp.ProxyTypeMisMatchException;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 *
 * This is the core class for SQL query statement, this Class is used to create where
 *
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/8/13
 * Time: 10:15 AM
 */
public final class Criteria implements Serializable {

    private Class<? extends Queriable> targetClass;

    public Criteria equals(final Field field) throws ProxyTypeMisMatchException{
        if(!field.getDeclaringClass().equals(targetClass)){
            throw new ProxyTypeMisMatchException(field.getName()
                    +" is not declared by "+targetClass.getCanonicalName());
        }
        return this;
    }

}
