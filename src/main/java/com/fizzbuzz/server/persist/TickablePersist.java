package com.fizzbuzz.server.persist;

import com.fizzbuzz.model.TickStamp;

/*
 * helper class for persistent objects that "tick"
 */
public interface TickablePersist {
    public TickStamp getTickCount(final long tickingObjectId);

    public TickStamp tick(final long tickingObjectId);

}
