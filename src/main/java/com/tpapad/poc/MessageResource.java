
package com.tpapad.poc;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author tpapad
 */
@Path("/messages")
public class MessageResource {

    @Inject
    MessageService messageService;

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String submitMessage(final String message) {
        messageService.submitMessage(message);
        return "OK";
    }

}
