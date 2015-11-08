/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odyssey.services;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.sun.org.apache.xpath.internal.SourceTree;

import odyssey.logic.Processing;
import odyssey.storage.comunication;

import javax.ws.rs.PathParam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 * REST Web Service
 *
 * @author Shagy
 */
@Path("libraries") //General querying still works pah.
public class LibrariesResource {
    
    private String _userID = "";


    @Context
    private UriInfo context;

    /**
     * Creates a new instance of LibrariesResource
     * @param pUserID
     */
    public LibrariesResource(String pUserID) {
        _userID = pUserID;
    }

    /**
     * Queries the database looking for all the existing libraries.
     * 
     * @return JSON Object with existing libraries.
     */
    @GET
    @Produces("application/json")
    public Response queryingLibraries() {
        return Response.status(200).entity("Querying Libraries for user: " + _userID).build();
    }

    /**
     * Creates a library.
     * 
     * @param content JSON Object with API Key and data.
     * @return JSON Object with library information.
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response creatingLibraries(String content) {
    	System.out.println("Here");
    	
        return Response.status(200).entity("Creating Libraries for user: " + _userID).build();
    }

    /**
     * Retrieves a library.
     * 
     * @param libraryID
     * @return JSON Object with information.
     */
    @GET
    @Path("{libraryID}")
    @Produces("application/json")
    public Response retrievingLibraries(@PathParam("libraryID") String libraryID, @QueryParam("type") String type) {

      	try{
      	if(type.compareTo("tree") == 0){
      		//Request for tree information
      		JSONArray container = new JSONArray();
          	JSONObject data = new JSONObject();
          	
          	data.put("id", "root");
        	data.put("text", "Nombre biblioteca");
        	data.put("icon", "fa fa-book fa-lg");
        	
        	JSONObject state = new JSONObject();
        	state.put("opened", true);
        	state.put("selected", false);
        	data.put("state", state);
        	
        	JSONArray childrenArray = new JSONArray();
        	JSONObject children1 = new JSONObject();
        	children1.put("text", "Song Name 1");
        	children1.put("icon", "fa fa-music fa-lg");
        	childrenArray.add(children1);
        	
        	JSONObject children2 = new JSONObject();
        	children2.put("text", "Song Name 2");
        	children2.put("icon", "fa fa-music fa-lg");
        	childrenArray.add(children2);
        	
        	data.put("children", childrenArray);
        	container.add(data);
            return Response.status(200).entity(container.toJSONString()).header("Access-Control-Allow-Origin", "*").build();
      	}
      	else if(type.compareTo("lib") == 0){
      		//Library page request
      		
          	JSONObject data = new JSONObject();
          	
        	data.put("id", "Nombre biblioteca");
        	data.put("owner", "David el tragador");
        	
        	//Canciones de la biblioteca
        	JSONArray songsArray = new JSONArray();
        	JSONObject song1 = new JSONObject();
        	song1.put("title", "Song Name 1");
        	song1.put("artist", "artist");
        	song1.put("album", "album");
        	song1.put("year", "year");
        	song1.put("genre", "rock");
        	songsArray.add(song1);
        	
        	JSONObject song2 = new JSONObject();
        	song2.put("title", "Song Name 2");
        	song2.put("artist", "Artist");
        	song2.put("album", "album");
        	song2.put("year", "year");
        	song2.put("genre", "pop");
        	songsArray.add(song2);
        	
        	JSONObject song3 = new JSONObject();
        	song3.put("title", "Song Name 2");
        	song3.put("artist", "Artist");
        	song3.put("album", "album");
        	song3.put("year", "year");
        	song3.put("genre", "pop");
        	songsArray.add(song3);
        	
        	JSONObject song4 = new JSONObject();
        	song4.put("title", "Song Name 2");
        	song4.put("artist", "Artist");
        	song4.put("album", "album");
        	song4.put("year", "year");
        	song4.put("genre", "pop");
        	songsArray.add(song4);
        	
        	data.put("songs", songsArray);

            return Response.status(200).entity(data.toJSONString()).header("Access-Control-Allow-Origin", "*").build();
        	
      	}
      	else {
      		return Response.status(204).build();
      	}
      	} catch(Exception e){
      		return Response.status(204).build();
      	}
        
    }
    
