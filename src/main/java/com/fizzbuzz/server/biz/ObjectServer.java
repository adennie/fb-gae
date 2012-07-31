package com.fizzbuzz.server.biz;

public abstract class ObjectServer<M>
        extends BaseServer {

    public abstract boolean entityExists(final long id);

    public abstract M get(final long id);

    public abstract M put(final M modelObject);

    public abstract M update(final M modelObject);

    public abstract void delete(final long id);

}
