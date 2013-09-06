package com.cs.gang.service;

import com.cs.gang.proxy.Criteria;
import com.cs.gang.proxy.Operateable;
import com.cs.gang.proxy.Queriable;
import com.cs.gang.service.excp.DataSourceException;

import java.rmi.Remote;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/24/13
 * Time: 2:30 PM
 */
public interface DataAccesser extends Remote {
    /**
     * These methods are used to retrieve Queries and Operatable Units from Remote Server
     * @return
     * @throws DataSourceException
     */
    public Set<String> getTableNames() throws DataSourceException;
    public Class<? extends Operateable> getTable(final String name) throws DataSourceException;
    public Set<String> getQueryNames() throws DataSourceException;
    public Class<? extends Queriable> getQuery(final String name) throws DataSourceException;

    /**
     * These methods are used to make CRUD operations to database remotely
     * @param condition
     * @param <T>
     * @return
     * @throws DataSourceException
     */
    public <T extends Queriable> T uniqueQuery(final Criteria condition) throws DataSourceException;
    public long count(final Criteria condition) throws DataSourceException;
    public <T extends Queriable> List<T> query(final Criteria condition) throws DataSourceException;

    // saves one record of operatable returns any message if exception happens
    //returns null if things go wrong
    public String save(final Operateable operateable) throws DataSourceException;

}