    /**
     * Updates a library.
     * 
     * @param libraryID
     * @return
     */
    @PUT
    @Path("{libraryID}")
    @Consumes("application/json")
    public Response updatingLibraries(String content, @PathParam("libraryID") String libraryID) {
    	
    	Object obj=JSONValue.parse(content);
    	JSONArray json = (JSONArray)obj;
    	
    	JSONArray response = Processing.processSongs(json); //Es STATIC D:
    	
    	
    	
        return Response.status(301).entity(response.toJSONString()).build();
    }
    
    /**
     * Deletes a library.
     * 
     * @param libraryID
     * @return
     */
    @DELETE
    @Path("{libraryID}")
    @Consumes("application/json")
    public Response deletingLibraries(@PathParam("libraryID") String libraryID) {
    
        return Response.status(200).entity("Deleting Libraries for user: " + _userID).build();
    }
    
    /**
     * Retrieves a SongResource instance.
     * 
     * @param libraryID Desired library to query.
     * @return
     */
    @Path("{libraryID}/songs")
    public SongsResource getSongsResource(@PathParam("libraryID") String libraryID) {
        return new SongsResource(libraryID);
    }

    /**
     * 
     */
    public class SongsResource {
        
        private String _libraryID = "";

        /**
         * Creates a new instance of SongsResource
         * 
         * @param plibraryID 
         */
        private SongsResource(String plibraryID) {
            _libraryID = plibraryID;
        }

        /**
         * Queries songs inside a library.
         * 
         * @return 
         */
        @GET
        @Produces("application/json")
        public Response queryingSongs() {
            return Response.status(200).entity("Querying songs inside, " + _libraryID + " for user: " + _userID).build();
        }
        
        /**
         * Creates a song inside a library.
         * 
         * @param content
         * @return
         * @throws IOException 
         * @throws SQLException 
         * @throws ClassNotFoundException 
         */
        @POST
        @Produces("application/json")
        @Consumes("application/json")
        public Response creatingSongs(String content) throws IOException, SQLException, ClassNotFoundException {
        	System.out.println("Creating song inside, " + _libraryID + " for user: " + _userID);
 
        	Object obj=JSONValue.parse(content);
        	JSONObject json = (JSONObject)obj;

        	//User
        	String name = (String) json.get("name");
        	String blob = (String) json.get("blob");
        	System.out.println(name);
        	
        	String OUTPUT_FILE_NAME = "C:\\Eclipse Servers\\usr\\servers\\OddyseyServer\\test1.mp3";
        	
        	byte[] decoded = Base64.decode(blob);

        	
        	
        	
        	JSONObject response = new JSONObject();
        	response.put("status", "Executed");
        	
            return Response.status(200).entity(response.toJSONString()).build();
        }
        
        /**
         * Retrieves a song inside a library.
         * 
         * @param songID
         * @return
         * @throws Exception 
         */
        @GET
        @Path("{songID}")
        //@Produces("audio/mp3")
        @Produces("application/json")
        public Response retrieveSongs(@PathParam("songID") String songID, @HeaderParam("Range") String range, @QueryParam("data") String data) throws Exception {
        	System.out.println("Sending song: " + songID +" info to client: " + _userID);
        	
        	if(data.compareTo("all") == 0){
        		JSONObject response = new JSONObject();
            	response.put("blob", "LOTS OF SHIT");
            	Thread.sleep(2000);
        		return Response.status(200).entity(response.toJSONString()).header("Access-Control-Allow-Origin", "*").build();
        	}
        	else {
        	JSONObject response = new JSONObject();
        	response.put("title", "Dig up her bones");
        	response.put("artist", "Misftis");
        	response.put("album", "American Psycho");
        	response.put("year", "1997");
        	response.put("owner", "David el tragador");
        	response.put("commentcount", 1);
        	response.put("lyrics", "La la la que pedo guey");
        	
        	JSONArray comments = new JSONArray();
        	
        	JSONObject comment1 = new JSONObject();
        	comment1.put("user", "Santi");
        	comment1.put("value", "Mae esa pieza si es una mierda, jueputa sal!");
        	comment1.put("date","29/10/15");
        	comments.add(comment1);
        	
        	JSONObject comment2 = new JSONObject();
        	comment2.put("user", "LuisDiego123");
        	comment2.put("value", "Santi flamer...");
        	comment2.put("date","31/10/15");
        	comments.add(comment2);
        	
        	
        	response.put("comments", comments);
        	
            return Response.status(200).entity(response.toJSONString()).header("Access-Control-Allow-Origin", "*").build();
        	}
        	/*
        	comunication.getInstance().open();
        	byte[] blob = comunication.getInstance().retrieve_song("Pinga", "Imagina Penes", "Rafalarga");
        	comunication.getInstance().close();
        	
        	FileOutputStream fos = null;
        	fos = new FileOutputStream("C:\\Eclipse Servers\\usr\\servers\\OddyseyServer\\tmp.mp3");
        	fos.write(blob);
        	fos.close();
        	
        	File audio = new File("C:\\Eclipse Servers\\usr\\servers\\OddyseyServer\\tmp.mp3");
        	
        	return buildStream(audio, range);*/
            //return Response.status(200).entity("Song inside, " + _libraryID + " with id: " + songID + " for user: " + _userID).build();
        }
        
