package com.fizzbuzz.server.persist;

import static com.fizzbuzz.util.base.Reflections.newInstance;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fizzbuzz.model.PersistentObject;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.code.twig.FindCommand.RootFindCommand;

public class CollectionPersist<C extends Collection<M>, M extends PersistentObject>
        extends BasePersist {
    protected final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    private final Class<C> mCollectionModelClass;
    private final Class<M> mCollectionItemModelClass;

    public CollectionPersist(final Class<C> collectionModelClass, final Class<M> collectionItemModelClass) {
        mCollectionModelClass = checkNotNull(collectionModelClass, "collection model class");
        mCollectionItemModelClass = checkNotNull(collectionItemModelClass, "collection item model class");
    }

    public C get() {
        return get(null);
    }

    public C get(RootFindCommand<M> findCommand) {
        C coll = newCollectionInstance();
        if (findCommand == null)
            findCommand = getRootFindCommand();
        QueryResultIterator<M> itr = findCommand.now();
        while (itr.hasNext()) {
            coll.add(itr.next());
        }

        return coll;
    }

    public void updateAll(final C collection) {
        getDs().updateAll(collection);
    }

    // delete all entities of type M (possibly constrained by implementation of getRootFindCommand, e.g. limit to entity
    // group)
    public void deleteAll(final ObjectPersist<M> itemPersist) {
        // do a keys-only fetch, then a batch delete
        QueryResultIterator<M> itr = getRootFindCommand()
                .unactivated() // keys-only
                .now();

        // delete all the entities corresponding to the keys in the iterator
        ((BaseDatastore) getDs()).batchKeyDelete(itr, itemPersist);
    }

    // delete all the entities in the specified collection
    public void delete(final C collection, final ObjectPersist<M> itemPersist) {
        ((BaseDatastore) getDs()).batchKeyDelete(collection, itemPersist);

    }

    protected RootFindCommand<M> getRootFindCommand() {
        return DatastoreHelper.getDs().find().type(mCollectionItemModelClass);
    }

    protected C newCollectionInstance() {
        return newInstance(mCollectionModelClass);
    }

    protected Class<M> getCollectionItemModelClass() {
        return mCollectionItemModelClass;
    }

}
