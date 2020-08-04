// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.envers.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerHistoryDto {

    private final CustomerDto customer;
    private final Long revision;
    private final RevisionTypeDto type;

    public CustomerHistoryDto(CustomerDto customer, Long revision, RevisionTypeDto type) {
        this.customer = customer;
        this.revision = revision;
        this.type = type;
    }

    @JsonProperty("customer")
    public CustomerDto getCustomer() {
        return customer;
    }

    @JsonProperty("revision")
    public Long getRevision() {
        return revision;
    }

    @JsonProperty("type")
    public RevisionTypeDto getType() {
        return type;
    }
}
