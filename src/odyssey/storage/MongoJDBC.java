package odyssey.storage;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.util.JSON;

import java.util.ArrayList;
import java.util.Arrays;

import org.bson.BasicBSONObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class MongoJDBC {
	MongoClient mongoClient;
	static DB db;
	static DBCollection infoUser;
	static DBCollection infoSong;

	/*
	 * public void MongoJDBC(){
	 * 
	 * }
	 */

	public void openConnection() {
		try {
			MongoClientURI uri = new MongoClientURI(
					"mongodb://odyssey:x100preXD@ds049624.mongolab.com:49624/odyssey-cloud");
			mongoClient = new MongoClient(uri);
			db = mongoClient.getDB(uri.getDatabase());
			System.out.println("Connection Success!");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void closeConnection() {
		try {
			mongoClient.close();
		} catch (MongoException e) {
			System.out.println("Error al cerrar");
		}
	}

	/** agregar user por primera vez */
	public boolean addUser(String userName, String generoPref) {
		openConnection();
		infoUser = db.getCollection("Users");// toma la coleccion
		BasicDBObject comp = new BasicDBObject();// crea un documento que se va
													// a usar para hacer la
													// comparacion en la
													// busqueda
		comp.put("User", userName);// indica que se va a buscar la llave user y
									// el valor indicado en el parametro
		DBCursor cursor = infoUser.find(comp); // hace la busqueda a partir del
												// documento de comparacion
		if (cursor.count() == 0) {// si el cursor es 0 es por que no existe el
									// user y entonces lo agrega
			BasicDBList friends = new BasicDBList();// lista donde se insertaran
													// los amigos
			BasicDBObject doc = new BasicDBObject("User", userName)// crea el
																	// documento
																	// para el
																	// user que
																	// se
																	// inserta
					.append("Genero Preferido", generoPref).append("Amigos", friends).append("Cantidad Comentarios", 0);
			infoUser.insert(doc);// inserta el user en la coleccion
			cursor.close();// limpia el cursor con el que se busco
			closeConnection();
			return true;
		} else {
			closeConnection();
			return false;
		}
	}

	public void deleteUser(String userName) {
		openConnection();
		infoUser = db.getCollection("Users");// toma la coleccion
		BasicDBObject comp = new BasicDBObject();// crea un documento que se va
													// a usar para hacer la
													// comparacion en la
													// busqueda
		comp.put("User", userName);// indica que se va a buscar la llave user y
									// el valor indicado en el parametro
		infoUser.remove(comp);
		closeConnection();
	}

	/** agregar cancion por primera vez */
	public boolean addSong(String songID) {
		openConnection();
		infoSong = db.getCollection("Songs");
		BasicDBObject comp = new BasicDBObject();
		comp.put("Song ID", songID);
		DBCursor cursor = infoSong.find(comp);
		if (cursor.count() == 0) {
			BasicDBList comments = new BasicDBList();
			BasicDBList likeUsers = new BasicDBList();
			BasicDBList disLikeUsers = new BasicDBList();
			BasicDBObject doc = new BasicDBObject("Song ID", songID).append("Comentarios", comments).append("Likes", 0)
					.append("likeUsers", likeUsers).append("disLikeUsers", disLikeUsers);
			infoSong.insert(doc);
			cursor.close();
			closeConnection();
			return true;
		} else {
			closeConnection();
			return false;
		}
	}

	public void deleteSong(String songID) {
		openConnection();
		infoSong = db.getCollection("Songs");
		BasicDBObject comp = new BasicDBObject();
		comp.put("Song ID", songID);
		infoSong.remove(comp);
		closeConnection();
	}

	/** Agregar amigo */
	public boolean addFriend(String userName, String friend) {
		openConnection();
		infoUser = db.getCollection("Users");
		BasicDBObject search1 = new BasicDBObject();// objeto con el que se
													// busca el user
		BasicDBObject search2 = new BasicDBObject();
		search1.put("User", userName);
		search2.put("User", friend);
		BasicDBObject toUpdate1 = (BasicDBObject) infoUser.findOne(search1);// crea
																			// una
																			// copia
																			// del
																			// documento
																			// del
																			// user
																			// al
																			// que
																			// se
																			// le
																			// añadiran
																			// amigos
		BasicDBObject toUpdate2 = (BasicDBObject) infoUser.findOne(search2);
		BasicDBList friendsList1 = (BasicDBList) toUpdate1.get("Amigos");// toma
																			// el
																			// valor
																			// de
																			// la
																			// llave
																			// de
																			// Amigos
																			// del
																			// user
																			// (la
																			// primera
																			// vez
																			// es
																			// una
																			// lista
																			// vacia,
																			// despues
																			// es
																			// la
																			// lista
																			// con
																			// los
																			// amigos
																			// ya
																			// agregados)
		BasicDBList friendsList2 = (BasicDBList) toUpdate2.get("Amigos");
		if (!friendsList1.contains((Object) friend)) {
			friendsList1.add(friend);// se añade el amigo deseado a la lista
			friendsList2.add(userName);
			toUpdate1.removeField("Amigos");// se elimina del documento el valor
											// de la llave amigos anterior a la
											// actualizacion
			toUpdate2.removeField("Amigos");
			toUpdate1.put("Amigos", friendsList1);// se asigna la lista
													// actualizada de amigos al
													// valor de la llave de
													// amigos
			toUpdate2.put("Amigos", friendsList2);
			infoUser.update(search1, toUpdate1);// se actualiza el documento en
												// la coleccion
			infoUser.update(search2, toUpdate2);
			closeConnection();
			return true;
		} else {
			closeConnection();
			return false;
		}
	}

	public void deleteFriend(String user, String friend) {
		openConnection();
		infoUser = db.getCollection("Users");
		BasicDBObject search1 = new BasicDBObject();
		BasicDBObject search2 = new BasicDBObject();
		search1.put("User", user);
		search2.put("User", friend);
		BasicDBObject toUpdate1 = (BasicDBObject) infoUser.findOne(search1);
		BasicDBObject toUpdate2 = (BasicDBObject) infoUser.findOne(search2);
		BasicDBList friendsList1 = (BasicDBList) toUpdate1.get("Amigos");
		BasicDBList friendsList2 = (BasicDBList) toUpdate2.get("Amigos");
		if (friendsList1.contains((Object) friend)) {
			friendsList1.remove(friend);
			friendsList2.remove(user);
			toUpdate1.removeField("Amigos");
			toUpdate2.removeField("Amigos");
			toUpdate1.put("Amigos", friendsList1);
			toUpdate2.put("Amigos", friendsList2);
			infoUser.update(search1, toUpdate1);
			infoUser.update(search2, toUpdate2);
		}
		closeConnection();
	}

	/** Agregar un comentario a una cancion */
	public boolean addComment(String songID, String user, String commentStr) {
		openConnection();
		infoSong = db.getCollection("Songs");
		infoUser = db.getCollection("Users");
		BasicDBObject search = new BasicDBObject();
		BasicDBObject search2 = new BasicDBObject();
		search.put("Song ID", songID);
		search2.put("User", user);
		BasicDBObject toUpdate = (BasicDBObject) infoSong.findOne(search);
		BasicDBObject toUpdate2 = (BasicDBObject) infoUser.findOne(search2);
		BasicDBList commentsList = (BasicDBList) toUpdate.get("Comentarios");
		BasicDBObject comment = new BasicDBObject();// crea un objeto con una
													// llave usuario con el
													// valor del nombre de
													// usuario y otra llave
													// comentario con el valor
													// comentario (cada
													// comentario y su usuario
													// va en un documento
													// aparte)
		comment.put("user", user);
		comment.put("comment", commentStr);
		commentsList.add(comment);// lista de comentarios(es una lista de
									// documentos)
		toUpdate.removeField("Comentarios");
		toUpdate.put("Comentarios", commentsList);
		infoSong.update(search, toUpdate);

		Integer cantCmnt = (Integer) toUpdate2.get("Cantidad Comentarios");
		cantCmnt += 1;
		toUpdate2.removeField("Cantidad Comentarios");
		toUpdate2.put("Cantidad Comentarios", cantCmnt);
		infoUser.update(search2, toUpdate2);
		closeConnection();
		return true;
	}

	/** Agregar un like a una cancion */
	public boolean addLike(String songID, String user) {
		openConnection();
		infoSong = db.getCollection("Songs");
		BasicDBObject search = new BasicDBObject();
		search.put("Song ID", songID);
		BasicDBObject toUpdate = (BasicDBObject) infoSong.findOne(search);
		Integer likes = (Integer) toUpdate.get("Likes");
		BasicDBList likeUsers = (BasicDBList) toUpdate.get("likeUsers");
		if (!likeUsers.contains((Object) user)) {
			likeUsers.add((Object) user);
			likes += 1;
			toUpdate.removeField("likeUsers");
			toUpdate.removeField("Likes");
			toUpdate.put("likeUsers", likeUsers);
			toUpdate.put("Likes", likes);
			infoSong.update(search, toUpdate);
			closeConnection();
			return true;
		} else {
			return false;
		}
	}

	public boolean disLike(String songID, String user) {
		openConnection();
		infoSong = db.getCollection("Songs");
		BasicDBObject search = new BasicDBObject();
		search.put("Song ID", songID);
		BasicDBObject toUpdate = (BasicDBObject) infoSong.findOne(search);
		Integer likes = (Integer) toUpdate.get("Likes");
		BasicDBList disLikeUsers = (BasicDBList) toUpdate.get("disLikeUsers");
		if (!disLikeUsers.contains((Object) user)) {
			disLikeUsers.add((Object) user);
			likes -= 1;
			toUpdate.removeField("disLikeUsers");
			toUpdate.removeField("Likes");
			toUpdate.put("disLikeUsers", disLikeUsers);
			toUpdate.put("Likes", likes);
			infoSong.update(search, toUpdate);
			closeConnection();
			return true;
		} else {
			return false;
		}
	}

	public int getLikes(String songID) {
		openConnection();
		infoSong = db.getCollection("Songs");
		BasicDBObject search = new BasicDBObject();
		search.put("Song ID", songID);
		BasicDBObject objective = (BasicDBObject) infoSong.findOne(search);
		Integer likes = (Integer) objective.get("Likes");
		closeConnection();
		return likes;
	}

	public ArrayList<JSONObject> getComments(String songID) {

		openConnection();
		infoSong = db.getCollection("Songs");
		BasicDBObject search = new BasicDBObject();
		search.put("Song ID", songID);
		BasicDBObject objective = (BasicDBObject) infoSong.findOne(search);
		BasicDBList commentsList = (BasicDBList) objective.get("Comentarios");
		ArrayList<JSONObject> res = new ArrayList<JSONObject>();
		JSONObject j = new JSONObject();

		for (Object o : commentsList) {
			JSONParser p = new JSONParser();
			try {
				j = (JSONObject) p.parse(o.toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			res.add(j);
		}
		closeConnection();
		return res;

	}

	public ArrayList<String> getFriends(String UserName) {
		openConnection();
		infoUser = db.getCollection("Users");
		BasicDBObject search = new BasicDBObject();
		search.put("User", UserName);
		BasicDBObject objective = (BasicDBObject) infoUser.findOne(search);

		if (objective != null) {
			BasicDBList friendsList = (BasicDBList) objective.get("Amigos");
			ArrayList<String> res = new ArrayList<String>();
			if (friendsList.isEmpty()) {
				closeConnection();
				return res;
			}
			for (Object o : friendsList) {
				String temp = o.toString();
				res.add(temp);
			}
			closeConnection();
			return res;

		}

		else {
			closeConnection();
			return null;
		}
	}

	public ArrayList<JSONObject> findOnComments(String compare) {
		ArrayList<JSONObject> res = new ArrayList<JSONObject>();
		BasicDBObject temp = new BasicDBObject();
		openConnection();
		infoSong = db.getCollection("Songs");
		DBCursor cursor = infoSong.find();
		JSONObject j = new JSONObject();
		while (cursor.hasNext()) {
			DBObject actual = cursor.next();
			BasicDBList cmnts = (BasicDBList) actual.get("Comentarios");
			for (Object o : cmnts) {
				if (o.toString().contains(compare)) {
					BasicDBObject ob = (BasicDBObject) o;
					temp.put("SongID", actual.get("Song ID"));
					temp.put("User", ob.get("user"));
					temp.put("Comment", ob.get("comment"));

					JSONParser p = new JSONParser();
					try {
						j = (JSONObject) p.parse(temp.toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					res.add(j);
				}
			}
		}
		closeConnection();
		return res;
	}

	public void setFavoriteGen(String user, String gen) {
		openConnection();
		infoUser = db.getCollection("Users");
		BasicDBObject search = new BasicDBObject();
		search.put("User", user);
		BasicDBObject toUpdate = (BasicDBObject) infoUser.findOne(search);
		toUpdate.removeField("Genero Preferido");
		toUpdate.put("Genero Preferido", gen);
		infoUser.update(search, toUpdate);
		closeConnection();
	}

	public String getFavoriteGen(String user) {
		openConnection();
		infoUser = db.getCollection("Users");
		BasicDBObject search = new BasicDBObject();
		search.put("User", user);
		BasicDBObject objective = (BasicDBObject) infoUser.findOne(search);
		String res = (String) objective.get("Genero Preferido");
		closeConnection();
		return res;

	}

	public ArrayList<String> suggestFriends(String user) {
		ArrayList<String> res = new ArrayList<String>();
		openConnection();
		infoUser = db.getCollection("Users");
		BasicDBObject search = new BasicDBObject();
		search.put("User", user);
		BasicDBObject objective = (BasicDBObject) infoUser.findOne(search);
		String genCompare = (String) objective.get("Genero Preferido");
		DBCursor cursor = infoUser.find();
		while (cursor.hasNext()) {
			DBObject actual = cursor.next();
			Object o = actual.get("Genero Preferido");
			String ob = (String) actual.get("User");
			BasicDBList amigos = (BasicDBList) objective.get("Amigos");
			/*
			 * System.out.println(genCompare.compareTo((String)o)==0);
			 * System.out.println(ob.compareTo((String)objective.get("User"))!=0
			 * ); System.out.println(!amigos.contains(actual.get("User")));
			 */if (genCompare.compareTo((String) o) == 0 && ob.compareTo((String) objective.get("User")) != 0
					&& !amigos.contains(actual.get("User"))) {
				res.add(ob);
			}
		}
		closeConnection();
		return res;
	}

	/****************************************************************/
	public ArrayList<String> getTopUsers() {
		ArrayList<String> res = new ArrayList<String>();
		ArrayList<String[]> sortList = new ArrayList<String[]>();
		openConnection();
		infoUser = db.getCollection("Users");
		DBCursor cursor = infoUser.find();

		while (cursor.hasNext()) {
			DBObject actual = cursor.next();
			String[] temp = { "", "0" };
			String user = (String) actual.get("User");
			BasicDBList listAmig = (BasicDBList) actual.get("Amigos");
			Integer cantAmig = listAmig.size();
			Integer rank = cantAmig + (Integer) actual.get("Cantidad Comentarios");
			temp[0] = user;
			temp[1] = Integer.toString(rank);
			sortList.add(temp);
		}
		closeConnection();
		return bubbleSortAsc(sortList, res);
	}

	private ArrayList<String> bubbleSortAsc(ArrayList<String[]> source, ArrayList<String> result) {
		if (source.isEmpty()) {
			return result;
		} else {
			int high = Integer.parseInt(source.get(0)[1]);
			String maxVal = source.get(0)[0];
			int maxIndx = 0;
			for (int i = 0; i < source.size(); i++) {
				int temp = Integer.parseInt(source.get(i)[1]);
				if (high < temp) {
					high = temp;
					maxVal = source.get(i)[0];
					maxIndx = i;
				}
			}
			result.add(maxVal);
			source.remove(maxIndx);

			return bubbleSortAsc(source, result);
		}
	}

	private ArrayList<String> bubbleSortDec(ArrayList<String[]> source, ArrayList<String> result) {
		if (source.isEmpty()) {
			return result;
		} else {
			int min = Integer.parseInt(source.get(0)[1]);
			String minVal = source.get(0)[0];
			int minIndx = 0;
			for (int i = 0; i < source.size(); i++) {
				int temp = Integer.parseInt(source.get(i)[1]);
				if (min > temp) {
					min = temp;
					minVal = source.get(i)[0];
					minIndx = i;
				}
			}
			result.add(minVal);
			source.remove(minIndx);

			return bubbleSortDec(source, result);
		}
	}

	public ArrayList<String> getHateUsers() {
		ArrayList<String> res = new ArrayList<String>();
		ArrayList<String[]> sortList = new ArrayList<String[]>();
		openConnection();
		infoUser = db.getCollection("Users");
		DBCursor cursor = infoUser.find();

		while (cursor.hasNext()) {
			DBObject actual = cursor.next();
			String[] temp = { "", "0" };
			String user = (String) actual.get("User");
			BasicDBList listAmig = (BasicDBList) actual.get("Amigos");
			Integer cantAmig = listAmig.size();
			Integer rank = cantAmig + (Integer) actual.get("Cantidad Comentarios");
			temp[0] = user;
			temp[1] = Integer.toString(rank);
			sortList.add(temp);
		}
		closeConnection();
		return bubbleSortDec(sortList, res);
	}

	public ArrayList<String> getHateSongs() {
		ArrayList<String> res = new ArrayList<String>();
		ArrayList<String[]> sortList = new ArrayList<String[]>();
		openConnection();
		infoSong = db.getCollection("Songs");
		DBCursor cursor = infoSong.find();

		while (cursor.hasNext()) {
			DBObject actual = cursor.next();
			String[] temp = { "", "0" };
			String song = (String) actual.get("Song ID");
			BasicDBList listAmig = (BasicDBList) actual.get("Amigos");
			Integer rank = this.getLikes(song);
			temp[0] = song;
			temp[1] = Integer.toString(rank);
			sortList.add(temp);
		}
		closeConnection();
		return bubbleSortDec(sortList, res);
	}

	public ArrayList<String> getlovedSongs() {
		ArrayList<String> res = new ArrayList<String>();
		ArrayList<String[]> sortList = new ArrayList<String[]>();
		openConnection();
		infoSong = db.getCollection("Songs");
		DBCursor cursor = infoSong.find();

		while (cursor.hasNext()) {
			DBObject actual = cursor.next();
			String[] temp = { "", "0" };
			String song = (String) actual.get("Song ID");
			BasicDBList listAmig = (BasicDBList) actual.get("Amigos");
			Integer rank = this.getLikes(song);
			temp[0] = song;
			temp[1] = Integer.toString(rank);
			sortList.add(temp);
		}
		closeConnection();
		return bubbleSortAsc(sortList, res);
	}

}