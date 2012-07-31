package com.fizzbuzz.server.biz;

public abstract class ReadWriteSingletonServer<M>
        extends ReadOnlySingletonServer<M> {

    abstract public void set(final M modelObject);

    abstract public void clear();
}
