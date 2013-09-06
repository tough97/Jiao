package com.cs.gang.db.excp;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 7/16/13
 * Time: 8:56 PM
 */
final public class ColumnNotFoundException extends Exception{
    public ColumnNotFoundException() {
    }

    public ColumnNotFoundException(String s) {
        super(s);
    }

    public ColumnNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ColumnNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
