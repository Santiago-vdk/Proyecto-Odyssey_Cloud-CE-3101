package odyssey.storage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class comunication {

	Connection connection;
	Statement statement;
	String connectionString = "jdbc:sqlserver://qi3tj3cjc7.database.windows.net:1433" + ";" + "database=OdysseyDB" + ";"
			+ "user=Rafael@qi3tj3cjc7" + ";" + "password=x100preXD";
	
	//String connectionString2 = "jdbc:sqlserver://192.168.1.129;" + "databaseName=OdysseyDB;user=SA;password=Badilla94;";

	/*
	 * A private Constructor prevents any other class from instantiating.
	 */
	public comunication() {
	}

	/* Static 'instance' method */
	

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
			System.out.println("Connection lost");
		}
	}
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Validacion de user

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
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Operaciones sobre canciones
	
	
	//despliega todas las canciones de la libreria de un usuario
	public List<List<String>> get_songs_lib(String User) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call get_songs_lib2(?) }");
		proc_stmt.setString(1, User);
		
		ResultSet rs = proc_stmt.executeQuery();
		List<List<String>> tmp = new ArrayList<List<String>>();
		while (rs.next()) {
			List<String> tmp2 = new ArrayList<String>();
			tmp2.add(rs.getString("Song_ID"));
			tmp2.add(rs.getString("Song_Name"));
			tmp2.add(rs.getString("Song_Artist"));
			tmp2.add(rs.getString("Song_Album"));
			tmp2.add(rs.getString("Song_Year"));
			tmp2.add(rs.getString("Song_Gen"));
			tmp2.add(rs.getString("Lyrics"));
			tmp.add(tmp2);
		}
		return tmp;
	}
	
	//Inserta una nueva cancion
	public void insert_song(String User, String Library,String Name,String Artist,String Album,String Year,String Gen,String Lyrics,byte[] Data,int local) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call insert_song(?,?,?,?,?,?,?,?,?,?) }");
		proc_stmt.setString(1, User);
		proc_stmt.setString(2, Name);
		proc_stmt.setString(3, Artist);
		proc_stmt.setString(4, Album);
		proc_stmt.setString(5, Gen);
		proc_stmt.setString(6, Year);
		proc_stmt.setBytes(7, Data);
		proc_stmt.setString(8, Library);
		proc_stmt.setString(9, Lyrics);
		proc_stmt.setInt(10, local);
		proc_stmt.executeUpdate();
	}
	
	//Devuelve bytes para el stream de una cancion, por
	public byte[] retrieve_song(int id) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call retrieve_song(?) }");
		proc_stmt.setInt(1,id);
		ResultSet rs = proc_stmt.executeQuery();
		byte[] res = null;
		while (rs.next()) {
			res = rs.getBytes("Data");
		}
		return res;
	}
	
	
	// actualiza una cancion que esta en cloud
	public void update_song(int id,String Name,String Artist,String Album,String Year,String Gen,String Lyrics) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call update_song(?,?,?,?,?,?,?) }");
		proc_stmt.setInt(1, id);
		proc_stmt.setString(2, Name);
		proc_stmt.setString(3, Artist);
		proc_stmt.setString(4, Album);
		proc_stmt.setString(5, Gen);
		proc_stmt.setString(6, Year);
		proc_stmt.setString(7, Lyrics);
		proc_stmt.executeUpdate();
	}
	
	// actualizar para commit
	public void update_song_from_local(int local,String User,String Name,String Artist,String Album,String Year,String Gen,String Lyrics) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call update_song_from_local(?,?,?,?,?,?,?,?) }");
		proc_stmt.setInt(1, local);
		proc_stmt.setString(2, User);
		proc_stmt.setString(3, Name);
		proc_stmt.setString(4, Artist);
		proc_stmt.setString(5, Album);
		proc_stmt.setString(6, Gen);
		proc_stmt.setString(7, Year);
		proc_stmt.setString(8, Lyrics);
		proc_stmt.executeUpdate();
	}
	
	//despliegue de versiones de una cancion
	public List<List<String>> get_songs_versions(int id) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call get_song_versions(?) }");
		proc_stmt.setInt(1,id);
		ResultSet rs = proc_stmt.executeQuery();
		List<List<String>> tmp = new ArrayList<List<String>>();
		while (rs.next()) {
			List<String> tmp2 = new ArrayList<String>();
			tmp2.add(rs.getString("Song_Name"));
			tmp2.add(rs.getString("Song_Artist"));
			tmp2.add(rs.getString("Song_Album"));
			tmp2.add(rs.getString("Song_Year"));
			tmp2.add(rs.getString("Song_Gen"));
			tmp2.add(rs.getString("Lyrics"));
			tmp2.add(rs.getString("Song_Version"));
			tmp.add(tmp2);
		}
		return tmp;
		
	}
	// devuelve los ids de las canciones de un usuario
	public List<Integer> get_song_id(String User) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call get_songID_local(?) }");
		proc_stmt.setString(1,User);
		ResultSet rs = proc_stmt.executeQuery();
		List<Integer> tmp = new ArrayList<Integer>();
		while (rs.next()) {
			tmp.add(rs.getInt("Song_ID"));	
		}
		return tmp;
	}
	
	// me verifica si una cancion ya existe.. para commit.. si  existe devuelve true
	public boolean exist(String User,int localid) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call get_songID_ids (?) }");
		proc_stmt.setString(1,User);
		ResultSet rs = proc_stmt.executeQuery();
		boolean flag = false;
		while (rs.next()) {
			if(localid==rs.getInt("LocalID")){
				flag=true;
			}
		}
		return flag;
	}
	
	// me verifica si la cancion tiene cambios.. para commit devuelve true si tiene cambios
	public boolean have_changes(String User,int local,String Name,String Artist,String Album,String Year,String Gen,String Lyrics) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call get_song_data(?,?) }");
		proc_stmt.setString(1,User);
		proc_stmt.setInt(2,local);
		ResultSet rs = proc_stmt.executeQuery();
		boolean flag = false;
		while (rs.next()) {
			if(Name.equals(rs.getString("Song_Name"))&&
			Artist.equals(rs.getString("Song_Artist"))&&
			Album.equals(rs.getString("Song_Album"))&&
			Year.equals(rs.getString("Song_Year"))&&
			Gen.equals(rs.getString("Song_Gen"))&&
			Lyrics.equals(rs.getString("Lyrics"))){
				
				flag=true;
			}
		}
		return !flag;
	
	}
	
	// setea a una version establecida
	public void set_version(int id,int version) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call set_version(?,?) }");
		proc_stmt.setInt(1, id);
		proc_stmt.setInt(2,version);
		proc_stmt.executeUpdate();
		
	}
	public int getid(String User,int localid) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call get_my_id_m8(?,?) }");
		proc_stmt.setString(1, User);
		proc_stmt.setInt(2,localid);
		ResultSet rs = proc_stmt.executeQuery();
		int tmp=0;
		while (rs.next()) {
			tmp=rs.getInt("Song_ID");
			
		}
		return tmp;
	}
	
	
	// metodo para commit de canciones
	public void recieve_from_local(String User,String Library,String Name,String Artist,String Album,String Year,String Gen,String Lyrics,byte[] Blob,int Local) throws SQLException{
		if (exist(User,Local)){
			insert_song(User,Library,Name,Artist,Album,Year,Gen,Lyrics,Blob,Local);
		}
		else{
			update_song_from_local(Local,User,Name,Artist,Album,Year,Gen,Lyrics);
		}
		
	}
	
	public List<String> get_songs_lib2(int id) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call get_songs_lib(?) }");
		proc_stmt.setInt(1,id);
		ResultSet rs = proc_stmt.executeQuery();
		List<String> tmp2 = new ArrayList<String>();
		while (rs.next()) {
			tmp2.add(rs.getString("Song_Name"));
			tmp2.add(rs.getString("Song_Artist"));
			tmp2.add(rs.getString("Song_Album"));
			tmp2.add(rs.getString("Song_Year"));
			tmp2.add(rs.getString("Song_Gen"));
			tmp2.add(rs.getString("Lyrics"));
			tmp2.add(rs.getString("LocalID"));
			
		}
		return tmp2;
		
	}
	

	public void close() throws SQLException {
		connection.close();
		
	}

	public static void main(String args[]) throws SQLException, ClassNotFoundException {
		
		comunication pinga = new comunication();
		pinga.open();
		byte[] data = "asdfasdfasdfasdfasdf".getBytes();
		pinga.insert_song("santi2", "1", "pinga", "fafa", "cancer", "213", "caca", "gayu ay", data, 1);
		pinga.close();
		
	}
	
	public List<Integer> idslocal(String User) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call get_songID_ids (?) }");
		proc_stmt.setString(1,User);
		ResultSet rs = proc_stmt.executeQuery();
		List<Integer> tmp = new ArrayList<Integer>();
		while (rs.next()) {
			tmp.add(rs.getInt("LocalID"));
		}
		return tmp;
	}
	
	public void drop_song(int id) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call drop_song(?) }");
		proc_stmt.setInt(1,id);
		proc_stmt.executeUpdate();
	}
	
	public List<Integer> finder(String word) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call finder(?) }");
		proc_stmt.setString(1,word);
		ResultSet rs = proc_stmt.executeQuery();
		List<Integer> tmp = new ArrayList<Integer>();
		while (rs.next()) {
			tmp.add(rs.getInt("Song_ID"));
		}
		return tmp;
	}
	
/*	public List<String> get_songs_lib(int id) throws SQLException{
		CallableStatement proc_stmt = connection.prepareCall("{ call get_songs_lib(?) }");
		proc_stmt.setInt(1, id);
		ResultSet rs = proc_stmt.executeQuery();
		List<String> tmp = new ArrayList<String>();
		while (rs.next()) {
			tmp.add(rs.getString("Song_ID"));
			tmp.add(rs.getString("Song_Name"));
			tmp.add(rs.getString("Song_Artist"));
			tmp.add(rs.getString("Song_Album"));
			tmp.add(rs.getString("Song_Year"));
			tmp.add(rs.getString("Song_Gen"));
			tmp.add(rs.getString("Lyrics"));
			
		}
		return tmp;
		
	}
*/
}