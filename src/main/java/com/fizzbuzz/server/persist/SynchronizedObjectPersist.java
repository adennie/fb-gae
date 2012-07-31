package com.fizzbuzz.server.persist;

import com.fizzbuzz.model.SynchronizedObject;
import com.fizzbuzz.model.TickStamp;

public class SynchronizedObjectPersist<M extends SynchronizedObject>
        extends ObjectPersist<M> {
    private TickablePersist mTickablePersist = null;
    private long mTickingObjectId;

    public SynchronizedObjectPersist(final Class<M> modelClass) {
        super(modelClass);
    }

    public SynchronizedObjectPersist(final Class<M> modelClass, final TickablePersist tickablePersist,
            final long tickingObjectId) {
        super(modelClass);
        mTickablePersist = tickablePersist;
        mTickingObjectId = tickingObjectId;
    }

    public void setTickablePersist(final TickablePersist tickablePersist) {
        mTickablePersist = tickablePersist;
    }

    public void setTickerId(final long tickingObjectId) {
        mTickingObjectId = tickingObjectId;
    }

    @Override
    protected void stamp(final M modelObject) {
        super.stamp(modelObject); // timestamp

        // Stamp the object being stored/updated with the tick value from the object's associated ticker.
        // Note: there is one case where mTickablePersist may legitimately be null; this is when the ticker object
        // itself is a synchronized object, and it is being stored (not updated). In that case, we obviously cannot load
        // it from the datastore and fetch its tick value, so just initialize it with a new TickStamp.
        TickStamp t = null;
        if (mTickablePersist != null) {
            t = mTickablePersist.getTickCount(mTickingObjectId);
        }
        else {
            t = new TickStamp(1); // start with 1, so that the object will be picked up on a fetch for all entities > 0
        }
        modelObject.setTickStamp(t);

    }

}
