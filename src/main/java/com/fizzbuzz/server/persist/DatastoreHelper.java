package com.fizzbuzz.server.persist;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fizzbuzz.server.resource.BaseApplication;
import com.google.code.twig.standard.StandardObjectDatastore;

public abstract class DatastoreHelper {
    protected final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    private static Map<Long, StandardObjectDatastore> mDsMap;

    static {
        mDsMap = new HashMap<Long, StandardObjectDatastore>();
    }

    public static StandardObjectDatastore getDs() {
        return checkNotNull(mDsMap.get(Thread.currentThread().getId()), "datastore not initialized");
    }

    abstract protected StandardObjectDatastore createDs();

    public void onAppStartup() {
        allocDatastoreForThread();
        registerKindNames();

        if (BaseApplication.getExecutionContext() == BaseApplication.ExecutionContext.DEVELOPMENT) {
            if (datastoreIsEmpty()) {
                seedDatastore();
            }
        }
    }

    public void onNewRequest() {
        allocDatastoreForThread();
    }

    public void onRequestComplete() {
        releaseDatastoreForThread();
    }

    abstract protected void registerKindNames();

    abstract protected boolean datastoreIsEmpty();

    public void resetDatastore() {
        mLogger.info("DatastoreHelper:resetDatastore: called");
        purgeDatastore();
        seedDatastore();
    }

    protected void purgeDatastore() {
        checkState((BaseApplication.getExecutionContext() == BaseApplication.ExecutionContext.DEVELOPMENT),
                "not running in development environment");
    }

    protected void seedDatastore() {
        // override in subclass
    }

    protected void allocDatastoreForThread() {
        // instantiate a Twig datastore object and store it in the map, keyed by the current thread ID
        mDsMap.put(Thread.currentThread().getId(), createDs());
    }

    protected void releaseDatastoreForThread() {
        mDsMap.remove(getDs());
    }

}
