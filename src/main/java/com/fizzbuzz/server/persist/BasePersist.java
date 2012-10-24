package com.fizzbuzz.server.persist;

import com.google.code.twig.configuration.Configuration;
import com.google.code.twig.standard.BaseObjectDatastore;

public class BasePersist {

    protected static BaseObjectDatastore getDs() {
        return DatastoreHelper.getDs();
    }

    protected static Configuration getConfiguration() {
        return DatastoreHelper.getDs().getConfiguration();
    }

}
