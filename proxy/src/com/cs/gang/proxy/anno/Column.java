package com.cs.gang.proxy.anno;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 8/30/13
 * Time: 3:34 PM
 */
public @interface Column {
    String format() default "";
    Class type() default Column.class;
}
