package com.fizzbuzz.server.biz;

import com.fizzbuzz.model.PersistentObject;
import com.fizzbuzz.server.persist.ObjectPersist;

public abstract class PersistentObjectServer<P extends ObjectPersist<M>, M extends PersistentObject>
        extends ObjectServer<M> {

    private final P mPersist;

    protected PersistentObjectServer(final P persist) {
        mPersist = persist;
    }

    protected P getPersist() {
        return mPersist;
    }

    @Override
    public boolean entityExists(final long id) {
        return mPersist.entityExists(id);
    }

    @Override
    public M get(final long id) {
        return mPersist.load(id);
    }

    // subclasses will typically override put() to merge the incoming model object's state with the previously saved
    // one, then call super.put().
    @Override
    public M put(final M modelObject) {
        return mPersist.update(modelObject);
    }

    @Override
    public M update(final M modelObject) {
        return mPersist.update(modelObject);
    }

    @Override
    public void delete(final long id) {
        mPersist.delete(id);
    }

}
