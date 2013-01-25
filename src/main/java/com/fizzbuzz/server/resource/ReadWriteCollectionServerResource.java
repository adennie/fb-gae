package com.fizzbuzz.server.resource;

import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import com.fizzbuzz.server.biz.ReadWriteCollectionServer;

public abstract class ReadWriteCollectionServerResource<S extends ReadWriteCollectionServer<C, M>, C, M>
        extends ReadOnlyCollectionServerResource<S, C, M> {

    @Override
    protected void doInit(final S server) throws ResourceException {
        super.doInit(server);
    }

    abstract protected String getUri(M modelObject);

    public M postResource(final M modelObject) {
        M result = null;
        try {
            result = addResource(modelObject);
        }
        catch (RuntimeException e) {
            doCatch(e);
        }
        return result;
    }

    @Override
    public Representation toRepresentation(final Object source,
            final Variant target) {
        Representation result = super.toRepresentation(source, target);
        // the POST method creates a new collection item, which is returned as the response body. We should specify the
        // Content-Location header to indicate the URI of that resource. The value of the URI was already stored into
        // the LocationRef of the response, so just grab that and reuse it.
        if (getMethod().equals(Method.POST))
            result.setLocationRef(getResponse().getLocationRef());
        return result;
    }

    protected M addResource(final M modelObject) {
        M result = null;
        result = getServer().add(modelObject);
        getResponse().setStatus(Status.SUCCESS_CREATED);
        getResponse().setLocationRef(getUri(result));
        return result;
    }

    public void deleteResource() {
        getServer().deleteAll();
    }
}
