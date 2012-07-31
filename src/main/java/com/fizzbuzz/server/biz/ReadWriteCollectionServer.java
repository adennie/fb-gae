package com.fizzbuzz.server.biz;

public abstract class ReadWriteCollectionServer<C, M>
        extends ReadOnlyCollectionServer<C> {

    public abstract M add(final M modelObject);

    public abstract void deleteAll();

    public abstract void delete(C collection);

}
