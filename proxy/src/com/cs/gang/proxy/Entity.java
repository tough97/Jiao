package com.cs.gang.proxy;

import com.cs.gang.proxy.excp.ProxyInitException;
import com.cs.gang.proxy.excp.ProxyTypeMisMatchException;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * This class is used to proxy the Queriable Row the difficulties of this Class design is how to access values of
 * the instance derived from this class, it should be fast
 * User: gang-liu
 * Date: 6/18/13
 * Time: 3:46 PM
 */
public abstract class Entity {

    private static final long serialVersionUID = 1L;


    protected Entity() throws ProxyInitException{
        if(!Entity.class.isAssignableFrom(this.getClass())){
            throw new ProxyInitException(new ProxyTypeMisMatchException(
                    this.getClass()+" is not a subtype of "+Entity.class));
        }



    }

}