        @GET
        @Produces("audio/mp3")
        @Path("stream")
        public Response streamAudio(@HeaderParam("Range") String range) throws Exception {
        	File audio = new File("C:\\Eclipse Servers\\usr\\servers\\OddyseyServer\\tmp.mp3");
            return buildStream(audio, range);
        }
        
        
        private Response buildStream(final File asset, final String range) throws Exception {
            // range not requested : Firefox, Opera, IE do not send range headers
            if (range == null) {
                StreamingOutput streamer = new StreamingOutput() {
                    @Override
                    public void write(final OutputStream output) throws IOException, WebApplicationException {
                    	/*System.out.println(_decoded);
                    	ByteBuffer buf = ByteBuffer.wrap(_decoded);
                    	final FileChannel inputChannel = null;
                    	inputChannel.read(buf);*/
                    	
                        final FileChannel inputChannel = new FileInputStream(asset).getChannel();
                        final WritableByteChannel outputChannel = Channels.newChannel(output);
                        try {
                            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                        } finally {
                            // closing the channels
                            inputChannel.close();
                            outputChannel.close();
                        }
                    }
                };
                return Response.ok(streamer).status(200).header(HttpHeaders.CONTENT_LENGTH, asset.length()).build();
            }

            String[] ranges = range.split("=")[1].split("-");
            final int from = Integer.parseInt(ranges[0]);
            /**
             * Chunk media if the range upper bound is unspecified. Chrome sends "bytes=0-"
             */
            int to = 1000000 + from;
            if (to >= asset.length()) {
                to = (int) (asset.length() - 1);
            }
            if (ranges.length == 2) {
                to = Integer.parseInt(ranges[1]);
            }

            final String responseRange = String.format("bytes %d-%d/%d", from, to, asset.length());
            final RandomAccessFile raf = new RandomAccessFile(asset, "r");
            raf.seek(from);

            final int len = to - from + 1;
            final MediaStreamer streamer = new MediaStreamer(len, raf);
            Response.ResponseBuilder res = Response.ok(streamer).status(206)
                    .header("Accept-Ranges", "bytes")
                    .header("Content-Range", responseRange)
                    .header(HttpHeaders.CONTENT_LENGTH, streamer.getLenth())
                    .header(HttpHeaders.LAST_MODIFIED, new Date(asset.lastModified()));
            return res.build();
        }
        
        /**
         * Updates a song inside a library.
         * 
         * @param songID
         * @return
         */
        @PUT
        @Path("{songID}")
        @Consumes("application/json")
        public Response updatingSongs(@PathParam("songID") String songID) {
            return Response.status(200).entity("Updating song inside, " + _libraryID + " with id: " + songID + " for user: " + _userID).build();
        }
        
        /**
         * Deletes a song inside a library.
         * 
         * @param songID
         * @return
         */
        @DELETE
        @Path("{songID}")
        @Consumes("application/json")
        public Response deletingSongs(@PathParam("songID") String songID) {
            return Response.status(200).entity("Deleting song inside, " + _libraryID + " with id: " + songID + " for user: " + _userID).build();
        }

    }
}
