package com.fizzbuzz.server.persist;

import static com.fizzbuzz.util.base.Reflections.newInstance;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fizzbuzz.model.PersistentObject;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class ObjectPersist<M extends PersistentObject>
        extends BasePersist {
    protected final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private final Class<M> mModelClass;

    public ObjectPersist(final Class<M> modelClass) {
        mModelClass = checkNotNull(modelClass, "model class");
    }

    public Class<M> getModelClass() {
        return mModelClass;
    }

    public boolean entityExists(final long id) {
        return load(id) != null;
    }

    public M store(final M modelObject) {

        // modelObject should not already have an assigned id
        if (modelObject.getId() != 0)
            mLogger.warn("ObjectPersist.store: modelObject already has an assigned entity ID.  This is OK if retrying a failed transaction, but otherwise indicates a problem.");

        modelObject.setCreationDate(Calendar.getInstance().getTime());

        stamp(modelObject);

        storeToDs(modelObject);

        mLogger.trace("ObjectPersist.store: stored new object - {}", modelObject);

        return modelObject;
    }

    protected void storeToDs(final M modelObject) {
        getDs().store(modelObject);
    }

    public M load(final long id) {
        return getDs().load(getKey(id));
    }

    public M update(final M modelObject) {
        checkNotNull(modelObject, "model object");

        // modelObject must already have an assigned id
        checkArgument((modelObject.getId() != 0), "update called on modelObject that was not previously stored");

        stamp(modelObject);

        if (!getDs().isAssociated(modelObject)) {
            getDs().associate(modelObject, getKey(modelObject));
        }
        getDs().update(modelObject);

        mLogger.trace("ObjectPersist.update: updated object - {}", modelObject);

        return modelObject;
    }

    // NOTE: this method doesn't write to the datastore. Follow up with call to update() or
    // CollectionPersist.updateAll().
    public M touch(final M modelObject) {
        stamp(modelObject);
        return modelObject;
    }

    public void delete(final M modelObject) {
        checkNotNull(modelObject, "model object");

        // modelObject must already have an assigned id
        checkArgument((modelObject.getId() != 0), "delete called on modelObject that was not previously stored");

        if (!getDs().isAssociated(modelObject)) {
            getDs().associate(modelObject, getKey(modelObject));
        }

        getDs().delete(modelObject);
    }

    // for deleting unassociated objects when all you have is the ID, and you don't want to fetch it first
    public void delete(final long id) {
        // create a dummy object to make twig happy
        M dummy = newInstance(mModelClass, id);
        delete(dummy);
    }

    public Key getKey(final long id) {
        return KeyFactory.createKey(getKind(), id);
    }

    protected String getKind() {
        return getKind(mModelClass);
    }

    protected void stamp(final M modelObject) {
        // no-op for now. Consider implementing a last modified timestamp in the future, and updating it here
    }

    static String getKind(final Class<?> c) {
        return getConfiguration().typeToKind(c);
    }

    Key getKey(final M modelObject) {
        return getKey(modelObject.getId());
    }

}
