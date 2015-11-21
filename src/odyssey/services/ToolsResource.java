package odyssey.services;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import odyssey.logic.SessionNode;
import odyssey.logic.Sessions;
import odyssey.security.BCrypt;
import odyssey.storage.MongoJDBC;
import odyssey.storage.comunication;

@Path("tools")
public class ToolsResource {
	@Context
	private UriInfo context;

	/**
	 * Creates a new instance of LibrariesResource
	 * 
	 * @param pUserID
	 */
	public ToolsResource() {
	}

	/**
	 * Omnisearch endpoint
	 * 
	 * @return JSON Object with existing libraries.
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	@GET
	@Path("/search")
	@Produces("application/json")
	public Response queryingLibraries(@QueryParam("query") String query) throws SQLException, ClassNotFoundException {
		System.out.println("Buscando..." + query);

		MongoJDBC mongo = new MongoJDBC();

		comunication com = new comunication();
		com.open();
		List<Integer> sqlResults = com.finder(query);
		com.close();

		JSONObject response = new JSONObject();

		JSONArray results = new JSONArray();
		com.open();
		for (int i = 0; i < sqlResults.size(); i++) {
			List<String> metadata = com.get_songs_lib2(sqlResults.get(i));

			JSONObject result = new JSONObject();
			result.put("type", "song");
			result.put("fa", "music");
			result.put("title", metadata.get(0));
			result.put("date", "unknown");
			result.put("globalid", sqlResults.get(i));
			result.put("owner", query);
			result.put("text", "Song with id, " + sqlResults.get(i) + " found inside " + query + "'s library!");

			results.add(result);
		}
		com.close();

		ArrayList<JSONObject> mongoResults = mongo.findOnComments(query);
		for (int i = 0; i < mongoResults.size(); i++) {
			JSONObject result = new JSONObject();

			result.put("type", "comment");
			result.put("fa", "comment");
			result.put("title", mongoResults.get(i).get("Comment"));
			result.put("date", "unknown");
			result.put("globalid", mongoResults.get(i).get("SongID"));
			result.put("owner", mongoResults.get(i).get("User"));
			result.put("text", mongoResults.get(i).get("User") + " made a comment on the song with id, " + mongoResults.get(i).get("SongID"));

			results.add(result);

		}

		response.put("results", results);

		response.put("count", results.size());

		return Response.status(200).entity(response.toJSONString()).header("Access-Control-Allow-Origin", "*").build();
		/*
		 * JSONArray results = new JSONArray(); JSONObject result1 = new
		 * JSONObject(); result1.put("type","song"); result1.put("fa","music");
		 * result1.put("title", "Dig up her bones"); result1.put("date",
		 * "12/9/14");x result1.put("text",
		 * "Beginner archers have an opportunity to test their skills at The Snowflake Shoot"
		 * );
		 * 
		 * JSONObject result2 = new JSONObject(); result2.put("type","user");
		 * result2.put("fa","user"); result2.put("title", "Santiago");
		 * result2.put("date", "12/9/14"); result2.put("text",
		 * "Un cabezon me dijo una vez");
		 * 
		 * JSONObject result3 = new JSONObject(); result3.put("type","lyrics");
		 * result3.put("fa","file-text"); result3.put("title", "lyrics");
		 * result3.put("date", "12/9/14"); result3.put("text",
		 * "Rayando el sol oh eh oh");
		 * 
		 * JSONObject result4 = new JSONObject(); result4.put("type","comment");
		 * result4.put("fa","comment"); result4.put("title", "comment");
		 * result4.put("date", "12/9/14"); result4.put("text", "jueputa sal!");
		 * 
		 * 
		 * results.add(result1); results.add(result2); results.add(result3);
		 * results.add(result4); return
		 * Response.status(200).entity(results.toJSONString()).header(
		 * "Access-Control-Allow-Origin", "*").build();
		 */
	}

	@POST
	@Path("/recomendations")
	@Produces("application/json")
	@Consumes("application/json")
	public Response friendRecomendations(String content, @QueryParam("type") String type) {
		if (type.compareTo("friends") == 0) {
			Object obj = JSONValue.parse(content);
			JSONObject json = (JSONObject) obj;
			String username = (String) json.get("username");

			JSONArray results = new JSONArray();

			MongoJDBC mongo = new MongoJDBC();
			ArrayList<String> suggestedFriends = mongo.suggestFriends(username);
			for (int i = 0; i < suggestedFriends.size(); i++) {
				JSONObject friend = new JSONObject();
				friend.put("user", suggestedFriends.get(i));
				results.add(friend);
			}
			return Response.status(200).entity(results.toJSONString()).header("Access-Control-Allow-Origin", "*")
					.build();

		} else {
			return Response.status(204).header("Access-Control-Allow-Origin", "*").build();

		}
	}

	@GET
	@Path("/time")
	@Produces("application/json")
	public Response test() {

		JSONObject time = new JSONObject();
		time.put("stamp", new Timestamp(System.currentTimeMillis()));
		return Response.status(200).entity(time.toJSONString()).header("Access-Control-Allow-Origin", "*").build();
	}

	@POST
	@Path("/social")
	@Produces("application/json")
	public Response social(String content, @QueryParam("type") String type)
			throws SQLException, ClassNotFoundException {
		Object obj = JSONValue.parse(content);
		JSONObject json = (JSONObject) obj;

		// User
		String username = (String) json.get("username");
		String token = (String) json.get("token");

		comunication com = new comunication();

		com.open();
		boolean isAdmin = com.valiadmin(username);
		com.close();
		MongoJDBC mongo = new MongoJDBC();
		if (isAdmin) {
			if (type.compareTo("loved_users") == 0) {
				JSONArray result = new JSONArray();

				ArrayList<String> array = mongo.getTopUsers();
				for (int i = 0; i < array.size(); i++) {
					result.add(array.get(i));
				}

				return Response.status(200).entity(result.toJSONString()).header("Access-Control-Allow-Origin", "*")
						.build();
			}
			if (type.compareTo("hated_users") == 0) {
				JSONArray result = new JSONArray();

				ArrayList<String> array = mongo.getHateUsers();
				for (int i = 0; i < array.size(); i++) {
					result.add(array.get(i));
				}


				return Response.status(200).entity(result.toJSONString()).header("Access-Control-Allow-Origin", "*")
						.build();
			}
			if (type.compareTo("loved_songs") == 0) {
				JSONArray result = new JSONArray();

				ArrayList<String> array = mongo.getlovedSongs();
				for (int i = 0; i < array.size(); i++) {
					result.add(array.get(i));
				}

				return Response.status(200).entity(result.toJSONString()).header("Access-Control-Allow-Origin", "*")
						.build();
			}
			if (type.compareTo("hated_songs") == 0) {
				JSONArray result = new JSONArray();

				ArrayList<String> array = mongo.getHateSongs();
				for (int i = 0; i < array.size(); i++) {
					result.add(array.get(i));
				}
				return Response.status(200).entity(result.toJSONString()).header("Access-Control-Allow-Origin", "*")
						.build();
			}
			if (type.compareTo("online_users") == 0) {
				JSONArray result = Sessions.getInstance().getOnlineSessions();
				return Response.status(200).entity(result.toJSONString()).header("Access-Control-Allow-Origin", "*")
						.build();
			} 
			if (type.compareTo("all_users") == 0) {
				JSONArray result = new JSONArray();
				
				com.open();
				List<String> users = com.getallusers();
				com.close();
				
				for(int i = 0; i < users.size(); i++){
					result.add(users.get(i));
				}
				
				return Response.status(200).entity(result.toJSONString()).header("Access-Control-Allow-Origin", "*")
						.build();
			}else {
				return Response.status(404).header("Access-Control-Allow-Origin", "*").build();
			}
		} else {
			return Response.status(401).header("Access-Control-Allow-Origin", "*").build();
		}

	}

	@POST
	@Path("/query")
	@Produces("application/json")
	public Response querySQL(String content, @QueryParam("query") String query)
			throws ClassNotFoundException, SQLException {
		Object obj = JSONValue.parse(content);
		JSONObject json = (JSONObject) obj;

		// User
		String username = (String) json.get("username");
		String token = (String) json.get("token");

		try {
			comunication com = new comunication();

			com.open();
			boolean isAdmin = com.valiadmin(username);
			com.close();

			if (isAdmin) {
				SessionNode session = Sessions.getInstance().find(token);
				// El admin si esta logueado
				if (session != null) {
					if (query.contains("~")) {

						query = query.substring(1, query.length());

						com.open();
						List<List<String>> matrizTabla = com.vertablas(query);
						com.close();

						JSONObject result = new JSONObject();

						JSONArray matriz = new JSONArray();
						JSONArray columnNames = new JSONArray();

						for (int i = 0; i < matrizTabla.get(0).size(); i++) {
							columnNames.add(matrizTabla.get(0).get(i));
						}

						for (int i = 1; i < matrizTabla.size(); i++) {
							JSONArray fila = new JSONArray();
							for (int j = 0; j < matrizTabla.get(0).size(); j++) {
								fila.add(matrizTabla.get(i).get(j));
							}
							matriz.add(fila);
						}
						result.put("matriz", matriz);
						result.put("columnNames", columnNames);

						return Response.status(200).entity(result.toJSONString())
								.header("Access-Control-Allow-Origin", "*").build();
					} else {

						com.open();
						com.query(query);
						com.close();

						JSONObject response = new JSONObject();
						response.put("result", "executed");

						return Response.status(200).entity(response.toJSONString())
								.header("Access-Control-Allow-Origin", "*").build();
					}
				}
				// El admin no esta logueado
				else {
					return Response.status(401).header("Access-Control-Allow-Origin", "*").build();
				}
			} else {
				return Response.status(401).header("Access-Control-Allow-Origin", "*").build();
			}

		} catch (Exception e) {
			System.out.println("Fuck...! with" + query);
			return Response.status(409).header("Access-Control-Allow-Origin", "*").build();
		}

	}
}
