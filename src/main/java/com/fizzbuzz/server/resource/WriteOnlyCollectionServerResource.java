package com.fizzbuzz.server.resource;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.fizzbuzz.server.biz.WriteOnlyCollectionServer;

public abstract class WriteOnlyCollectionServerResource<S extends WriteOnlyCollectionServer<M>, M>
        extends AbstractServerResource<S> {

    @Override
    protected void doInit(final S server) throws ResourceException {
        super.doInit(server);
    }

    public M postResource(final M modelObject) {
        M result = null;
        try {
            result = getServer().add(modelObject);
            getResponse().setStatus(Status.SUCCESS_CREATED);
        }
        catch (RuntimeException e) {
            doCatch(e);
        }
        return result;
    }
}
