package com.fizzbuzz.server.biz;

abstract public class ReadOnlySingletonServer<M>
        extends BaseServer {

    abstract public M get();
}
