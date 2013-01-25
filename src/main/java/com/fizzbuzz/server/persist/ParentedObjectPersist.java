package com.fizzbuzz.server.persist;

import com.fizzbuzz.model.PersistentObject;
import com.google.appengine.api.datastore.Key;
import com.google.code.twig.FindCommand.RootFindCommand;

public class ParentedObjectPersist<M extends PersistentObject>
        extends ObjectPersist<M> {

    private final long mParentId;
    private final ParentedObjectHelper<M> mHelper;

    public ParentedObjectPersist(final Class<M> objectModelClass,
            final ObjectPersist<?> parentPersist,
            final long parentId,
            final String childKind) {
        super(objectModelClass);
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
