package com.cs.gang.db.excp;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/30/13
 * Time: 10:00 AM
 */
public final class TableNotFoundException extends Exception{

    public TableNotFoundException() {
    }

    public TableNotFoundException(String s) {
        super(s);
    }

    public TableNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TableNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
