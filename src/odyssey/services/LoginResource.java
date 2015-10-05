/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odyssey.services;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Shagy
 */
@javax.ws.rs.ApplicationPath("login")
public class LoginResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of LoginResource
     */
    public LoginResource() {
    }
    
    /**
     * Authenticates an existing user.
     * 
     * @param content
     * @return JSON Object with API Key.
     */
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response loggingInUsers(String content) {
        return Response.status(200).entity("Logging in user with credentials: " + content).build();
    }
    
}
