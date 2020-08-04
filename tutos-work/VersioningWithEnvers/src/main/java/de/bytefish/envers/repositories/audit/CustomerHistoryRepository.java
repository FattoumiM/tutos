// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.envers.repositories.audit;

import de.bytefish.envers.audit.query.AuditQueryResult;
import de.bytefish.envers.audit.query.AuditQueryUtils;
import de.bytefish.envers.model.Customer;
import de.bytefish.envers.model.CustomerHistory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerHistoryRepository implements ICustomerHistoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<CustomerHistory> listCustomerRevisions(Long customerId) {

        // Create the Audit Reader. It uses the EntityManager, which will be opened when
        // starting the new Transation and closed when the Transaction finishes.
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        // Create the Query:
        AuditQuery auditQuery = auditReader.createQuery()
                .forRevisionsOfEntity(Customer.class, false, true)
                .add(AuditEntity.id().eq(customerId));

        // We don't operate on the untyped Results, but cast them into a List of AuditQueryResult:
        return AuditQueryUtils.getAuditQueryResults(auditQuery, Customer.class).stream()
                // Turn into the CustomerHistory Domain Object:
                .map(x -> getCustomerHistory(x))
                // And collect the Results:
                .collect(Collectors.toList());
    }

    private static CustomerHistory getCustomerHistory(AuditQueryResult<Customer> auditQueryResult) {
        return new CustomerHistory(
                auditQueryResult.getEntity(),
                auditQueryResult.getRevision().getRevisionNumber(),
                auditQueryResult.getType()
        );
    }

}
