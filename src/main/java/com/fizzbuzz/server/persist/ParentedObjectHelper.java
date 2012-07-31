package com.fizzbuzz.server.persist;

import com.fizzbuzz.model.PersistentObject;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.code.twig.standard.StandardObjectDatastore;

// helper utilities for parented objects
public class ParentedObjectHelper {

    private final ObjectPersist<?> mParentPersist;
    private final long mParentId;
    private final String mChildKind;

    public ParentedObjectHelper(final ObjectPersist<?> parentPersist, final long parentId, final String childKind) {
        mParentPersist = parentPersist;
        mParentId = parentId;
        mChildKind = childKind;
    }

    public void storeToDs(final PersistentObject parent, final PersistentObject newChild) {
        StandardObjectDatastore ds = DatastoreHelper.getDs();

        if (!ds.isAssociated(parent)) {
            ds.associate(parent, mParentPersist.getKey(parent.getId()));
        }
        ds.store().instance(newChild).parent(parent).now();
    }

    public Key getKey(final long childId) {
        return KeyFactory.createKey(mParentPersist.getKey(mParentId), mChildKind, childId);

    }

}
