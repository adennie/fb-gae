package com.fizzbuzz.server.persist;

import java.util.Collection;

import com.fizzbuzz.model.PersistentObject;
import com.google.code.twig.FindCommand.RootFindCommand;

public abstract class ParentedCollectionPersist<C extends Collection<M>, M extends PersistentObject, P extends ObjectPersist<?>>
        extends CollectionPersist<C, M> {

    private final long mParentId;
    private final ObjectPersist<?> mParentPersist;

    protected ParentedCollectionPersist(final Class<C> collectionModelClass, final Class<M> collectionItemModelClass,
            final P parentPersist, final long parentId) {
        super(collectionModelClass, collectionItemModelClass);
        mParentId = parentId;
        mParentPersist = parentPersist;
    }

    public long getParentId() {
        return mParentId;
    }

    // since this is a parented collection, we need to override getRootFindCommand to factor in the parent info
    @Override
    protected RootFindCommand<M> getRootFindCommand() {
        PersistentObject parent = getParent();
        if (parent == null)
            throw new ParentNotFoundException(mParentId);

        return DatastoreHelper.getDs().find().type(getCollectionItemModelClass()).ancestor(parent);
    }

    private PersistentObject getParent() {
        return mParentPersist.load(mParentId);
    }
}
