package com.fizzbuzz.server.resource;

import static com.fizzbuzz.server.resource.Resources.checkObjectExists;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.fizzbuzz.model.PersistentObject;
import com.fizzbuzz.server.biz.ObjectServer;

/*
 * Base server resource class for objects with IDs (fetched from the URL).
 */
public abstract class ObjectServerResource<S extends ObjectServer<M>, M extends PersistentObject>
        extends AbstractServerResource<S> {
    private long mId;

    @Override
    protected void doInit(final S server) throws ResourceException {
        // get the object ID from the URL and verify its existence
        mId = checkObjectExists(this, server);
        super.doInit(server);
    }

    protected long getId() {
        return mId;
    }

    public M getResource() {
        M result = null;
        try {
            result = getServer().get(mId);
        }
        catch (RuntimeException e) {
            doCatch(e);
        }
        return result;

    }

    public void putResource(final M modelObject) {
        try {
            // by default, return 204, since we're not returning any representation. Subclasses that override put() in
            // the call below can change the response status if needed.
            getResponse().setStatus(Status.SUCCESS_NO_CONTENT);
            getServer().put(modelObject);
        }
        catch (RuntimeException e) {
            doCatch(e);
        }
    }

    public void deleteResource() {
        try {
            getServer().delete(mId);
        }
        catch (RuntimeException e) {
            doCatch(e);
        }
    }

}
