package com.fizzbuzz.server.resource;

import org.restlet.resource.ResourceException;

import com.fizzbuzz.server.biz.ReadOnlySingletonServer;

public class ReadOnlySingletonServerResource<S extends ReadOnlySingletonServer<M>, M>
        extends AbstractServerResource<S> {

    @Override
    protected void doInit(final S server) throws ResourceException {
        super.doInit(server);
    }

    public M getResource() {
        M result = null;
        try {
            result = getServer().get();
        }
        catch (RuntimeException e) {
            doCatch(e);
        }
        return result;
    }
}
