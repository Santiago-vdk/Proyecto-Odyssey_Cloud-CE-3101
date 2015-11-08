package odyssey.services;

import java.sql.Timestamp;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Path("tools")
public class ToolsResource {
	@Context
	private UriInfo context;

	/**
     * Creates a new instance of LibrariesResource
     * @param pUserID
     */
    public ToolsResource() {}

	/**
	 * Omnisearch endpoint
	 * 
	 * @return JSON Object with existing libraries.
	 */
    @POST
    @Path("/search")
    @Produces("application/json")
    @Consumes("application/json")
	public Response queryingLibraries(String response) {
    	
    	System.out.println("Buscando..." + response);
    	
    	JSONArray results = new JSONArray();
    	JSONObject result1 = new JSONObject();
    	result1.put("type","song");
    	result1.put("fa","music");
    	result1.put("title", "Dig up her bones");
    	result1.put("date", "12/9/14");
    	result1.put("text", "Beginner archers have an opportunity to test their skills at The Snowflake Shoot");
    	
    	JSONObject result2 = new JSONObject();
    	result2.put("type","user");
    	result2.put("fa","user");
    	result2.put("title", "Santiago");
    	result2.put("date", "12/9/14");
    	result2.put("text", "Un cabezon me dijo una vez");
    	
    	JSONObject result3 = new JSONObject();
    	result3.put("type","lyrics");
    	result3.put("fa","file-text");
    	result3.put("title", "lyrics");
    	result3.put("date", "12/9/14");
    	result3.put("text", "Rayando el sol oh eh oh");
    	
    	JSONObject result4 = new JSONObject();
    	result4.put("type","comment");
    	result4.put("fa","comment");
    	result4.put("title", "comment");
    	result4.put("date", "12/9/14");
    	result4.put("text", "jueputa sal!");
    	
    	
    	results.add(result1);
    	results.add(result2);
    	results.add(result3);
    	results.add(result4);
		return Response.status(200).entity(results.toJSONString()).header("Access-Control-Allow-Origin", "*").build();
	}
    
    @POST
    @Path("/recomendations")
    @Produces("application/json")
    @Consumes("application/json")
	public Response friendRecomendations(String response) {
    	JSONArray results = new JSONArray();
    	JSONObject friend1 = new JSONObject();
    	friend1.put("user", "Gracie");
    	
    	JSONObject friend2 = new JSONObject();
    	friend2.put("user", "McQuiddy");
    	
    	JSONObject friend3 = new JSONObject();
    	friend3.put("user", "Ronaldo");
    	
    	results.add(friend1);
    	results.add(friend2);
    	results.add(friend3);
    	
		return Response.status(200).entity(results.toJSONString()).header("Access-Control-Allow-Origin", "*").build();
	}
    
    @GET
    @Path("/time")
    @Produces("application/json")
	public Response test() {
    	
    	JSONObject time = new JSONObject();
    	time.put("stamp",new Timestamp(System.currentTimeMillis()));
		return Response.status(200).entity(time.toJSONString()).header("Access-Control-Allow-Origin", "*").build();
	}
}
