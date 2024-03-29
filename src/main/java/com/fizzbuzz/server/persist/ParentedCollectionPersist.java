package com.fizzbuzz.server.persist;

import java.util.Collection;

import com.fizzbuzz.model.PersistentObject;
import com.google.code.twig.FindCommand.RootFindCommand;

public abstract class ParentedCollectionPersist<C extends Collection<M>, M extends PersistentObject, P extends ObjectPersist<?>>
        extends CollectionPersist<C, M> {

    private final ParentedObjectHelper<M> mHelper;
    private final long mParentId;

    protected ParentedCollectionPersist(final Class<C> collectionModelClass,
            final Class<M> collectionItemModelClass,
            final P parentPersist,
            final long parentId,
            final String collectionItemKind) {
        super(collectionModelClass, collectionItemModelClass);
        mHelper = new ParentedObjectHelper<M>(parentPersist, parentId, collectionItemModelClass, collectionItemKind);
        mParentId = parentId;
    }

    // since this is a parented collection, we need to override getRootFindCommand to factor in the parent info
    @Override
    protected RootFindCommand<M> getRootFindCommand() {
        return mHelper.getRootFindCommand();
    }

    protected long getParentId() {
        return mParentId;
    }

}
