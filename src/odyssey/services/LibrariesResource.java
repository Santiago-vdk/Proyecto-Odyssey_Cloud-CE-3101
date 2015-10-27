/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odyssey.services;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.sun.org.apache.xpath.internal.SourceTree;

import javax.ws.rs.PathParam;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Paths;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
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
    public Response retrievingLibraries(@PathParam("libraryID") String libraryID) {
        return Response.status(200).entity("Retrieving Libraries for user: " + _userID).build();
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
    public Response updatingLibraries(@PathParam("libraryID") String libraryID) {
        return Response.status(301).entity("Updating Libraries for user: " + _userID).build();
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
         */
        @POST
        @Produces("application/json")
        @Consumes("application/json")
        public Response creatingSongs(String content) throws IOException {
        	System.out.println("Creating song inside, " + _libraryID + " for user: " + _userID);
 
        	Object obj=JSONValue.parse(content);
        	JSONObject json = (JSONObject)obj;

        	//User
        	String name = (String) json.get("name");
        	byte[] blob = (byte[]) json.get("blob");
        	System.out.println(name);
        	
        	String OUTPUT_FILE_NAME = "C:\\Eclipse Servers\\usr\\servers\\OddyseyServer\\test1.mp3";
        	
        	DataOutputStream os = new DataOutputStream(new FileOutputStream("C:\\Eclipse Servers\\usr\\servers\\OddyseyServer\\test1.mp3"));
        	 os.write(blob);
        	 os.close();
        	
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
        @Produces("audio/mp3")
        public Response retrieveSongs(@PathParam("songID") String songID, @HeaderParam("Range") String range) throws Exception {
        	System.out.println("Here");
        	
        	File audio = new File("C:\\Eclipse Servers\\usr\\servers\\OddyseyServer\\test.mp3");
        	System.out.println(audio);
        	return buildStream(audio, range);
            //return Response.status(200).entity("Song inside, " + _libraryID + " with id: " + songID + " for user: " + _userID).build();
        }
        
        
        private Response buildStream(final File asset, final String range) throws Exception {
            // range not requested : Firefox, Opera, IE do not send range headers
            if (range == null) {
                StreamingOutput streamer = new StreamingOutput() {
                    @Override
                    public void write(final OutputStream output) throws IOException, WebApplicationException {

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
