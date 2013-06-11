package com.fizzbuzz.server.persist;

import com.fizzbuzz.model.PersistentObject;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.code.twig.FindCommand;

import java.util.Collection;

import static com.fizzbuzz.util.base.Reflections.newInstance;
import static com.google.common.base.Preconditions.checkNotNull;

public class CollectionPersist<C extends Collection<M>, M extends PersistentObject>
        extends BasePersist<M> {

    private final Class<C> mCollectionModelClass;

    public CollectionPersist(final Class<C> collectionModelClass,
            final Class<M> collectionItemModelClass) {
        super(collectionItemModelClass);
        mCollectionModelClass = checkNotNull(collectionModelClass, "collection model class");
    }

    public C get() {
        return get(null);
    }

    public C get(final FindCommand.RootFindCommand<M> providedFindCommand) {
        FindCommand.RootFindCommand<M> findCommand = providedFindCommand;
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
    public void delete(final C collection,
            final ObjectPersist<M> itemPersist) {
        ((BaseDatastore) getDs()).batchKeyDelete(collection, itemPersist);

    }

    protected C newCollectionInstance() {
        return newInstance(mCollectionModelClass);
    }

    protected Class<M> getCollectionItemModelClass() {
        return getModelClass();
    }

}
