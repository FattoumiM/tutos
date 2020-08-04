// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.envers.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

@Component
public class ThreadLocalStorageAuditorAware implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        return (String) RequestContextHolder
                .currentRequestAttributes()
                .getAttribute("Username", SCOPE_REQUEST);
    }

}
