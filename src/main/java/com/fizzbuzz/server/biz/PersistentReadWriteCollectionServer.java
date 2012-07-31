package com.fizzbuzz.server.biz;

import java.util.Collection;

import com.fizzbuzz.model.PersistentObject;
import com.fizzbuzz.server.persist.CollectionPersist;
import com.fizzbuzz.server.persist.ObjectPersist;

public class PersistentReadWriteCollectionServer<P extends CollectionPersist<C, M>, C extends Collection<M>, M extends PersistentObject, IP extends ObjectPersist<M>>
        extends ReadWriteCollectionServer<C, M> {

    private final P mPersist;
    private final IP mItemPersist;

    protected PersistentReadWriteCollectionServer(final P persist, final IP itemPersist) {
        mPersist = persist;
        mItemPersist = itemPersist;
    }

    protected P getPersist() {
        return mPersist;
    }

    protected IP getItemPersist() {
        return mItemPersist;
    }

    @Override
    public C get() {
        return mPersist.get();
    }

    @Override
    public M add(final M modelObject) {
        return getItemPersist().store(modelObject);
    }

    @Override
    public void deleteAll() {
        mPersist.deleteAll(mItemPersist);
    }

    @Override
    public void delete(C collection) {
        mPersist.delete(collection, mItemPersist);
    }

}
