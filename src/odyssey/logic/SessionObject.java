package odyssey.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import odyssey.storage.comunication;

public class SessionObject {
	private final String _user;
	private final String _token;
	private final String _loginTime;
	private String _listeningTo;
	private File _listeningPath;

	public SessionObject(String pUser, String pToken, String pLoginTime, String plisteningTo){
		_user = pUser;
		_token = pToken;
		_loginTime = pLoginTime;
		_listeningTo = plisteningTo;
	}
	
	public void loadSongForStream(String pUserID, String pSongID) throws ClassNotFoundException, SQLException, IOException{
		try{
			System.out.println("Loading from user "+ pUserID + "song with global id, " + pSongID );
			comunication com = new comunication();
			com.open();
			byte[] blob = com.retrieve_song(Integer.parseInt(pSongID));
			com.close();

			FileOutputStream fos = null;
			fos = new FileOutputStream("C:\\Eclipse Servers\\usr\\servers\\OddyseyServer\\tmp" + pUserID + ".mp3");
			fos.write(blob);
			fos.close();

			File audio = new File("C:\\Eclipse Servers\\usr\\servers\\OddyseyServer\\tmp" + pUserID + ".mp3");
			_listeningPath = audio;
			
		}catch(Exception e){
			System.out.println("Error while loading song for user");
			System.out.println(e);
		}
	}
	
	public File getListeningPath(){
		return _listeningPath;
	}
	
	public void setListeningPath(File plisteningPath){
		_listeningPath = plisteningPath;
	}
	
	
	public String getListeningTo(){
		return _listeningTo;
	}
	
	public void setListeningTo(String plisteningTo){
		_listeningTo = plisteningTo;
	}
	
	public String getUser(){
		return _user;
	}

	public String getToken(){
		return _token;
	}
	
	public String getLoginTime(){
		return _loginTime;
	}
}
