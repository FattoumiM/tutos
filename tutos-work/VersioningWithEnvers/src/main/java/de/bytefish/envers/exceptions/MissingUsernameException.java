// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.envers.exceptions;

public class MissingUsernameException extends RuntimeException {

    public MissingUsernameException() {
    }

    public MissingUsernameException(String message) {
        super(message);
    }

    public MissingUsernameException(String message, Throwable cause) {
        super(message, cause);
    }

}
