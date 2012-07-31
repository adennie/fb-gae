package com.fizzbuzz.server.resource;

import java.lang.reflect.Constructor;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.fizzbuzz.server.biz.ObjectServer;

public class Utils {

    public static long checkObjectExists(final ServerResource r,
            final ObjectServer<?> server) {
        UriParser p = new UriParser(r);
        long id = p.getId();
        checkResourceExists(server.entityExists(id), "object id: " + Long.toBinaryString(id));
        return id;
    }

    public static void checkResourceExists(final boolean expression, final Object errorMessage) {
        if (!expression) {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, errorMessage.toString());
        }
    }

    public static <T> T newInstance(final Class<T> clazz) {
        T result = null;

        try {
            result = clazz.newInstance();
        }
        catch (Exception e) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "failed to instantiate class: " + clazz.getName());

        }
        return result;
    }

    public static <T> T newInstance(final Constructor<T> ctor, final Object... args) {
        T result = null;

        try {
            result = ctor.newInstance(args);
        }
        catch (Exception e) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "failed to instantiate class: "
                    + ctor.getClass().getName());

        }
        return result;
    }

    public static <T> Constructor<T> getConstructor(final Class<T> clazz, final Class<?>... paramTypes) {
        Constructor<T> result = null;
        try {
            result = clazz.getConstructor(paramTypes);
        }
        catch (Exception e) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "failed to get constructor for class"
                    + clazz.getName());

        }
        return result;

    }
}
