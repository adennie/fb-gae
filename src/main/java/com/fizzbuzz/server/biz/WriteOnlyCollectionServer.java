package com.fizzbuzz.server.biz;

public abstract class WriteOnlyCollectionServer<M>
        extends BaseServer {

    public abstract M add(final M modelObject);

}
