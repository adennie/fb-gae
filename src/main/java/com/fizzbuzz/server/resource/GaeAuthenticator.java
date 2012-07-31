package com.fizzbuzz.server.resource;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.security.Authenticator;
import org.restlet.security.Enroler;
import org.restlet.security.User;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class GaeAuthenticator
        extends Authenticator {
    private final UserService userService = UserServiceFactory.getUserService();

    public GaeAuthenticator(final Context context) {
        super(context);
    }

    public GaeAuthenticator(final Context context, final boolean optional) {
        super(context, optional);
    }

    public GaeAuthenticator(final Context context, final boolean optional, final Enroler enroler) {
        super(context, optional, enroler);
    }

    @Override
    protected boolean authenticate(final Request request, final Response response) {
        boolean result = false;

        ClientInfo info = request.getClientInfo();
        if (info.isAuthenticated()) {
            result = true;
        }
        else if (userService.isUserLoggedIn()) {
            // The user is logged in, create restlet user.
            com.google.appengine.api.users.User gaeUser = userService
                    .getCurrentUser();
            User restletUser = new User(gaeUser.getUserId());
            restletUser.setEmail(gaeUser.getEmail());
            restletUser.setFirstName(gaeUser.getNickname());
            info.setUser(restletUser);
            info.setAuthenticated(true);
            result = true;
        }

        // TODO: look at user agent - if coming from browswer, redirect to GAE log, if coming from PuSH hub, don't do
        // anything, if coming from Android, maybe return a not authorized status
        return result;
    }
}