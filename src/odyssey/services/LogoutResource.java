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

import odyssey.logic.SessionObject;
import odyssey.logic.Sessions;
import odyssey.security.BCrypt;
import odyssey.storage.comunication;

import java.sql.Timestamp;

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
@Path("logout")
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
		Object obj = JSONValue.parse(content);
		JSONObject json = (JSONObject) obj;

		// User
		String username = (String) json.get("username");
		String token = (String) json.get("token");
		
		SessionObject session = Sessions.getInstance().find(token).getSession();
		
		if(session != null && session.getUser().compareTo(username) == 0){
			//El token si pertenece a ese usuario, es seguro desconectarlo.
			System.out.println("SERVER: User, " + username + " disconnecting!");
			Sessions.getInstance().deleteSession(token);
			return Response.status(200).build();
		}
		else {
			//El usuario que se esta tratando de desconectar no posee esa combinacion de credenciales.
			return Response.status(403).build();
		}


    }
}
