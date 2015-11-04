package odyssey.logic;

public class Session {
	private final String _user;
	private final String _token;

	public Session(String pUser, String pToken){
		_user = pUser;
		_token = pToken;
	}
	
	public String getUser(){
		return _user;
	}

	public String getToken(){
		return _token;
	}
}
