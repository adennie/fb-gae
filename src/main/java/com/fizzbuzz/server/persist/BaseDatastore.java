package com.fizzbuzz.server.persist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
import com.google.code.twig.configuration.Configuration;
import com.google.code.twig.conversion.CombinedConverter;
import com.google.code.twig.conversion.SpecificConverter;
import com.google.code.twig.standard.StandardObjectDatastore;
import com.google.code.twig.util.EntityToKeyFunction;
import com.google.common.collect.Iterators;

public class BaseDatastore
        extends StandardObjectDatastore {

    public BaseDatastore(final Configuration config) {
        super(config);
    }

    public final void deleteEntityGroup(final Key rootKey) {
        Query query = new Query(rootKey);
        query.setKeysOnly();
        FetchOptions options = FetchOptions.Builder.withChunkSize(100);
        Iterator<Entity> entities = servicePrepare(query).asIterator(options);
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
    protected CombinedConverter createTypeConverter() {
        CombinedConverter converter = super.createTypeConverter();

        // append converters that convert from TickStamp to Long and vice versa
        converter.append(new SpecificConverter<TickStamp, Long>() {
            @Override
            public Long convert(final TickStamp tickStamp)
            {
                return tickStamp.getTickValue();
            }
        });
        converter.append(new SpecificConverter<Long, TickStamp>() {
            @Override
            public TickStamp convert(final Long value)
            {
                return new TickStamp(value);
            }
        });

        // append converters that convert from Ticker to Long and vice versa
        converter.append(new SpecificConverter<Ticker, Long>() {
            @Override
            public Long convert(final Ticker ticker)
            {
                return ticker.getTickStamp().getTickValue();
            }
        });
        converter.append(new SpecificConverter<Long, Ticker>() {
            @Override
            public Ticker convert(final Long value)
            {
                return new Ticker(new TickStamp(value));
            }
        });

        // append converters that convert from String to Link and vice versa
        converter.append(new SpecificConverter<String, Link>() {
            @Override
            public Link convert(final String string)
            {
                return new Link(string);
            }
        });
        converter.append(new SpecificConverter<Link, String>() {
            @Override
            public String convert(final Link link)
            {
                return link.toString();
            }
        });

        // append converters that convert from String to PhoneNumber and vice versa
        converter.append(new SpecificConverter<String, PhoneNumber>() {
            @Override
            public PhoneNumber convert(final String string)
            {
                return new PhoneNumber(string);
            }
        });
        converter.append(new SpecificConverter<PhoneNumber, String>() {
            @Override
            public String convert(final PhoneNumber ph)
            {
                return ph.toString();
            }
        });

        // append converters that convert from String to Email and vice versa
        converter.append(new SpecificConverter<String, Email>() {
            @Override
            public Email convert(final String string)
            {
                return new Email(string);
            }
        });
        converter.append(new SpecificConverter<Email, String>() {
            @Override
            public String convert(final Email email)
            {
                return email.toString();
            }
        });

        return converter;
    }

}
