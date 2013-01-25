package com.fizzbuzz.server.persist;

import java.util.Collection;

import com.fizzbuzz.model.SynchronizedObject;
import com.fizzbuzz.model.TickStamp;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.code.twig.FindCommand.RootFindCommand;

public class SynchronizedCollectionPersist<C extends Collection<M>, M extends SynchronizedObject>
        extends CollectionPersist<C, M> {

    public SynchronizedCollectionPersist(final Class<C> collectionModelClass,
            final Class<M> collectionItemModelClass) {
        super(collectionModelClass, collectionItemModelClass);
    }

    public C getSince(final TickStamp tickStamp) {
        RootFindCommand<M> findCommand = getRootFindCommand().addFilter("mTickStamp", FilterOperator.GREATER_THAN,
                tickStamp);
        return get(findCommand);

    }
}
