package com.fizzbuzz.server.resource;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.resource.Directory;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.utils.SystemProperty;

public abstract class BaseApplication
        extends Application {

    private final ServerUriHelper mServerUriHelper;
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    public enum ExecutionContext {
        DEVELOPMENT,
        PRODUCTION
    }

    private static ExecutionContext mExecutionContext;

    static {
        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production)
            mExecutionContext = ExecutionContext.PRODUCTION;
        else
            mExecutionContext = ExecutionContext.DEVELOPMENT;
    }

    protected BaseApplication(final ServerUriHelper serverUriHelper) {
        mServerUriHelper = checkNotNull(serverUriHelper, "serverUriHelper");

        setOwner("Fizz Buzz LLC");
        setAuthor("The Fizz Buzz Team");
    }

    public static ExecutionContext getExecutionContext() {
        return mExecutionContext;
    }

    public static boolean inDevelopmentContext() {
        return mExecutionContext == ExecutionContext.DEVELOPMENT;
    }

    public static boolean inProductionContext() {
        return mExecutionContext == ExecutionContext.PRODUCTION;
    }

    @Override
    public Restlet createInboundRoot() {
        Restlet result = null;
        try {
            Router router = new Router(getContext());

            router.attachDefault(new Directory(getContext(), "war:///"));

            for (Map.Entry<Class<? extends ServerResource>, String> entry : mServerUriHelper
                    .getServerResourceClassToUriPatternMapEntries()) {
                router.attach(entry.getValue(), entry.getKey());
            }

            // GaeAuthenticator guard = new GaeAuthenticator(getContext());
            // GaeEnroler enroler = new GaeEnroler(new Role("admin", "Administrator"));
            // guard.setEnroler(enroler);
            // guard.setNext(router);
            // result = guard;

            result = router;
        }
        catch (RuntimeException e) {
            mLogger.warn("BaseApplication.createInboundRoot: exception caught:", e);
            throw e;
        }

        return result;

    }

    @Override
    public void handle(final Request request, final Response response) {
        mLogger.info("BaseApplication.handle: request received - {}", request);
        super.handle(request, response);
    }

    public ServerUriHelper getUriHelper() {
        return mServerUriHelper;
    }

}
