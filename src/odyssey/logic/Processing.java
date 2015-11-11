package odyssey.logic;

import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import odyssey.storage.comunication;

public class Processing {
	final String _serverPath = "http://192.168.1.135:9080/OdysseyCloud/api/v1/";

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
	public JSONArray processSongs(String pUsername, JSONArray pSongsArray) throws SQLException {
		JSONArray result = new JSONArray();
		
		System.out.println(pSongsArray);
		for (int i = 0; i < pSongsArray.size(); i++) {
			JSONObject songJSON = (JSONObject) pSongsArray.get(i);

			boolean exists = comunication.getInstance().exist(pUsername, Integer.parseInt((String) songJSON.get("id")));
			// Si no existe debo insertar toda la cancion con BLOB
			if (!exists) {
				JSONObject procedure = new JSONObject();
				procedure.put("method", "POST");
				procedure.put("url", _serverPath + pUsername + "/1/songs/");
				result.add(procedure);
				
			// En caso de si existir
			} 
			else {
				// En caso de si existir reviso si su metadata cambio
				boolean needs_update = comunication.getInstance().have_changes(pUsername,Integer.parseInt((String) songJSON.get("id")),
						songJSON.get("title").toString(), songJSON.get("artist").toString(),
						songJSON.get("album").toString(), songJSON.get("year").toString(),
						songJSON.get("genre").toString(), songJSON.get("lyrics").toString());
				// Necesita actualizacion
				if (needs_update) {
					JSONObject procedure = new JSONObject();
					procedure.put("method", "PUT");
					procedure.put("url", _serverPath + pUsername + "/1/songs/" + "idpieza");//Necesito id global de la cancion
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

}
