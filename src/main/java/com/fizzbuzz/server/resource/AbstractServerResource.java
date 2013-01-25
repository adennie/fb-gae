package com.fizzbuzz.server.resource;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fizzbuzz.exception.ConflictException;
import com.fizzbuzz.exception.NotFoundException;
import com.fizzbuzz.server.biz.BaseServer;

public abstract class AbstractServerResource<S extends BaseServer>
        extends ServerResource {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private S mServer;

    protected void doInit(final S server) throws ResourceException {
        mServer = server;
        super.doInit();
    }

    protected S getServer() {
        return mServer;
    }

    protected ServerUriHelper getUriHelper() {
        return ((BaseApplication) getApplication()).getUriHelper();
    }

    protected void doCatch(final RuntimeException e) {
        Class<?> exceptionClass = e.getClass();
        if (NotFoundException.class.isAssignableFrom(exceptionClass)) {
            // chain & translate internal "not found" exception into a 404
            mLogger.error(e.getMessage());
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage(), e);
        }
        if (ConflictException.class.isAssignableFrom(exceptionClass)
                || IllegalStateException.class.isAssignableFrom(exceptionClass)) {
            // chain & translate internal "conflict" and "illegal state" exceptions into a 409
            mLogger.error(e.getMessage());
            throw new ResourceException(Status.CLIENT_ERROR_CONFLICT, e.getMessage(), e);
        }
        if (IllegalArgumentException.class.isAssignableFrom(exceptionClass)) {
            // chain & translate internal "illegal argument" exception into a 400
            mLogger.error(e.getMessage());
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage(), e);
        }

        throw e;
    }
}
