package com.cs.gang.proxy;


import com.cs.gang.proxy.excp.ProxyInitException;
import com.cs.gang.proxy.excp.ProxyTypeMisMatchException;

/**
 *
 * used by client to make a SQL select statement and take the result set into sub-class of this
 * class. each sun-class of Queriable will be used to represent a row of ResultSet objct. and
 * Queriable classes are administrated at server side.
 *
 * Jiao SERVER DOES NOT ALLOW CLIENT TO CUSTOMIZE QUERY AT RUN TIME, DATABASE ADMINISTRATOR
 * CREATE PRE-DEFINED QUERY AT SERVER SIDE AND
 *
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/8/13
 * Time: 10:16 AM
 */
public abstract class Queriable extends ColumnSet{

    /**
     * This constructor
     * @throws ProxyInitException
     * @throws ProxyTypeMisMatchException
     */
    protected Queriable() throws ProxyInitException, ProxyTypeMisMatchException {
        super();
    }



}
