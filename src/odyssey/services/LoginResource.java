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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import odyssey.storage.comunication;

import javax.ws.rs.PathParam;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Shagy
 */
@Path("login")
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
     * @throws ParseException 
     * @throws SQLException 
     * @throws ClassNotFoundException 
     */
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response loggingInUsers(String response) throws ParseException, SQLException, ClassNotFoundException {
    	Object obj=JSONValue.parse(response);
    	JSONObject json = (JSONObject)obj;

    	//User
    	String username = (String) json.get("username");
    	String password = (String) json.get("password");
    	System.out.println(username);
    	System.out.println(password);
    	
    	//Debe devolver false si existe  el usuario
    	comunication.getInstance().open();
    	boolean exists_user = comunication.getInstance().validate_user_nick(username);
    	System.out.println(exists_user);
    	//Debe devolver true si las credenciales son correctas
    	boolean password_correct = comunication.getInstance().compare_pass(username, password);
    	System.out.println(password_correct);
    	comunication.getInstance().close();
    	
    	if(!exists_user && password_correct){
    		System.out.println("Succesfully Rekt");
    	}
    	 
    	 return Response.status(200).entity("Logging in user with credentials: " ).build();
    }
    
}
