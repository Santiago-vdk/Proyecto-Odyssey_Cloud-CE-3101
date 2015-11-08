package odyssey.logic;

public class SessionObject {
	private final String _user;
	private final String _token;
	private final String _loginTime;

	public SessionObject(String pUser, String pToken, String pLoginTime){
		_user = pUser;
		_token = pToken;
		_loginTime = pLoginTime;
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
