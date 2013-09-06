package com.cs.gang.proxy;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/16/13
 * Time: 3:29 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ColumnField {

    String pattern() default "";
    Class<? extends Serializable> targetType() default Serializable.class;
    int index();

}
