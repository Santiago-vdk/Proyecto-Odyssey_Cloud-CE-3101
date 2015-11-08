package odyssey.storage;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class test {
	
    public static BasicDBObject[] createSeedData(){
        
        BasicDBObject seventies = new BasicDBObject();
        seventies.put("decade", "1970s");
        seventies.put("artist", "Debby Boone");
        seventies.put("song", "You Light Up My Life");
        seventies.put("weeksAtOne", 10);
        
        BasicDBObject eighties = new BasicDBObject();
        BasicDBList list = new BasicDBList();
        BasicDBObject comentario = new BasicDBObject();
        comentario.put("Santiago", "esa pieza es una mierda");
        
        list.add(comentario);
        
        eighties.put("decade", "1980s");
        eighties.put("artist", "Olivia Newton-John");
        eighties.put("song", "Physical");
        eighties.put("comentarios", list);
        
        BasicDBObject nineties = new BasicDBObject();
        nineties.put("decade", "1990s");
        nineties.put("artist", "Mariah Carey");
        nineties.put("song", "One Sweet Day");
        nineties.put("weeksAtOne", 16);
        
        final BasicDBObject[] seedData = {seventies, eighties, nineties};
        
        return seedData;
    }
    

	public static void main(String[] args) {
		final BasicDBObject[] seedData = createSeedData();
		
		
		MongoClientURI uri  = new MongoClientURI("mongodb://odyssey:x100preXD@ds049624.mongolab.com:49624/odyssey-cloud"); 
		MongoClient client = new MongoClient(uri);
		DB db = client.getDB(uri.getDatabase());
		DBCollection songs = db.getCollection("Odyssey");
		songs.insert(seedData);
		client.close();
	}

}
