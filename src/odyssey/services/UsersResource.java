/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odyssey.services;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import odyssey.storage.comunication;

import javax.ws.rs.PathParam;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
@Path("users")
public class UsersResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of UsersResource
     */
    public UsersResource() {
    }

    /**
     * Queries the database looking for all the users.
     * 
     * @return JSON Object with data.
     */
    @GET
    @Produces("application/json")
    public Response queryingUsers() {
        return Response.status(200).entity("Querying users").build();
    }

    /**
     * Registers new user in the database.
     * 
     * @param content JSON Object with hashed information.
     * @return
     * @throws SQLException 
     * @throws ClassNotFoundException 
     */
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response registeringUsers(String content) throws SQLException, ClassNotFoundException {
    	Object obj=JSONValue.parse(content);
    	JSONObject json = (JSONObject)obj;

    	//User
    	String username = (String) json.get("username");
    	String password = (String) json.get("password");
    	System.out.println(username);
    	System.out.println(password);
    	
    	
    	boolean tam = comunication.getInstance().validate_user_length(username);
    	System.out.println(tam);
		comunication.getInstance().open();
    	boolean existe = comunication.getInstance().validate_user_nick(username);
    	comunication.getInstance().close();    	
    	System.out.println(existe);
    	if(tam && existe){
    		comunication.getInstance().open();
        	comunication.getInstance().new_user(username, password);  
        	comunication.getInstance().close();
    	}

        return Response.status(200).entity("Querying users").build();
    }
    
    /**
     * Retrieves information of a particular user.
     * 
     * @param userID 
     * @return JSON Object with user data.
     */
    @GET
    @Path("{userID}")
    @Produces("application/json")
    public Response retrievingUsers(@PathParam("userID") String userID) {
        return Response.status(200).entity("Retrieving user with id: " + userID).build();
    }
    
    /**
     * Updates information of a particular user.
     * 
     * @param userID
     * @return 
     */
    @PUT
    @Path("{userID}")
    @Consumes("application/json")
    public Response updatingUsers(@PathParam("userID") String userID) {
        return Response.status(200).entity("Updating user with id: " + userID).build();
    }
    
    /**
     * Deletes a specified user from the database.
     * 
     * @param userID
     * @return
     */
    @DELETE
    @Path("{userID}")
    @Consumes("application/json")
    public Response deletingUsers(@PathParam("userID") String userID) {
        return Response.status(200).entity("Deleting user with id: " + userID).build();
    }
    
    
    /**
     * Retrieves a SongResource instance.
     * 
     * @param _userID
     * @return
     */
    @Path("{userID}/libraries")
    public LibrariesResource getSongsResource(@PathParam("userID") String _userID) {
        return new LibrariesResource(_userID);
    }
    
}
