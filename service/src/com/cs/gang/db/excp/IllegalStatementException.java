package com.cs.gang.db.excp;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/3/13
 * Time: 10:46 AM
 */
public class IllegalStatementException extends Exception{

    public IllegalStatementException() {
    }

    public IllegalStatementException(String s) {
        super(s);
    }

    public IllegalStatementException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public IllegalStatementException(Throwable throwable) {
        super(throwable);
    }
}
