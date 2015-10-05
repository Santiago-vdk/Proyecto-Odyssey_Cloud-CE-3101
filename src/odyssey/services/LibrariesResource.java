/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odyssey.services;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

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
         */
        @POST
        @Produces("application/json")
        @Consumes("application/json")
        public Response creatingSongs(String content) {
            return Response.status(200).entity("Creating song inside, " + _libraryID + " for user: " + _userID).build();
        }
        
        /**
         * Retrieves a song inside a library.
         * 
         * @param songID
         * @return
         */
        @GET
        @Path("{songID}")
        @Produces("application/json")
        public Response retrieveSongs(@PathParam("songID") String songID) {
            return Response.status(200).entity("Song inside, " + _libraryID + " with id: " + songID + " for user: " + _userID).build();
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
