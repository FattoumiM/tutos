// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.envers.web.resources;


import de.bytefish.envers.model.CustomerHistory;
import de.bytefish.envers.repositories.audit.ICustomerHistoryRepository;
import de.bytefish.envers.web.converter.Converters;
import de.bytefish.envers.web.model.CustomerHistoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/history")
public class CustomerHistoryResource {

    private final ICustomerHistoryRepository repository;

    @Autowired
    public CustomerHistoryResource(ICustomerHistoryRepository repository) {
        this.repository = repository;
    }

    @GET
    @Path("customer/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CustomerHistoryDto> getHistory(@PathParam("id") Long id) {
        // Get History:
        List<CustomerHistory> history = repository.listCustomerRevisions(id);

        // Return the DTO List:
        return history.stream()
                .map(Converters::convert)
                .collect(Collectors.toList());
    }
}
