/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odyssey.services;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Shagy
 */
@javax.ws.rs.ApplicationPath("logout")
public class LogoutResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of LogoutResource
     */
    public LogoutResource() {
    }

    /**
     * Logout an existing user and revoke it's API Key.
     * 
     * @param content JSON Object with user information.
     * @return
     */
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response loggingOutUsers(String content) {
        return Response.status(200).entity("Logging out user with credentials: " + content).build();
    }
}
