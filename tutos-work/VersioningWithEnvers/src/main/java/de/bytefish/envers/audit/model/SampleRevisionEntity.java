package de.bytefish.envers.audit.model;

import org.hibernate.envers.RevisionEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.DateFormat;

@Entity
@RevisionEntity
@Table(schema = "envers", name = "revinfo")
public class SampleRevisionEntity extends CustomRevisionEntity {

    public SampleRevisionEntity() {
    }

    public String toString() {
        return "SampleRevisionEntity(revisionNumber = " + getRevisionNumber() + ", revisionDate = " + DateFormat.getDateTimeInstance().format(this.getRevisionDate()) + ")";
    }
}