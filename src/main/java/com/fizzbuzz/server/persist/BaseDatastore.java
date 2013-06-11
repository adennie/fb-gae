package com.fizzbuzz.server.persist;

import com.fizzbuzz.model.PersistentObject;
import com.fizzbuzz.model.TickStamp;
import com.fizzbuzz.model.Ticker;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.code.twig.Settings;
import com.google.code.twig.configuration.Configuration;
import com.google.code.twig.standard.StandardObjectDatastore;
import com.google.code.twig.util.EntityToKeyFunction;
import com.google.common.collect.Iterators;
import com.vercer.convert.CompositeTypeConverter;
import com.vercer.convert.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class BaseDatastore
        extends StandardObjectDatastore {

    public BaseDatastore(final Configuration config) {
        super(Settings.defaults(), config, 0, false);
    }

    public final void deleteEntityGroup(final Key rootKey) {
        Query query = new Query(rootKey);
        query.setKeysOnly();
        FetchOptions options = FetchOptions.Builder.withChunkSize(100);
        Iterator<Entity> entities = servicePrepare(query, Settings.defaults()).asIterator(options);
        Iterator<Key> keys = Iterators.transform(entities, new EntityToKeyFunction());
        Iterator<List<Key>> partitioned = Iterators.partition(keys, 100);
        while (partitioned.hasNext()) {
            deleteKeys(partitioned.next());
        }
    }

    public <M extends PersistentObject> void batchKeyDelete(final QueryResultIterator<M> itr,
            final ObjectPersist<?> persist) {
        ArrayList<Key> keys = new ArrayList<Key>();

        while (itr.hasNext()) {
            M entity = itr.next();
            keys.add(persist.getKey(entity.getId()));
        }

        // delete the entities in the key collection
        if (!keys.isEmpty()) {
            super.deleteKeys(keys);
        }
    }

    public <M extends PersistentObject> void batchKeyDelete(final Collection<M> collection,
            final ObjectPersist<?> persist) {
        ArrayList<Key> keys = new ArrayList<Key>();

        for (M entity : collection) {
            keys.add(persist.getKey(entity.getId()));
        }

        // delete the entities in the key collection
        if (!keys.isEmpty()) {
            super.deleteKeys(keys);
        }
    }

    @Override
    protected CompositeTypeConverter createTypeConverter() {
        CompositeTypeConverter converter = super.createTypeConverter();

        // register converters that convert from TickStamp to Long and vice versa
        converter.register(new Converter<TickStamp, Long>() {
            @Override
            public Long convert(final TickStamp tickStamp) {
                return tickStamp.getTickValue();
            }
        });
        converter.register(new Converter<Long, TickStamp>() {
            @Override
            public TickStamp convert(final Long value)
            {
                return new TickStamp(value);
            }
        });

        // register converters that convert from Ticker to Long and vice versa
        converter.register(new Converter<Ticker, Long>() {
            @Override
            public Long convert(final Ticker ticker)
            {
                return ticker.getTickStamp().getTickValue();
            }
        });
        converter.register(new Converter<Long, Ticker>() {
            @Override
            public Ticker convert(final Long value)
            {
                return new Ticker(new TickStamp(value));
            }
        });

        // register converters that convert from String to Link and vice versa
        converter.register(new Converter<String, Link>() {
            @Override
            public Link convert(final String string)
            {
                return new Link(string);
            }
        });
        converter.register(new Converter<Link, String>() {
            @Override
            public String convert(final Link link)
            {
                return link.toString();
            }
        });

        // register converters that convert from String to PhoneNumber and vice versa
        converter.register(new Converter<String, PhoneNumber>() {
            @Override
            public PhoneNumber convert(final String string)
            {
                return new PhoneNumber(string);
            }
        });
        converter.register(new Converter<PhoneNumber, String>() {
            @Override
            public String convert(final PhoneNumber ph)
            {
                return ph.toString();
            }
        });

        // register converters that convert from String to Email and vice versa
        converter.register(new Converter<String, Email>() {
            @Override
            public Email convert(final String string)
            {
                return new Email(string);
            }
        });
        converter.register(new Converter<Email, String>() {
            @Override
            public String convert(final Email email)
            {
                return email.toString();
            }
        });

        return converter;
    }

}
