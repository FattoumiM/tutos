// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.envers.web.exceptions;

import de.bytefish.envers.web.errors.ServiceError;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class GeneralExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        return Response
                .status(400)
                .entity(new ServiceError(e.getMessage()))
                .build();
    }

}
