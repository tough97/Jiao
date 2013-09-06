package com.cs.gang.service.excp;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/24/13
 * Time: 2:40 PM
 */
public final class DataSourceException extends Exception{

    public DataSourceException() {
    }

    public DataSourceException(String s) {
        super(s);
    }

    public DataSourceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DataSourceException(Throwable throwable) {
        super(throwable);
    }
}
