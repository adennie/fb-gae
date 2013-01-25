package com.fizzbuzz.server.resource;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.fizzbuzz.resource.UriHelper;
import com.google.common.collect.ImmutableMap;

public abstract class ServerUriHelper {
    private ImmutableMap<Class<? extends ServerResource>, String> mServerResourceClassToUriPatternMap;
    private final UriHelper mUriHelper;

    protected ServerUriHelper(final UriHelper uriHelper) {
        mUriHelper = uriHelper;
    }

    protected void init(final Package pkg) {
        initUriPatternToServerResourceClassMap(pkg);
    }

    private void initUriPatternToServerResourceClassMap(final Package pkg) {
        ImmutableMap.Builder<Class<? extends ServerResource>, String> builder = new ImmutableMap.Builder<Class<? extends ServerResource>, String>();
        for (Map.Entry<Class<?>, String> entry : mUriHelper.geResourceInterfaceTotUriPatternMapEntries()) {
            String serverClassName = entry.getKey().getSimpleName().replace("Resource", "ServerResource");
            String fqServerClassName = pkg.getName() + "." + serverClassName;

            try {
                @SuppressWarnings("unchecked")
                Class<? extends ServerResource> serverResourceClass =
                        (Class<? extends ServerResource>) Class.forName(fqServerClassName);
                builder.put(serverResourceClass, entry.getValue());
            }
            catch (ClassNotFoundException e) {
                throw new ResourceException(e);
            }
        }
        mServerResourceClassToUriPatternMap = builder.build();
    }

    public String getUriForServerResourceClass(final Class<? extends ServerResource> serverResourceClass,
            final ImmutableMap<String, String> uriTokenToValueMap) {
        String uriPattern = getUriPatternForServerResourceClass(serverResourceClass);
        return formatUriTemplate(uriPattern, uriTokenToValueMap);
    }

    public String getUriPatternForServerResourceClass(final Class<? extends ServerResource> serverResourceClass) {
        return mServerResourceClassToUriPatternMap.get(serverResourceClass);
    }

    public Set<Entry<Class<? extends ServerResource>, String>> getServerResourceClassToUriPatternMapEntries() {
        return mServerResourceClassToUriPatternMap.entrySet();
    }

    public String formatUriTemplate(final String uriTemplate,
            final ImmutableMap<String, String> uriTokenToValueMap) {
        // pass thru to UriHelper
        return mUriHelper.formatUriTemplate(uriTemplate, uriTokenToValueMap);
    }

}
