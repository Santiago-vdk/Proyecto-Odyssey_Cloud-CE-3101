package odyssey.storage;

import java.sql.*;

public class comunication {

	private static comunication _singleton = new comunication();
	Connection connection;
	Statement statement;
	String connectionString = 
            "jdbc:sqlserver://hyysfso8a0.database.windows.net:1433" + ";" +  
                "database=OdysseyDB" + ";" + 
                "user=Odyssey@hyysfso8a0" + ";" +  
                "password=x100preXD";

	/*
	 * A private Constructor prevents any other class from instantiating.
	 */
	private comunication(){ }

	/* Static 'instance' method */
	public static comunication getInstance() {
		return _singleton;
	}

	/* Other methods protected by singleton-ness */
	protected static void demoMethod() {
		System.out.println("demoMethod for singleton");
	}
	
	//cambiar parametros de conexion
	public void open(){
		

	       
		 connection = null; 
	     statement = null;   

	}
	public void prueba() throws SQLException, ClassNotFoundException{
		
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connection = DriverManager.getConnection(connectionString);
        String sqlString = 
        		 "SET IDENTITY_INSERT Person ON " + 
        		            "INSERT INTO Person " + 
        		            "(PersonID, LastName, FirstName) " + 
        		            "VALUES(11, 'Tetas', 'Culo')";
        statement = connection.createStatement();
        statement.executeUpdate(sqlString);

        // Provide a message when processing is complete.
        System.out.println("Processing complete.");
		
	}
	public void close() throws SQLException{
		connection.close();
	}
	public static void main(String args[]) throws SQLException, ClassNotFoundException {
		comunication.getInstance().open();
		comunication.getInstance().prueba();
		comunication.getInstance().close();
	}
       
    }


