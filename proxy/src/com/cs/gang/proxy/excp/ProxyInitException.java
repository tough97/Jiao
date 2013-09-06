package com.cs.gang.proxy.excp;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/8/13
 * Time: 8:48 PM
 */
public final class ProxyInitException extends Exception{

    public ProxyInitException() {
    }

    public ProxyInitException(String s) {
        super(s);
    }

    public ProxyInitException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ProxyInitException(Throwable throwable) {
        super(throwable);
    }
}
