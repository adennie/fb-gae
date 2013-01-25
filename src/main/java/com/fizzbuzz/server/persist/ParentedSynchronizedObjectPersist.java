package com.fizzbuzz.server.persist;

import com.fizzbuzz.model.SynchronizedObject;
import com.google.appengine.api.datastore.Key;
import com.google.code.twig.FindCommand.RootFindCommand;

public class ParentedSynchronizedObjectPersist<M extends SynchronizedObject>
        extends SynchronizedObjectPersist<M> {

    private final long mParentId;
    private final ParentedObjectHelper<M> mHelper;

    public ParentedSynchronizedObjectPersist(final Class<M> objectModelClass,
            final ObjectPersist<?> parentPersist,
            final long parentId,
            final TickablePersist tickablePersist,
            final long tickerId,
            final String childKind) {
        super(objectModelClass, tickablePersist, tickerId);
        mHelper = new ParentedObjectHelper<M>(parentPersist, parentId, objectModelClass, childKind);
        mParentId = parentId;
    }

    @Override
    protected RootFindCommand<M> getRootFindCommand() {
        return mHelper.getRootFindCommand();
    }

    @Override
    public void storeToDs(final M newChild) {
        mHelper.storeToDs(newChild);
    }

    @Override
    public Key getKey(final long childId) {
        return mHelper.getKey(childId);
    }

    protected long getParentId() {
        return mParentId;
    }
}
