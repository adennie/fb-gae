package com.fizzbuzz.server.resource;

import java.util.Map;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.fizzbuzz.resource.UriHelper;

public class UriParser {
    private final ServerResource mServerResource;

    public UriParser(final ServerResource serverResource) {
        mServerResource = serverResource;
    }

    public long getId() throws ResourceException {
        return getLongTokenValue(UriHelper.URL_TOKEN_ID);
    }

    public long getLongTokenValue(final String token) {

        String s = (String) getServerResource().getRequestAttributes().get(token);

        if (s == null) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "missing " + token + " URL component: " + s);
        }

        try {
            return Long.parseLong(s);
        }
        catch (Exception e) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "invalid " + token + " URL component: " + s);
        }
    }

    public long getLongParamValue(final String paramName) {
        String paramAsString = null;

        Map<String, String> params = mServerResource.getQuery().getValuesMap();
        paramAsString = params.get(paramName);
        return Long.parseLong(paramAsString);
    }

    public String getStringParamValue(final String paramName) {
        Map<String, String> params = mServerResource.getQuery().getValuesMap();
        return params.get(paramName);

    }

    public ServerResource getServerResource() {
        return mServerResource;
    }
}
