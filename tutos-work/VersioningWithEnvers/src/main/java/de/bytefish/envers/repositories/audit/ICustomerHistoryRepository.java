// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.envers.repositories.audit;

import de.bytefish.envers.model.CustomerHistory;

import java.util.List;

public interface ICustomerHistoryRepository {

    List<CustomerHistory> listCustomerRevisions(Long customerId);

}
