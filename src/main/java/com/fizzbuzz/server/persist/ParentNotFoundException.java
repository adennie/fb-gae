package com.fizzbuzz.server.persist;

import com.fizzbuzz.exception.NotFoundException;

public class ParentNotFoundException
        extends NotFoundException {
    private static final long serialVersionUID = 1L;
    private final long mParentId;

    public ParentNotFoundException(final long parentId) {
        super(generateMessage(parentId));
        mParentId = parentId;
    }

    public ParentNotFoundException(final long parentId, final Throwable cause) {
        super(generateMessage(parentId), cause);
        mParentId = parentId;
    }

    public long getParentId() {
        return mParentId;
    }

    private static String generateMessage(final long parentId) {
        return "No entity found for parent ID=" + Long.toString(parentId);
    }
}
