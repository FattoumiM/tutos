// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.envers.audit.model;

import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

@MappedSuperclass
public class CustomRevisionEntity implements Serializable {

    private static final long serialVersionUID = 8530213963961662300L;

    @Id
    @RevisionNumber
    @GeneratedValue
    @Column(name = "revision_number")
    private int revisionNumber;

    @RevisionTimestamp

    @Column(name = "revision_timestamp")
    private long revisionTimestamp;

    public CustomRevisionEntity() {
    }

    public int getRevisionNumber() {
        return this.revisionNumber;
    }

    @Transient
    public Date getRevisionDate() {
        return new Date(this.revisionTimestamp);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof CustomRevisionEntity)) {
            return false;
        } else {
            CustomRevisionEntity that = (CustomRevisionEntity) o;
            return this.revisionNumber == that.revisionNumber && this.revisionTimestamp == that.revisionTimestamp;
        }
    }

    public int hashCode() {
        int result = this.revisionNumber;
        result = 31 * result + (int) (this.revisionTimestamp ^ this.revisionTimestamp >>> 32);
        return result;
    }

    public String toString() {
        return "CustomRevisionEntity(revisionNumber = " + this.revisionNumber + ", revisionDate = " + DateFormat.getDateTimeInstance().format(this.getRevisionDate()) + ")";
    }
}