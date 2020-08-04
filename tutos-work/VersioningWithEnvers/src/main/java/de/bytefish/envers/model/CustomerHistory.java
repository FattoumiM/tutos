// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.envers.model;

import org.hibernate.envers.RevisionType;

public class CustomerHistory {

    private final Customer customer;
    private final Number revision;
    private final RevisionType revisionType;

    public CustomerHistory(Customer customer, Number revision, RevisionType revisionType) {
        this.customer = customer;
        this.revision = revision;
        this.revisionType = revisionType;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Number getRevision() {
        return revision;
    }

    public RevisionType getRevisionType() {
        return revisionType;
    }
}