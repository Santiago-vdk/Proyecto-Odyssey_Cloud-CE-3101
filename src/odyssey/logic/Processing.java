package odyssey.logic;

import java.sql.SQLException;
import java.util.List;

//import org.glassfish.jersey.internal.util.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import odyssey.storage.comunication;

public class Processing {
	final String _serverPath = "http://192.168.1.135:9080/OdysseyCloud/api/v1/";

	
	public JSONArray retrieveSongs(String pUsername) throws ClassNotFoundException, SQLException{
		comunication com = new comunication();
		com.open();
		List<Integer> songs = com.get_song_id(pUsername);
		com.close();
		
		JSONArray result = new JSONArray();
		
		for (int i = 0; i < songs.size(); i++) {
			JSONObject procedure = new JSONObject();
			procedure.put("method", "GET");
			procedure.put("url", _serverPath + "users/" + pUsername + "/libraries/1/songs/" + songs.get(i) + "/?data=all");
			result.add(procedure);
		}
		
		return result;
	}
	
	public boolean existsLocaly(JSONArray localArray, int globalID){
		for(int i = 0; i < localArray.size(); i++){
			JSONObject object = (JSONObject) localArray.get(i);
			int localID = Integer.parseInt((String) object.get("id"));
			if(localID == globalID){
				return true; //Existe en la local y en la global
			}
		}
		return false; //Existe en la global y no en la local
	}
	
	/*
	 * Recibe la metadata de la biblioteca local de un usuario Define que
	 * canciones requieren, Tipo 1: Nueva insercion (metadata y blob) Tipo 2:
	 * Actualizacion cancion (metadata) Tipo 3: Sin cambios (nada) [ {
	 * "mathod":POST/PUT/DELETE,
	 * "url":http://192.168.1.135:9080/OdysseyCloud/api/v1/{username}/1/songs/{
	 * id} } ]
	 * 
	 * 
	 */
	public JSONArray processSongs(String pUsername, JSONArray pSongsArray) throws SQLException, NumberFormatException, ClassNotFoundException {
		JSONArray result = new JSONArray();
		System.out.println(pSongsArray);
		
		//Comunication instance
		comunication com = new comunication();
		
		com.open();
		List<Integer> globalArray = com.idslocal(pUsername);
		com.close();
		
		for(int i = 0; i < globalArray.size(); i ++){
			int globalID = globalArray.get(i);
			
			if(!existsLocaly(pSongsArray, globalID)){
				//Se debe borrar
				JSONObject procedure = new JSONObject();
				procedure.put("method", "DELETE");
				
				com.open();
				int idSong = com.getid(pUsername, globalID);
				com.close();
				
				procedure.put("url", _serverPath + "users/" +  pUsername + "/libraries/1/songs/" + idSong);//Necesito id global de la cancion
				result.add(procedure);
			}
		}
		
		
		for (int i = 0; i < pSongsArray.size(); i++) {
			JSONObject songJSON = (JSONObject) pSongsArray.get(i);

			com.open();
			boolean exists = com.exist(pUsername, Integer.parseInt((String) songJSON.get("id")));
			com.close();
			// Si no existe debo insertar toda la cancion con BLOB
			if (!exists) {
				JSONObject procedure = new JSONObject();
				procedure.put("method", "POST");
				procedure.put("url", _serverPath + "users/" + pUsername + "/libraries/1/songs/");
				result.add(procedure);
				
			// En caso de si existir
			} 
			else {
				// En caso de si existir reviso si su metadata cambio
				com.open();
				boolean needs_update = com.have_changes(pUsername,Integer.parseInt((String) songJSON.get("id")),
						songJSON.get("title").toString(), songJSON.get("artist").toString(),
						songJSON.get("album").toString(), songJSON.get("year").toString(),
						songJSON.get("genre").toString(), songJSON.get("lyrics").toString());
				com.close();
				// Necesita actualizacion
				if (needs_update) {
					JSONObject procedure = new JSONObject();
					procedure.put("method", "PUT");
					
					com.open();
					int idSong = com.getid(pUsername,Integer.parseInt((String) songJSON.get("id")));
					com.close();
					
					procedure.put("url", _serverPath + "users/" +  pUsername + "/libraries/1/songs/" + idSong + "?type=owner");//Necesito id global de la cancion
					result.add(procedure);
				}
				//No necesita actualizacion
				else {
					JSONObject procedure = new JSONObject();
					procedure.put("method", "NONE");
					procedure.put("url", "NONE");
					result.add(procedure);
				}
			}
		}
		return result;
	}
	
	/*
	 * {
	 * 	"title":title,
	 * 	"artist":artist,
	 * 	"album":album,
	 *  "year":year,
	 * 	"genre":genre,
	 *  "lyrics":lyrics,
	 *  "id":localid
	 *  "blob":blob
	 * }
	 */
	public JSONObject pullSong(String pUsername, String pGlobalID) throws NumberFormatException, SQLException, ClassNotFoundException{
		JSONObject result = new JSONObject();
		try {
			
			comunication com = new comunication();
			System.out.println(pGlobalID);
			com.open();
			List<String> metadata = com.get_songs_lib2(Integer.parseInt(pGlobalID));
			com.close();
			
			result.put("title", metadata.get(0));
			result.put("artist", metadata.get(1));
			result.put("album", metadata.get(2));
			result.put("year", metadata.get(3));
			result.put("genre", metadata.get(4));
			result.put("lyrics", metadata.get(5));
			result.put("id", metadata.get(6));
			
			
			com.open();
			byte[] blob = com.retrieve_song(Integer.parseInt(pGlobalID));
			
			com.close();
			
			result.put("blob", new String(Base64.encode(blob)));
			
			return result;
		} catch (NullPointerException e){
			return null;
		}
		
		
		
	}
	
	
}