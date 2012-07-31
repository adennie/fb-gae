package com.fizzbuzz.server.resource;

import org.restlet.resource.ResourceException;

import com.fizzbuzz.server.biz.ReadOnlyCollectionServer;

public abstract class ReadOnlyCollectionServerResource<S extends ReadOnlyCollectionServer<C>, C, M>
        extends BaseServerResource<S> {

    @Override
    protected void doInit(final S server) throws ResourceException {
        super.doInit(server);
    }

    public C getResource() {
        C result = null;
        try {
            result = getServer().get();
        }
        catch (RuntimeException e) {
            doCatch(e);
        }
        return result;
    }
}
