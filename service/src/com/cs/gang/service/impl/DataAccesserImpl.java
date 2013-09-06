package com.cs.gang.service.impl;

import com.cs.gang.proxy.Criteria;
import com.cs.gang.proxy.Operateable;
import com.cs.gang.proxy.Queriable;
import com.cs.gang.service.DataAccesser;
import com.cs.gang.service.excp.DataSourceException;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 5/27/13
 * Time: 9:13 PM
 */
public final class DataAccesserImpl implements DataAccesser{
    @Override
    public Set<String> getTableNames() throws DataSourceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class<? extends Operateable> getTable(String name) throws DataSourceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<String> getQueryNames() throws DataSourceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
    public <T extends Queriable> List<T> query(Criteria condition) throws DataSourceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String save(Operateable operateable) throws DataSourceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
