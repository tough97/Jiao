package com.cs.gang.proxy.excp;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/17/13
 * Time: 9:18 AM
 */
public final class FieldNotFoundException extends Exception{

    public FieldNotFoundException() {
    }

    public FieldNotFoundException(String s) {
        super(s);
    }

    public FieldNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public FieldNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
