package com.cs.gang.proxy;


import com.cs.gang.proxy.excp.ProxyInitException;
import com.cs.gang.proxy.excp.ProxyTypeMisMatchException;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/8/13
 * Time: 10:17 AM
 */
public abstract class Operateable extends Queriable{


    protected Operateable() throws ProxyInitException, ProxyTypeMisMatchException {
        super();
    }

}
