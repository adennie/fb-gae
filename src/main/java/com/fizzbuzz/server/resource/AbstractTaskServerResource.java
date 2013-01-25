package com.fizzbuzz.server.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fizzbuzz.server.biz.BaseServer;

public abstract class AbstractTaskServerResource<S extends BaseServer>
        extends AbstractServerResource<S> {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    // Catch and squelch commonly-encountered exceptions for which there is no point in having GAE retry the task request. Pass other exceptions to
    // the base class implementation.
    @Override
    protected void doCatch(final RuntimeException e) {
        Class<?> exceptionClass = e.getClass();

        if (IllegalArgumentException.class.isAssignableFrom(exceptionClass))
            mLogger.error("FeedFetcherTaskServerResource.postResource: caught IllegalArgumentException, squelching it to prevent retries", e);
        else if (IllegalStateException.class.isAssignableFrom(exceptionClass))
            mLogger.error("FeedFetcherTaskServerResource.postResource: caught IllegalStateException, squelching it to prevent retries", e);
        else
            super.doCatch(e);

    }
}
