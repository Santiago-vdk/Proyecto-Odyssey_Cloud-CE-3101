package odyssey.logic;

public class SessionObject {
	private final String _user;
	private final String _token;
	private final String _loginTime;
	private final String _listeningTo;

	public SessionObject(String pUser, String pToken, String pLoginTime, String plisteningTo){
		_user = pUser;
		_token = pToken;
		_loginTime = pLoginTime;
		_listeningTo = plisteningTo;
	}
	
	public String getListeningTo(){
		return _listeningTo;
	}
	
	public void setListeningTo(String plisteningTo){
		
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
