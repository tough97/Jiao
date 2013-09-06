package com.cs.gang.proxy.excp;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/9/13
 * Time: 8:48 AM
 */
public class ProxyTypeMisMatchException extends Exception{

    public ProxyTypeMisMatchException() {
    }

    public ProxyTypeMisMatchException(String s) {
        super(s);
    }

    public ProxyTypeMisMatchException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ProxyTypeMisMatchException(Throwable throwable) {
        super(throwable);
    }
}
