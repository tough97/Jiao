package com.cs.gang.db.excp;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 4/24/13
 * Time: 2:11 PM
 */
public final class DBParsingException extends Exception{

    public DBParsingException() {
    }

    public DBParsingException(String s) {
        super(s);
    }

    public DBParsingException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DBParsingException(Throwable throwable) {
        super(throwable);
    }
}
