package com.cs.gang.db.excp;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 4/24/13
 * Time: 2:41 PM
 */
public final class DBInitialException extends Exception{

    public DBInitialException() {
    }

    public DBInitialException(String s) {
        super(s);
    }

    public DBInitialException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DBInitialException(Throwable throwable) {
        super(throwable);
    }
}
