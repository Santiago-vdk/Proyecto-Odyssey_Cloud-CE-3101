/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odyssey.services;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import odyssey.logic.SessionObject;
import odyssey.logic.Sessions;
import odyssey.security.BCrypt;
import odyssey.storage.MongoJDBC;
import odyssey.storage.comunication;

import javax.ws.rs.PathParam;

import java.sql.SQLException;
import java.sql.Timestamp;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
	 * @param content
	 *            JSON Object with hashed information.
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response registeringUsers(String content) throws SQLException, ClassNotFoundException {
		Object obj = JSONValue.parse(content);
		JSONObject json = (JSONObject) obj;

		// User
		String username = (String) json.get("username");
		String password = (String) json.get("password");

		// Valido longitud del nombre de usuario nuevo
		boolean tam = comunication.getInstance().validate_user_length(username);

		// Valido si el nombre de usario existe
		comunication.getInstance().open();
		boolean existe = comunication.getInstance().validate_user_nick(username);
		comunication.getInstance().close();

		// Si no existe el nombre de usuario lo inserto.
		if (tam && existe) {
			System.out.println("SERVER: Creating new user, " + username + "!");
			comunication.getInstance().open();
			comunication.getInstance().new_user(username, password);
			comunication.getInstance().close();

			// Insercion MongoDB
			MongoJDBC.getInstance().openConnection();
			MongoJDBC.getInstance().addUser(username, "null", "null");
			MongoJDBC.getInstance().closeConnection();

			return Response.status(201).build();
		} else {
			// Procesamiento fallo
			return Response.status(202).build();
		}
	}

	@POST
	@Path("/me")
	@Produces("application/json")
	@Consumes("application/json")
	public Response me(String content) {
		Object obj = JSONValue.parse(content);
		JSONObject json = (JSONObject) obj;

		// User
		String token = (String) json.get("token");
		SessionObject session = Sessions.getInstance().find(token).getSession();
		if (session != null) {
			JSONObject response = new JSONObject();
			response.put("id", session.getUser());

			JSONArray amigos = new JSONArray();

			JSONObject amigo1 = new JSONObject();
			amigo1.put("user", "McQUiddy");
			amigos.add(amigo1);

			JSONObject amigo2 = new JSONObject();
			amigo2.put("user", "Cristian");
			amigos.add(amigo2);

			JSONObject amigo3 = new JSONObject();
			amigo3.put("user", "Casimiro");
			amigos.add(amigo3);

			JSONObject amigo4 = new JSONObject();
			amigo4.put("user", "Palmera");
			amigos.add(amigo4);

			JSONObject amigo5 = new JSONObject();
			amigo5.put("user", "Cali");
			amigos.add(amigo5);

			JSONObject amigo6 = new JSONObject();
			amigo6.put("user", "Fio");
			amigos.add(amigo6);

			response.put("friends", amigos);
			return Response.status(200).entity(response.toJSONString()).header("Access-Control-Allow-Origin", "*")
					.build();

		} else {
			return Response.status(403).header("Access-Control-Allow-Origin", "*").build();
		}

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
		JSONObject response = new JSONObject();
		response.put("id", userID);
		JSONArray amigos = new JSONArray();

		JSONObject amigo1 = new JSONObject();
		amigo1.put("user", "McQUiddy");
		amigos.add(amigo1);

		JSONObject amigo2 = new JSONObject();
		amigo2.put("user", "Cristian");
		amigos.add(amigo2);

		JSONObject amigo3 = new JSONObject();
		amigo3.put("user", "Casimiro");
		amigos.add(amigo3);

		JSONObject amigo4 = new JSONObject();
		amigo4.put("user", "Palmera");
		amigos.add(amigo4);

		JSONObject amigo5 = new JSONObject();
		amigo5.put("user", "Cali");
		amigos.add(amigo5);

		JSONObject amigo6 = new JSONObject();
		amigo6.put("user", "Fio");
		amigos.add(amigo6);

		response.put("friends", amigos);
		return Response.status(200).entity(response.toJSONString()).header("Access-Control-Allow-Origin", "*").build();
	}

	/**
	 * Updates information of a particular user, generally the users password.
	 * Add friends, etc etc
	 * 
	 * @param userID
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@PUT
	@Path("{userID}")
	@Consumes("application/json")
	public Response updatingUsers(String content, @PathParam("userID") String userID, @QueryParam("type") String type)
			throws SQLException, ClassNotFoundException {

		if (type.compareTo("password") == 0) {
			Object obj = JSONValue.parse(content);
			JSONObject json = (JSONObject) obj;

			// User
			String username = (String) json.get("username");
			String old_password = (String) json.get("old_password");
			String new_password = (String) json.get("new_password");

			// Debe devolver false si existe el usuario
			comunication.getInstance().open();
			boolean exists_user = comunication.getInstance().validate_user_nick(username);

			// Debe devolver true si las credenciales son correctas
			boolean password_correct = comunication.getInstance().compare_pass(username, old_password);
			comunication.getInstance().open();

			if (!exists_user && password_correct) {
				// Usuario autenticado
				System.out.println("SERVER: User, " + username + " changed his/her password!");
				comunication.getInstance().open();
				comunication.getInstance().update_pass(username, new_password);
				comunication.getInstance().open();
				return Response.status(200).build();
			} else {
				return Response.status(401).build();
			}
		} 
		else if(type.compareTo("friend") == 0){
			Object obj = JSONValue.parse(content);
			JSONObject json = (JSONObject) obj;

			// User
			String username = (String) json.get("username");
			String token = (String) json.get("token");
			String friend = (String) json.get("friend");
			
			String tToken = Sessions.getInstance().find(token).getSession().getToken(); //Falta validar mas
			if(tToken.compareTo(token)==0){
				//Si lo esta solicitando el usuario correcto
				System.out.println(username + " added, " + friend + " as friend! <3");
				
				MongoJDBC.getInstance().openConnection();
				MongoJDBC.getInstance().addFriend(username, friend);
				MongoJDBC.getInstance().closeConnection();
				
				return Response.status(201).build();
			} else {
				//Unauthorized
				return Response.status(401).build();
			}
			
		}
		else {
			//Malformed syntax
			return Response.status(400).build();
		}
		

	}

	/**
	 * Deletes a specified user from the database.
	 * 
	 * @param userID
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@DELETE
	@Path("{userID}")
	@Consumes("application/json")
	public Response deletingUsers(String content, @PathParam("userID") String userID)
			throws ClassNotFoundException, SQLException {
		Object obj = JSONValue.parse(content);
		JSONObject json = (JSONObject) obj;

		// User
		String username = (String) json.get("username");
		String password = (String) json.get("password");
		String token = (String) json.get("token");

		// Debe devolver false si existe el usuario
		comunication.getInstance().open();
		boolean exists_user = comunication.getInstance().validate_user_nick(username);

		// Debe devolver true si las credenciales son correctas
		boolean password_correct = comunication.getInstance().compare_pass(username, password);
		comunication.getInstance().open();

		if (!exists_user && password_correct) {
			// Confirmo que el usuario si existe en la BD, ahora verifico que su
			// sesion este activa
			SessionObject session = Sessions.getInstance().find(token).getSession();
			if (session != null && session.getUser().compareTo(username) == 0) {
				// Confirmo que la sesion si esta activa, primero lo desconecto
				Sessions.getInstance().deleteSession(token);

				// Ahora lo borro de la BD
				System.out.println("SERVER: Deleting, " + username + " account!");
				comunication.getInstance().open();
				comunication.getInstance().drop_user(username);
				comunication.getInstance().open();
				return Response.status(200).build();
			} else {
				// No autorizado
				return Response.status(401).build();
			}

		} else {
			// Acceso denegado
			return Response.status(403).build();
		}

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
