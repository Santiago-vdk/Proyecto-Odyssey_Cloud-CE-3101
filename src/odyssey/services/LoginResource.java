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

import odyssey.logic.SessionNode;
import odyssey.logic.SessionObject;
import odyssey.logic.Sessions;
import odyssey.security.BCrypt;
import odyssey.storage.comunication;

import javax.ws.rs.PathParam;

import java.sql.SQLException;
import java.sql.Timestamp;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
		Object obj = JSONValue.parse(response);
		JSONObject json = (JSONObject) obj;

		// User
		String username = (String) json.get("username");
		String password = (String) json.get("password");

		// Comunication instance
		comunication com = new comunication();

		com.open();
		boolean isAdmin = com.valiadmin(username);
		com.close();

		if (isAdmin) {
			// Debe devolver false si existe el usuario
			com.open();
			boolean exists_user = com.validate_user_nick(username);
			com.close();

			// Debe devolver true si las credenciales son correctas
			com.open();
			boolean password_correct = com.compare_pass(username, password);
			com.close();

			if (!exists_user && password_correct) {
				// Usuario autenticado, reviso que no haya una sesion activa
				// para ese usuario
				SessionNode session = Sessions.getInstance().findByUsername(username);
				if (session == null) {
					// Sesion para ese usuario no existe
					System.out.println("SERVER ADMIN: " + username + " connecting!");
					String time = new Timestamp(System.currentTimeMillis()).toString();
					String toHash = password + time;
					String hashed = BCrypt.hashpw(toHash, BCrypt.gensalt());
					Sessions.getInstance().createSession(username, hashed, time, "");

					JSONObject jsontoken = new JSONObject();
					jsontoken.put("token", hashed);
					jsontoken.put("admin", true);

					return Response.status(200).entity(jsontoken.toJSONString()).build();
				} else {
					return Response.status(403).build();
				}

			} else {
				return Response.status(401).build();
			}
		} else {

			// Debe devolver false si existe el usuario
			com.open();
			boolean exists_user = com.validate_user_nick(username);
			com.close();

			// Debe devolver true si las credenciales son correctas
			com.open();
			boolean password_correct = com.compare_pass(username, password);
			com.close();

			if (!exists_user && password_correct) {
				// Usuario autenticado, reviso que no haya una sesion activa
				// para ese usuario
				SessionNode session = Sessions.getInstance().findByUsername(username);
				if (session == null) {
					// Sesion para ese usuario no existe
					System.out.println("SERVER: User, " + username + " connecting!");
					String time = new Timestamp(System.currentTimeMillis()).toString();
					String toHash = password + time;
					String hashed = BCrypt.hashpw(toHash, BCrypt.gensalt());
					Sessions.getInstance().createSession(username, hashed, time, "");

					JSONObject jsontoken = new JSONObject();
					jsontoken.put("token", hashed);

					return Response.status(200).entity(jsontoken.toJSONString()).build();
				} else {
					return Response.status(403).build();
				}

			} else {
				return Response.status(401).build();
			}
		}
	}

	@GET
	@Path("token")
	@Produces("application/json")
	public Response checkToken(String content, @QueryParam("token") String token) {
		SessionNode session = Sessions.getInstance().find(token);
		if (session == null) {
			// La sesion no existe
			return Response.status(204).build();
		} else {
			// El token si existe
			return Response.status(200).build();
		}
	}

}
