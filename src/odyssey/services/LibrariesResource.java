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
import odyssey.storage.MongoJDBC;
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
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
@Path("libraries") // General querying still works pah.
public class LibrariesResource {

	private String _userID = "";

	@Context
	private UriInfo context;

	/**
	 * Creates a new instance of LibrariesResource
	 * 
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
	 * @param content
	 *            JSON Object with API Key and data.
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

		try {
			if (type.compareTo("all") == 0) {
				System.out.println("User, " + _userID + " is downloading his/her library!");

				Processing processing = new Processing();
				JSONArray result = processing.retrieveSongs(_userID);

				// Request for tree information
				/*
				 * JSONArray container = new JSONArray(); JSONObject data = new
				 * JSONObject();
				 * 
				 * data.put("id", "root"); data.put("text", "Nombre biblioteca"
				 * ); data.put("icon", "fa fa-book fa-lg");
				 * 
				 * JSONObject state = new JSONObject(); state.put("opened",
				 * true); state.put("selected", false); data.put("state",
				 * state);
				 * 
				 * JSONArray childrenArray = new JSONArray(); JSONObject
				 * children1 = new JSONObject(); children1.put("text",
				 * "Song Name 1"); children1.put("icon", "fa fa-music fa-lg");
				 * childrenArray.add(children1);
				 * 
				 * JSONObject children2 = new JSONObject();
				 * children2.put("text", "Song Name 2"); children2.put("icon",
				 * "fa fa-music fa-lg"); childrenArray.add(children2);
				 * 
				 * data.put("children", childrenArray); container.add(data);
				 */

