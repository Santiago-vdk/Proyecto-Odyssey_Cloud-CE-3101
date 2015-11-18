package odyssey.storage;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Arrays;


public class MongoJDBC {
	MongoClient mongoClient;
	static DB db;
	static DBCollection infoUser;
	static DBCollection infoSong;
	
	
	public void MongoJDBC(){
		
	}
	
	public void openConnection (){
		try {
            MongoClientURI uri  = new MongoClientURI("mongodb://odyssey:x100preXD@ds049624.mongolab.com:49624/odyssey-cloud");
			mongoClient = new MongoClient(uri);
			db = mongoClient.getDB(uri.getDatabase());
			System.out.println("Connection Success!");		
		}catch (Exception e){
				System.err.println(e.getClass().getName()+ ": "+e.getMessage());
		}
	}
	
	public void closeConnection(){
		try {
			mongoClient.close();
		}catch( MongoException e){			
			System.out.println("Error al cerrar");
		} 	
	}
	
	/**agregar user por primera vez */
	public boolean addUser(String userName,String generoPref){
		openConnection();
		infoUser = db.getCollection("Users");//toma la coleccion 
		BasicDBObject comp = new BasicDBObject();//crea un documento que se va a usar para hacer la comparacion en la busqueda
		comp.put("User", userName);//indica que se va a buscar la llave user y el valor indicado en el parametro
		DBCursor cursor = infoUser.find(comp); //hace la busqueda a partir del documento de comparacion 
		if (cursor.count()==0){// si el cursor es 0 es por que no existe el user y entonces lo agrega			
			BasicDBList friends = new BasicDBList();//lista donde se insertaran los amigos
			BasicDBObject doc = new BasicDBObject("User", userName)//crea el documento para el user que se inserta
	            .append("Genero Preferido", generoPref)	
				.append("Amigos", friends);
			infoUser.insert(doc);//inserta el user en la coleccion 
			cursor.close();//limpia el cursor con el que se busco
			closeConnection();
			return true;
		}
		else {
			closeConnection();
			return false;
		}
	}

	/**agregar cancion por primera vez*/
	public boolean addSong(String songID){
		openConnection();
		infoSong = db.getCollection("Songs");
		BasicDBObject comp = new BasicDBObject();
		comp.put("Song ID", songID);
		DBCursor cursor = infoSong.find(comp); 
		if (cursor.count()==0){
			BasicDBList comments = new BasicDBList();
			BasicDBObject doc = new BasicDBObject("Song ID",songID)
					.append("Comentarios", comments)
					.append("Likes", 0);
			infoSong.insert(doc);
			cursor.close();
			closeConnection();
			return true;
		}
		else {
			closeConnection();
			return false;
		}
	} 
	
	/**Agregar amigo */
	public boolean addFriend(String userName,String friend){
		openConnection();
		infoUser = db.getCollection("Users");
		BasicDBObject search = new BasicDBObject();//objeto con el que se busca el user
		search.put("User", userName);
		BasicDBObject toUpdate = (BasicDBObject)infoUser.findOne(search);//crea una copia del documento del user al que se le añadiran amigos
		BasicDBList friendsList =(BasicDBList) toUpdate.get("Amigos");//toma el valor de la llave de Amigos del user (la primera vez es una lista vacia, despues es la lista con los amigos ya agregados)
		friendsList.add(friend);//se añade el amig deseado a la lista
		toUpdate.removeField("Amigos");//se elimina del documento el valor de la llave amigos anterior a la actualizacion
		toUpdate.put("Amigos", friendsList);//se asigna la lista actualizada de amigos al valor de la llave de amigos 
		infoUser.update(search, toUpdate);//se actualiza el documento en la coleccion
		closeConnection();
		return true;
	}
	
	/**Agregar un comentario a una cancion*/
	public boolean addComment(String songID,String user, String commentStr){
		openConnection();
		infoSong = db.getCollection("Songs");
		BasicDBObject search = new BasicDBObject();
		search.put("Song ID", songID);
		BasicDBObject toUpdate = (BasicDBObject)infoSong.findOne(search);
		BasicDBList commentsList =(BasicDBList) toUpdate.get("Comentarios");
		BasicDBObject comment =new BasicDBObject();//crea un objeto con el usuario que comenta como llave y el comentario como valor (cada comentario y su usuario va en un documento aparte)
		comment.put(user, commentStr);
		commentsList.add(comment);//lista de comentarios(es una lista de documento)
		toUpdate.removeField("Comentarios");
		toUpdate.put("Comentarios", commentsList);
		infoSong.update(search, toUpdate);
		closeConnection();
		return true;
	}
	
	/**Agregar un like a una cancion*/
	public boolean addLike(String songID){
		openConnection();
		infoSong = db.getCollection("Songs");
		BasicDBObject search = new BasicDBObject();
		search.put("Song ID", songID);
		BasicDBObject toUpdate = (BasicDBObject)infoSong.findOne(search);
		Integer likes =(Integer) toUpdate.get("Likes");
		likes +=1;
		toUpdate.removeField("Likes");
		toUpdate.put("Likes", likes);
		infoSong.update(search, toUpdate);
		closeConnection();
		return true;
	}

	
}