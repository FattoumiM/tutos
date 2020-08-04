// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.envers.web.errors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceError {

    private final String message;

    public ServiceError(String message) {
        this.message = message;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
}