				return Response.status(200).entity(result.toJSONString()).header("Access-Control-Allow-Origin", "*")
						.build();
			} else if (type.compareTo("lib") == 0) {
				System.out.println("Reading, " + _userID + "'s library!");
				// Retorna la biblioteca de un usuario

				JSONObject data = new JSONObject();

				comunication com = new comunication();
				com.open();
				List<List<String>> library = com.get_songs_lib(_userID);
				com.close();

				data.put("id", "1");
				data.put("owner", _userID);
				JSONArray songsArray = new JSONArray();
				for (int i = 0; i < library.size(); i++) {
					JSONObject song = new JSONObject();
					song.put("id", library.get(i).get(0));
					song.put("title", library.get(i).get(1));
					song.put("artist", library.get(i).get(2));
					song.put("album", library.get(i).get(3));
					song.put("year", library.get(i).get(4));
					song.put("genre", library.get(i).get(5));
					song.put("lyrics", library.get(i).get(6));
					songsArray.add(song);
				}

				data.put("songs", songsArray);

				System.out.println(data);

				return Response.status(200).entity(data.toJSONString()).header("Access-Control-Allow-Origin", "*")
						.build();

			} else {
				return Response.status(204).build();
			}
		} catch (Exception e) {
			return Response.status(204).build();
		}

	}

	/**
	 * Updates a library.
	 * 
	 * @param libraryID
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws NumberFormatException
	 */
	@PUT
	@Path("{libraryID}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response updatingLibraries(String content, @PathParam("libraryID") String libraryID)
			throws SQLException, NumberFormatException, ClassNotFoundException {
		System.out.println("Creating Plan");

		Object obj = JSONValue.parse(content);
		JSONArray json = (JSONArray) obj;

		Processing process = new Processing();
		JSONArray result = process.processSongs(_userID, json);

		System.out.println(result);

		return Response.status(200).entity(result.toJSONString()).build();
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
	 * @param libraryID
	 *            Desired library to query.
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
			return Response.status(200).entity("Querying songs inside, " + _libraryID + " for user: " + _userID)
					.build();
		}

		/**
		 * Creates a song inside a library, all metadata and blob.
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
			try {
				System.out.println("Creating song inside, " + _libraryID + " for user: " + _userID);

				Object obj = JSONValue.parse(content);
				JSONObject json = (JSONObject) obj;

				// User
				String title = (String) json.get("title");
				String artist = (String) json.get("artist");
				String album = (String) json.get("album");
				String year = (String) json.get("year");
				String genre = (String) json.get("genre");
				String lyrics = (String) json.get("lyrics");
				String lib = (String) json.get("lib");
				String id = (String) json.get("id"); // Local id
				String blob = (String) json.get("blob");

				byte[] blobData = Base64.decode(blob);

				// String OUTPUT_FILE_NAME = "C:\\Eclipse
				// Servers\\usr\\servers\\OddyseyServer\\test1.mp3";
				// byte[] decoded = Base64.decode(blob);

				comunication com = new comunication();
				com.open();
				com.insert_song(_userID, "1", title, artist, album, year, genre, lyrics, blobData,
						Integer.parseInt(id));
				com.close();

				com.open();
				int globalID = com.getid(_userID, Integer.parseInt(id));
				com.close();

				MongoJDBC mongo = new MongoJDBC();
				mongo.addSong(String.valueOf(globalID));

				JSONObject response = new JSONObject();
				response.put("status", "Executed");

				return Response.status(200).entity(response.toJSONString()).build();
			} catch (NullPointerException e) {
				System.out.println("Bad arguments!");
				return Response.status(400).build();
			}
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
		// @Produces("audio/mp3")
		@Produces("application/json")
		public Response retrieveSongs(@PathParam("songID") String songID, @QueryParam("data") String data)
				throws Exception {

			if (data.compareTo("all") == 0) {
				System.out.println("Pulling all the song");
				Processing processing = new Processing();
				JSONObject response = processing.pullSong(_userID, songID);
				if (response == null) {
					return Response.status(404).header("Access-Control-Allow-Origin", "*").build();
				} else {
					return Response.status(200).entity(response.toJSONString())
							.header("Access-Control-Allow-Origin", "*").build();
				}
			} else if (data.compareTo("social") == 0) {
				System.out.println("social");
				JSONObject response = new JSONObject();
				MongoJDBC mongo = new MongoJDBC();

				comunication com = new comunication();
				com.open();
				List<String> metadata = com.get_songs_lib2(Integer.parseInt(songID));
				com.close();

				response.put("globalid", songID);
				response.put("title", metadata.get(0));
				response.put("artist", metadata.get(1));
				response.put("album", metadata.get(2));
				response.put("year", metadata.get(3));
				response.put("genre", metadata.get(4));
				response.put("lyrics", metadata.get(5));

				ArrayList<JSONObject> commentArray = mongo.getComments(songID);
				System.out.println(commentArray.size());
				JSONArray comments = new JSONArray();
				for (int i = 0; i < commentArray.size(); i++) {
					JSONObject comment = new JSONObject();
					comment.put("user", commentArray.get(i).get("user"));
					comment.put("value", commentArray.get(i).get("comment"));
					comments.add(comment);
				}

				response.put("comments", comments);
				response.put("commentcount", comments.size());

				return Response.status(200).entity(response.toJSONString()).header("Access-Control-Allow-Origin", "*")
						.build();
			} else if (data.compareTo("version") == 0) {
				System.out.println("Reading version, " + _userID + "'s library!");

				comunication com = new comunication();
				com.open();
				List<List<String>> versions = com.get_songs_versions(Integer.parseInt(songID));
				com.close();

				JSONArray result = new JSONArray();
				for (int i = 0; i < versions.size(); i++) {
					JSONObject song = new JSONObject();
					song.put("title", versions.get(i).get(0));
					song.put("artist", versions.get(i).get(1));
					song.put("album", versions.get(i).get(2));
					song.put("year", versions.get(i).get(3));
					song.put("genre", versions.get(i).get(4));
					song.put("lyrics", versions.get(i).get(5));
					song.put("version", versions.get(i).get(6));
					result.add(song);
				}

				return Response.status(200).entity(result.toJSONString()).header("Access-Control-Allow-Origin", "*")
						.build();

			} else {
				return Response.status(404).header("Access-Control-Allow-Origin", "*").build();
			}

			/*
			 * comunication.getInstance().open(); byte[] blob =
			 * comunication.getInstance().retrieve_song("Pinga", "Imagina Penes"
			 * , "Rafalarga"); comunication.getInstance().close();
			 * 
			 * FileOutputStream fos = null; fos = new FileOutputStream(
			 * "C:\\Eclipse Servers\\usr\\servers\\OddyseyServer\\tmp.mp3");
			 * fos.write(blob); fos.close();
			 * 
			 * File audio = new File(
			 * "C:\\Eclipse Servers\\usr\\servers\\OddyseyServer\\tmp.mp3");
			 * 
			 * return buildStream(audio, range);
			 */
			// return Response.status(200).entity("Song inside, " + _libraryID +
			// " with id: " + songID + " for user: " + _userID).build();
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
		public Response updatingSongs(String content, @PathParam("songID") String songID,
				@QueryParam("type") String type) {
			try {
				System.out.println("Updating song, " + songID + " inside, " + _userID + "'s library!");

				if (type.compareTo("owner") == 0) {
					Object obj = JSONValue.parse(content);
					JSONObject json = (JSONObject) obj;

					// User
					String title = (String) json.get("title");
					String artist = (String) json.get("artist");
					String album = (String) json.get("album");
					String year = (String) json.get("year");
					String genre = (String) json.get("genre");
					String lyrics = (String) json.get("lyrics");
					String lib = (String) json.get("lib");
					String id = (String) json.get("id"); // Local id

					comunication com = new comunication();
					com.open();
					com.update_song_from_local(Integer.parseInt((String) id), _userID, title, artist, album, year,
							genre, lyrics);
					com.close();

					return Response.status(200).build();
				} else if (type.compareTo("friend") == 0) {

					Object obj = JSONValue.parse(content);
					JSONObject json = (JSONObject) obj;

					// User
					String title = (String) json.get("title");
					String artist = (String) json.get("artist");
					String album = (String) json.get("album");
					String year = (String) json.get("year");
					String genre = (String) json.get("genre");
					String lyrics = (String) json.get("lyrics");

					comunication com = new comunication();
					com.open();
					com.update_song(Integer.parseInt(songID), title, artist, album, year, genre, lyrics);
					com.close();

					return Response.status(200).build();

				} else if (type.compareTo("version") == 0) {

					Object obj = JSONValue.parse(content);
					JSONObject json = (JSONObject) obj;
					String version = (String) json.get("version");

					comunication com = new comunication();
					com.open();
					com.set_version(Integer.parseInt(songID), Integer.parseInt(version));
					com.close();

					return Response.status(200).build();
				} else if (type.compareTo("comment") == 0) {

					Object obj = JSONValue.parse(content);
					JSONObject json = (JSONObject) obj;
					String comment = (String) json.get("comment");
					String fromUser = (String) json.get("fromUser");
					
					MongoJDBC mongo = new MongoJDBC();
					mongo.addComment(songID, fromUser, comment);
					

					return Response.status(200).build();
				} 
				else if (type.compareTo("like") == 0) {
					MongoJDBC mongo = new MongoJDBC();
					mongo.addLike(songID);
					return Response.status(200).build();
				}
				else if (type.compareTo("dislike") == 0) {
					MongoJDBC mongo = new MongoJDBC();
					mongo.disLike(songID);
					return Response.status(200).build();
				} else {
					
					return Response.status(404).build();
				}

			} catch (Exception e) {
				System.out.println("Error while updating song, " + songID);
				System.out.println(e);
				return Response.status(400).build();
			}
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
			try {
				System.out.println("Deleting song, " + songID + " inside, " + _userID + "'s library!");
				comunication com = new comunication();
				com.open();
				com.drop_song(Integer.parseInt(songID));
				com.close();

				MongoJDBC mongo = new MongoJDBC();
				mongo.deleteSong(songID);

				return Response.status(200).build();
			} catch (Exception e) {
				System.out.println("Error while deleting song, " + songID);
				System.out.println(e);
				return Response.status(400).build();
			}
		}

		@GET
		@Produces("audio/mp3")
		@Path("{songID}/stream")
		public Response streamAudio(@PathParam("songID") String songID, @HeaderParam("Range") String range)
				throws Exception {

			System.out.println("Streaming song " + songID + " inside " + _userID + "'s library!");
			
			
			
			comunication com = new comunication();
			com.open();
			byte[] blob = com.retrieve_song(Integer.parseInt(songID));
			com.close();

			FileOutputStream fos = null;
			fos = new FileOutputStream("C:\\Eclipse Servers\\usr\\servers\\OddyseyServer\\tmp" + _userID + ".mp3");
			fos.write(blob);
			fos.close();

			File audio = new File("C:\\Eclipse Servers\\usr\\servers\\OddyseyServer\\tmp" + _userID + ".mp3");

			return buildStream(audio, range);
		}

		private Response buildStream(final File asset, final String range) throws Exception {
			// range not requested : Firefox, Opera, IE do not send range
			// headers
			if (range == null) {
				StreamingOutput streamer = new StreamingOutput() {
					@Override
					public void write(final OutputStream output) throws IOException, WebApplicationException {
						/*
						 * System.out.println(_decoded); ByteBuffer buf =
						 * ByteBuffer.wrap(_decoded); final FileChannel
						 * inputChannel = null; inputChannel.read(buf);
						 */

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
			 * Chunk media if the range upper bound is unspecified. Chrome sends
			 * "bytes=0-"
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
			Response.ResponseBuilder res = Response.ok(streamer).status(206).header("Accept-Ranges", "bytes")
					.header("Content-Range", responseRange).header(HttpHeaders.CONTENT_LENGTH, streamer.getLenth())
					.header(HttpHeaders.LAST_MODIFIED, new Date(asset.lastModified()));
			return res.build();
		}
	}
}
