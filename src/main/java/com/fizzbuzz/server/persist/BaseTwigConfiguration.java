package com.fizzbuzz.server.persist;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.fizzbuzz.model.PersistentObject;
import com.google.appengine.api.datastore.Text;
import com.google.code.twig.configuration.DefaultConfiguration;

public class BaseTwigConfiguration
        extends DefaultConfiguration {

    public BaseTwigConfiguration() {
        super(0);
    }

    @Override
    public int activationDepth(final Field field, final int depth) {
        return depth;
    }

    @Override
    public long allocateIdsFor(final Type type) {
        return 0;
    }

    @Override
    public boolean id(final Field field) {
        return (field.getName().equals("mId") && field.getDeclaringClass().equals(PersistentObject.class));
    }

    @Override
    public boolean parent(final Field field) {
        return false;
    }

    @Override
    public boolean child(final Field field) {
        return false;
    }

    @Override
    public boolean embed(final Field field) {
        return false;
    }

    @Override
    public boolean entity(final Field field) {
        return false;
    }

    @Override
    public boolean index(final Field field) {
        if (field.getName().equals("mTickStamp"))
            return true;

        return false;
    }

    @Override
    public boolean store(final Field field) {
        return true;
    }

    @Override
    public boolean polymorphic(final Field field) {
        return false;
    }

    @Override
    public boolean polymorphic(final Class<?> instance) {
        return false;
    }

    @Override
    public boolean key(final Field field) {
        return false;
    }

    @Override
    public Type typeOf(final Field field) {
        String fieldName = field.getName();

        // Text fields (up to 1MB, not indexable). Remember, String fields are limited to 500 characters.
        if (fieldName.equals("mNotes")) // all fields named "mNotes" are Texts
            return Text.class;

        return super.typeOf(field);
    }
}
