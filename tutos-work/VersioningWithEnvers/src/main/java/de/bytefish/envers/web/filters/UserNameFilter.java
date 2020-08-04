// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.envers.web.filters;


import de.bytefish.envers.exceptions.MissingUsernameException;
import org.hibernate.Session;
import org.springframework.web.context.request.RequestContextHolder;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

@Provider
public class UserNameFilter implements ContainerRequestFilter {

    Session session;

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {

        MultivaluedMap<String, String> headers = ctx.getHeaders();

//        if(headers == null) {
//            handleError(ctx);
//        }
//
//        if(!headers.containsKey("X-Username")) {
//            handleError(ctx);
//        }
//
//        String username = headers.getFirst("X-Username");
//
//        if(username == null) {
//            handleError(ctx);
//        }

        // Set the Username in the current Request Scope:
        RequestContextHolder
                .currentRequestAttributes()
                .setAttribute("Username", "Mahe", SCOPE_REQUEST);
    }

    public void handleError(ContainerRequestContext ctx) {
        throw new MissingUsernameException("Request is missing a Username");
    }
}
