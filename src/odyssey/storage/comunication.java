package odyssey.storage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class comunication {

	private static comunication _singleton = new comunication();
	Connection connection;
	Statement statement;
	/*String connectionString = "jdbc:sqlserver://192.168.1.50;" + "databaseName=Odyssey;user=SA;password=Bases2013;";*/
	String connectionString = "jdbc:sqlserver://hyysfso8a0.database.windows.net:1433" + ";" + "database=OdysseyDB" + ";"
			+ "user=Odyssey@hyysfso8a0" + ";" + "password=x100preXD";

	/*
	 * A private Constructor prevents any other class from instantiating.
	 */
	private comunication() {
	}

	/* Static 'instance' method */
	public static comunication getInstance() {
		return _singleton;
	}

	/* Other methods protected by singleton-ness */
	protected static void demoMethod() {
		System.out.println("demoMethod for singleton");
	}

	// cambiar parametros de conexion
	public void open() throws ClassNotFoundException, SQLException {
		
		connection = null;
		statement = null;
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		connection = DriverManager.getConnection(connectionString);
		if(connection == null){
			System.out.println("rkt");
		}
	}

	// Valida si el numero de carateres de un usuario, devuelve tru si lo
	// cumple.
	public boolean validate_user_length(String user) throws SQLException {
		if (user.length() >= 4 && user.length() <= 16) {
			return true;
		} else {
			return false;
		}
	}

	// Valida si un usuario existe, si existe devuelve false.
	public boolean validate_user_nick(String user) throws SQLException {
		boolean flag = true;
		CallableStatement proc_stmt = connection.prepareCall("{ call exist_user(?) }");
		proc_stmt.setString(1, user);
		ResultSet rs = proc_stmt.executeQuery();
		while (rs.next()) {
			String name = rs.getString("Users_Name");
			if (user.equals(name)) {
				flag = false;
			}
		}
		return flag;
	}

	// crea un nuevo usuario
	public void new_user(String user, String password) throws SQLException, ClassNotFoundException {

		if (user.length() >= 4 && user.length() <= 16) {
			CallableStatement proc_stmt = connection.prepareCall("{ call insert_user(?,?) }");
			proc_stmt.setString(1, user);
			proc_stmt.setString(2, password);
			proc_stmt.executeUpdate();
			System.out.println("Processing complete.");
		} else {
			System.out.println("Processing incomplete.");
		}
	}

	// valida un password, retorna true si es valida
	public boolean compare_pass(String user, String hash) throws SQLException {
		CallableStatement proc_stmt = connection.prepareCall("{ call exist_user(?) }");
		proc_stmt.setString(1, user);
		ResultSet rs = proc_stmt.executeQuery();
		String pass = null;
		while (rs.next()) {
			pass = rs.getString("Users_Password");
		}
		if (hash.equals(pass)) {
			return true;
		} else {
			return false;
		}

	}

	// Drop a un usuario
	public void drop_user(String user) throws SQLException {
		CallableStatement proc_stmt = connection.prepareCall("{ call drop_user(?) }");
		proc_stmt.setString(1, user);
		proc_stmt.executeUpdate();
	}

	// Update a un password
	public void update_pass(String user, String newpass) throws SQLException {
		CallableStatement proc_stmt = connection.prepareCall("{ call update_pass(?,?) }");
		proc_stmt.setString(1, user);
		proc_stmt.setString(2, newpass);
		proc_stmt.executeUpdate();
	}

	// Inserta una biblioteca
	public void insert_lib(String user, String Lib_name) throws SQLException {
		CallableStatement proc_stmt = connection.prepareCall("{ call insert_library(?,?) }");
		proc_stmt.setString(1, user);
		proc_stmt.setString(2, Lib_name);
		proc_stmt.executeUpdate();
	}

	public void drop_lib(String user, String Lib) throws SQLException {
		CallableStatement proc_stmt = connection.prepareCall("{ call insert_library(?,?) }");
		proc_stmt.setString(1, user);
		proc_stmt.setString(2, Lib);
		proc_stmt.executeUpdate();
	}

	public List<String> get_Libs(String user) throws SQLException {
		CallableStatement proc_stmt = connection.prepareCall("{ call get_librarys(?) }");
		proc_stmt.setString(1, user);
		ResultSet rs = proc_stmt.executeQuery();
		String name = null;
		List<String> libs = new ArrayList<String>();
		while (rs.next()) {
			name = rs.getString("Lib_Name");
			libs.add(name);
		}
		return libs;
	}
	
	public void insert_song(String User, String Library,String Name,String Artist,String Album,String Year,String Duration,String Lyrics,byte[] Data) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call insert_song(?,?,?,?,?,?,?,?,?) }");
		proc_stmt.setString(1, User);
		proc_stmt.setString(2, Name);
		proc_stmt.setString(3, Artist);
		proc_stmt.setString(4, Album);
		proc_stmt.setString(5, Duration);
		proc_stmt.setString(6, Year);
		proc_stmt.setBytes(7, Data);
		proc_stmt.setString(8, Library);
		proc_stmt.setString(9, Lyrics);
		proc_stmt.executeUpdate();
	}
	
	public byte[] retrieve_song(String User,String Name,String Artist) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call retrieve_song(?,?,?) }");
		proc_stmt.setString(1, User);
		proc_stmt.setString(2, Name);
		proc_stmt.setString(3, Artist);
		ResultSet rs = proc_stmt.executeQuery();
		byte[] res = null;
		while (rs.next()) {
			res = rs.getBytes("Data");
		}
		return res;
	}

	public void close() throws SQLException {
		connection.close();
	}

	public static void main(String args[]) throws SQLException, ClassNotFoundException {
		comunication.getInstance().open();
		
		comunication.getInstance().close();
	}

}