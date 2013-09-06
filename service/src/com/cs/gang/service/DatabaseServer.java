package com.cs.gang.service;

import com.cs.gang.db.Database;
import com.cs.gang.db.excp.TableNotFoundException;
import com.cs.gang.proxy.Criteria;
import com.cs.gang.proxy.Operateable;
import com.cs.gang.proxy.Queriable;
import com.cs.gang.service.excp.DataSourceException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/23/13
 * Time: 1:24 PM
 */
public class DatabaseServer extends UnicastRemoteObject implements DataAccesser {

    private Database database;

    private DatabaseServer(final Database database, final int port) throws RemoteException {
        super(port);
        this.database = database;
    }

    @Override
    public Set<String> getTableNames() throws DataSourceException {
        return database.getTableAccessory().getTableNames();
    }

    @Override
    public Class<? extends Operateable> getTable(final String name) throws DataSourceException {
        try {
            return database.getTableAccessory().getTableClass(name);
        } catch (final TableNotFoundException e) {
            throw new DataSourceException(e);
        }
    }

    @Override
    public Set<String> getQueryNames() throws DataSourceException {
        return null;
    }

    @Override
    public Class<? extends Queriable> getQuery(String name) throws DataSourceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends Queriable> T uniqueQuery(Criteria condition) throws DataSourceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long count(Criteria condition) throws DataSourceException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends Queriable> List<T> query(final Criteria condition) throws DataSourceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String save(Operateable operateable) throws DataSourceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
