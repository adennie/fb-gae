package com.fizzbuzz.server.persist;

import com.fizzbuzz.model.PersistentObject;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.code.twig.FindCommand.RootFindCommand;
import com.google.code.twig.standard.StandardObjectDatastore;

// helper utilities for parented objects
class ParentedObjectHelper<M extends PersistentObject> {

    private final ObjectPersist<?> mParentPersist;
    private final long mParentId;
    private final Class<M> mChildClass;
    private final String mChildKind;

    ParentedObjectHelper(final ObjectPersist<?> parentPersist,
            final long parentId,
            final Class<M> childClass,
            final String childKind) {
        mParentPersist = parentPersist;
        mParentId = parentId;
        mChildClass = childClass;
        mChildKind = childKind;
    }

    RootFindCommand<M> getRootFindCommand() {
        PersistentObject parent = mParentPersist.load(mParentId);
        if (parent == null)
            throw new ParentNotFoundException(mParentId);

        return DatastoreHelper.getDs().find().type(mChildClass).ancestor(parent);
    }

    void storeToDs(final PersistentObject newChild) {
        PersistentObject parent = mParentPersist.load(mParentId);
        if (parent == null) {
            throw new ParentNotFoundException(mParentId);
        }
        StandardObjectDatastore ds = DatastoreHelper.getDs();

        if (!ds.isAssociated(parent)) {
            ds.associate(parent, mParentPersist.getKey(mParentId));
        }
        ds.store().instance(newChild).parent(parent).now();
    }

    Key getKey(final long childId) {
        return KeyFactory.createKey(mParentPersist.getKey(mParentId), mChildKind, childId);

    }

}
