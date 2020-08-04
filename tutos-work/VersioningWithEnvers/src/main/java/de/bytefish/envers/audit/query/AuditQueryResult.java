// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.envers.audit.query;

import de.bytefish.envers.audit.model.CustomRevisionEntity;
import org.hibernate.envers.RevisionType;

public class AuditQueryResult<T> {

    private final T entity;
    private final CustomRevisionEntity revision;
    private final RevisionType type;

    public AuditQueryResult(T entity, CustomRevisionEntity revision, RevisionType type) {
        this.entity = entity;
        this.revision = revision;
        this.type = type;
    }

    public T getEntity() {
        return entity;
    }

    public CustomRevisionEntity getRevision() {
        return revision;
    }

    public RevisionType getType() {
        return type;
    }
}
