// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.envers.web.converter;

import de.bytefish.envers.model.Customer;
import de.bytefish.envers.model.CustomerHistory;
import de.bytefish.envers.web.model.CustomerDto;
import de.bytefish.envers.web.model.CustomerHistoryDto;
import de.bytefish.envers.web.model.RevisionTypeDto;
import org.hibernate.envers.RevisionType;

public class Converters {

    public static CustomerDto convert(Customer source) {

        if(source == null) {
            return null;
        }

        return new CustomerDto(source.getId(), source.getFirstName(), source.getLastName());
    }

    public static Customer convert(CustomerDto source) {

        if(source == null) {
            return null;
        }

        return new Customer(source.getId(), source.getFirstName(), source.getLastName());
    }

    public static RevisionTypeDto convert(RevisionType source) {

        if(source == null) {
            return null;
        }

        switch (source) {
            case ADD:
                return RevisionTypeDto.ADD;
            case MOD:
                return RevisionTypeDto.MOD;
            case DEL:
                return RevisionTypeDto.DEL;
            default:
                throw new IllegalArgumentException("source");
        }
    }

    public static CustomerHistoryDto convert(CustomerHistory source) {

        if(source == null) {
            return null;
        }

        CustomerDto customerDto = convert(source.getCustomer());
        Long revision = source.getRevision().longValue();
        RevisionTypeDto revisionTypeDto = convert(source.getRevisionType());

        return new CustomerHistoryDto(customerDto, revision, revisionTypeDto);
    }
}
