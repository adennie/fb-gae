package com.fizzbuzz.server.persist;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.code.twig.FindCommand.RootFindCommand;
import com.google.code.twig.configuration.Configuration;
import com.google.code.twig.standard.BaseObjectDatastore;

public class BasePersist<M> {

    private final Class<M> mModelClass;

    protected BasePersist(Class<M> objectModelClass) {
        mModelClass = checkNotNull(objectModelClass, "object model class");
    }

    protected Class<M> getModelClass() {
        return mModelClass;
    }

    protected RootFindCommand<M> getRootFindCommand() {
        return DatastoreHelper.getDs().find().type(getModelClass());
    }

    protected static BaseObjectDatastore getDs() {
        return DatastoreHelper.getDs();
    }

    protected static Configuration getConfiguration() {
        return DatastoreHelper.getDs().getConfiguration();
    }
}
