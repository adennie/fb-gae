package com.fizzbuzz.server.persist;

import java.util.Collection;

import com.fizzbuzz.model.SynchronizedObject;
import com.google.code.twig.FindCommand.RootFindCommand;

public class ParentedSynchronizedCollectionPersist<C extends Collection<M>, M extends SynchronizedObject>
        extends SynchronizedCollectionPersist<C, M> {

    private final long mParentId;
    private final ParentedObjectHelper<M> mHelper;

    public ParentedSynchronizedCollectionPersist(final Class<C> collectionModelClass,
            final Class<M> collectionItemModelClass,
            final ObjectPersist<?> parentPersist,
            final long parentId,
            final String collectionItemKind) {
        super(collectionModelClass, collectionItemModelClass);
        mHelper = new ParentedObjectHelper<M>(parentPersist, parentId, collectionItemModelClass, collectionItemKind);
        mParentId = parentId;
    }

    @Override
    protected RootFindCommand<M> getRootFindCommand() {
        return mHelper.getRootFindCommand();
    }

    protected long getParentId() {
        return mParentId;
    }
}
