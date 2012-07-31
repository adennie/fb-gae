package com.fizzbuzz.server.biz;

public abstract class ReadOnlyCollectionServer<C>
        extends BaseServer {

    public abstract C get();

}
