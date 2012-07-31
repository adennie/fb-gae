package com.fizzbuzz.server.biz;

import java.util.Collection;

import com.fizzbuzz.model.PersistentObject;
import com.fizzbuzz.server.persist.CollectionPersist;

public class PersistentReadOnlyCollectionServer<P extends CollectionPersist<C, M>, C extends Collection<M>, M extends PersistentObject>
        extends ReadOnlyCollectionServer<C> {

    private final P mPersist;

    protected PersistentReadOnlyCollectionServer(final P persist) {
        mPersist = persist;
    }

    protected P getPersist() {
        return mPersist;
    }

    @Override
    public C get() {
        return mPersist.get();
    }

}
