package com.cs.gang.db.excp;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 8/25/13
 * Time: 8:33 PM
 */
final public class TypeMismatchException extends Exception{
    public TypeMismatchException() {
    }

    public TypeMismatchException(String s) {
        super(s);
    }

    public TypeMismatchException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TypeMismatchException(Throwable throwable) {
        super(throwable);
    }
}
