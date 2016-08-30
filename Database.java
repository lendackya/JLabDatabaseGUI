package org.clas.detector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.sql.DriverManager; 
import java.sql.Driver;
//import java.util.PriorityQueue;
import java.util.Collections;

public class Database {
	
	protected Connection connection; // the connection to the database. Allows the user to modify and display information to a given database. 
	
	/**
	 * Constructor for the Database class. The constructor for this class establishes a connection to a given database. 
	 * @param databaseName the name of the database that you are connecting to.
	 * @param port the port number of the connection. 
	 * @param userName the username of the person signing on to the database.
	 * @param password the user's password.
	 * 
	 */
	public Database(String databaseName, String port, String userName, String password){
		
		try{
			this.connection = getConnection(databaseName, port, userName, password); 
			System.out.println("Open Connection to Database: " + databaseName);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Closes the connection to the database. 
	 * @return returns true if the connection was succesully closed, false otherwise.  
	 */
	public boolean closeConnection(){
				
		try{
			this.connection.close();
			return true; 
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false; 
		}
	}
	
	/**
	 * Prints a specifcied row of a table in a database to the screen. The row number entered must be positive, and start at 1. 
	 * 
	 * @param tablename the name of the table being printed from. 
	 * @param rowNum the row number in the database you want to access. This number must be positive and begin at 1. 
	 */
	public void selectRowInTable(String tablename, int rowNum){
		
		try{
			PreparedStatement st = this.connection.prepareStatement("SELECT * FROM " + tablename + " WHERE id = " + rowNum + ";"); 
			ResultSet rs = st.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData(); 
				
			while(rs.next()){
				for (int i = 1; i <= rsmd.getColumnCount(); i++){
				
					System.out.print(rs.getString(i) + "\n");
				}
			}
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	
	/**
	 * Returns a specified row out of a table in the database as an array of strings.
	 * 
	 * @param tablename the name of the table being printed from.
	 * @param rowNum the row number in the database you want to access. This number must be positive and begin at 1. 
	 * @return returns the row in the table specified
	 */
	public String[] getRowInTable(String tablename, int rowNum){
		
		try{
			PreparedStatement st = this.connection.prepareStatement("SELECT * FROM " + tablename + " WHERE id = " + rowNum + ";"); 
			ResultSet rs = st.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData(); 
		
			String[] arry = new String[46]; 
			
			while(rs.next()){
				for (int i = 1; i <= rsmd.getColumnCount(); i++){
					arry[i-1] = rs.getString(i); 
				}
			}
			return arry; 
		}catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}	
	}
		
	/**
	 * Prints all the values of a given tablename in the database filtered by some String value. 
	 * 
	 * @param
	 * @param
	 * @param

	 */
	public void searchBy(String tablename, String columnName, String filterBy){
		
		try{
			PreparedStatement st = this.connection.prepareStatement("SELECT * FROM " + tablename + " WHERE " + columnName + " LIKE " + "'" + filterBy + "'" + ";"); 
			ResultSet rs = st.executeQuery();
				
		while(rs.next()){
			System.out.println(rs.getString(2)); 
		}
		
		}catch(Exception e){
			System.out.println(e.getMessage());
		}		
		
	}
	
	/**
	 * Inputs a given text file to a table in a database. 
	 * To import a text file into the database, each new column value must be separated by tabs.
	 * If an entry does not contain a value, '\N' must be inserted to indicate NULL. 
	 * 
	 * @return returns true if successful, false otherwise. 
	 */
	public boolean fileToDatabase(String tablename, String infile){
		
		try{	
			PreparedStatement statement = this.connection.prepareStatement("LOAD DATA INFILE " + "'" + infile + "'" + " INTO TABLE " + tablename + ";");
			statement.executeUpdate(); 
			return true; 
		}catch(Exception e){
			System.out.println(e.getMessage()); 
			return false; 
		}
	}

	/**
	 * Displays to the screen the name of the columns and their data type. 
	 *
	 * @param tablename the name of the table you want to describe. 
	 * 
	 */
	public void describeTable(String tablename){
		
		try{
			
			PreparedStatement statement = this.connection.prepareStatement("DESCRIBE " + tablename); 
			ResultSet result = statement.executeQuery(); 
		
			while(result.next()){
				
				System.out.println(result.getString(1) + " --------- " + result.getString(2));
			}
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Adds a new column into the database to the last available position. 
	 *
	 * @param tablename the name of the table you want to delete. 
	 * @return returns true if the new column was succesfully added. false otherwise.  
	 */
	public boolean alterTable(String tablename, String newColumn){
		
		try{
			PreparedStatement statement = this.connection.prepareStatement("ALTER TABLE " + tablename +  " ADD " + newColumn + " double;");
			statement.executeUpdate(); 
			return true; 
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false; 
		}	
	}
	
	/**
	 * Deletes a table of a given name in the database. 
	 * 
	 * @param connection the connection to the database
	 * @param tablename the name of the table you want to delete. 
	 * @return returns true if the table was successfully deleted, false otherwise. 
	 */
	public boolean deleteTable(String tablename){
		
		try{
			PreparedStatement statement = this.connection.prepareStatement("DROP TABLE " + tablename); 
			statement.executeUpdate(); 
			return true; 
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false; 
		}	
	}
		
	/**
	 * Inserts into a table a value of type varchar (string) with a max length of 250 characters into a row of the database. 
	 * As of right now, the function is hardcoded to take 45 parameters (one for each of the MA-PMT parameters). I want to fix this in the future, to work in a gneral case. 
	 * For now, I will keep them hardcoded in. 
	 * 
	 * FIXME: In the Database class, it should use a parameter to create a varibale number 
	 * of columns from a given list. 
	 * 
	 * @param value the string value that being added to the database. 
	 * @param tablename the name of the table
	 * @param columnName the name of the column you are inserting the data into.
	 * @return returns true if the table was successfully inserted into, false otherwise. 
	 */
	public boolean insertIntoTable(double[] values, String tablename){
		
		try{ 	
			String sql = "INSERT INTO " + tablename + "(VER, PMT, NTUBE, RUNSET, RPOS, RUNT, HV, CLS, ALS, ADC1000"
					+ ",ADC1100, CBSI, GAIN, PIX, YIELD, CHI2, NU2, NU3, A1, A3, NUAV, SC"
					+ ",SSC, SIGMA, SSIGMA, MU, SMU, NU1, SNU1, A2"
					+ ",SA2, PNU2, SNU2, PA3, SA3, PNU3, SNU3, XI, SXI"
					+ ",BETA, SBETA, TAU, STAU, EFF20, EFF25)" +
			        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?)";
			
			PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
		
			for (int i = 0; i < 45; i++){
				preparedStatement.setDouble(i+1, values[i]);
			}
			preparedStatement.executeUpdate();
			return true; 
		}catch(Exception e){
			System.out.println(e.getMessage()); 
			return false; 
		}	
	}
	
	/**
	 * Displays all the tables of the connected database. 
	 *
	 */
	public void showTables(){
		
		try{
			PreparedStatement st = this.connection.prepareStatement("SHOW TABLES;");
			ResultSet rs = st.executeQuery();
			
			System.out.println("Table Names: ");
			while(rs.next()){
				System.out.print(rs.getString(1) + ", ");	
			}
			System.out.println("");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}	
	}
	
	/**
	 * Exports a table in the connected database to a text file. 
	 * The text file will be tabbed to indicate new column values, and will contain a '\N' where any values were left empty (NULL).
	 * 
	 * @param tablename the name of the table you wish to export. 
	 * @param outfile the name of the file you want to save the export as. This file cannot currently exist. 
	 * @return returns true if the database was successfully exported to the file. 
	 */
	public boolean databaseToFile(String tablename, String outfile){
		
		try {
			PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM " + tablename + " INTO OUTFILE " + "'" + outfile + "'" + ";"); 
			statement.executeQuery(); 		
			return true; 
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false; 
		}	
	}
	
	/**
	 * Selects a table in the database, and displays its content.
	 * Right now, this function does not display anything over 12 columns correctly. This will vary based on the size of your computer 
	 * monitor. I can show the values are still uploaded correctly via the databaseToFile method. 
	 * 
	 * @param tablename the name of the table you want to display. 
	 * @return returns true if the table was successfully selected, and false otherwise. 
	 */
	public boolean selectTable(String tablename){
		
		try{
			PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM " + tablename); 
			ResultSet result = statement.executeQuery(); 
			ResultSetMetaData rsmd = result.getMetaData();
			
			int numOfColumns = rsmd.getColumnCount(); 

			for (int i = 1; i <= 12; i++) {
		        if (i > 1){
		        	System.out.print(",     ");
		        }
		        String columnName = rsmd.getColumnName(i);
		        System.out.print(columnName);
		      }
		      System.out.println("");
		      
		      while (result.next()) {
		          for (int i = 1; i <= 12; i++) {
		            if (i > 1){
		            	System.out.print(",     ");
		            }
		            String columnValue = result.getString(i);
		            System.out.print(columnValue);
		          }
		          System.out.println("");  
		        }
		 
			return true; 
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false; 
		}	
	}
	
	/** 
	 * Adds a new table to the database of the given connection. 
	 * 
	 * @return returns true if the table was created successfully, false otherwise.  
	 * @throws Exception throws an exception if a table with the name already exists.
	 */
	public boolean createTable(String tablename) throws Exception{
		
		try {
			PreparedStatement statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tablename + "(id int NOT NUll AUTO_INCREMENT, PRIMARY KEY(id))");
			statement.executeUpdate(); 
			return true; 
			 
		}catch(Exception e ){
			System.out.println(e.getMessage());
			return false; 		
		}
	}
	
	/**
	 * 
	 * Connects to a database associated with the values supplied by the parameters.
	 * Right now, it only connects to the database through the local host.
	 * The connection can be used to add, insert, modify tables, as well as insert values into the tables
	 * of the associated connected database. 
	 * 
	 * FIXME: The connection string corresponding to the mySQL needs to be changed depending on the IP address of the server. Right now this is not constant, 
	 * and changes 
	 * 
	 * @param port the port you want to connect to
	 * @param database the database you want to connect to 
	 * @param user username of user
	 * @return returns the connection to the database. 
	 * @throws Exception
	 */
	public static Connection getConnection(String database, String port, String user, String pass) throws Exception{
		Connection con = null; 
		
		try{
				
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				
			}catch(ClassNotFoundException e){
				System.out.println("Where is your mySQL JDBC Driver?\n");
				e.printStackTrace();
			}
			
			// this will change depending on the server and IP address. 
			con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:" + port + "/" + database, user, pass);		
			return con; 
		}catch (Exception e){
			System.out.println(e.getMessage());
			return con; 
		}
	}
	
	/**
	 * Loads the data from a file and returns the data as a 2D String array.
	 * This method goes through the given file row by row, and at each row, separates the columns based on spacing.
	 * 
	 * @param filename the name of the file that you want to load in.
	 * @return returns the data as a 2D double array. 
	 */
	public static double[][] loadDataFromFile(String filename) throws FileNotFoundException{
		FileReader fr = new FileReader(filename); 
		BufferedReader br = new BufferedReader(fr); 
		
		int rowCounter = 0; // keeps track of what row  
		String row; 
		String[][] data = new String[768][45]; // FIXME: This needs to account for variable length
		
		try {	
			while((row = br.readLine()) != null){
				row = row.substring(2); 
				data[rowCounter] = row.split("  "); 
				rowCounter++; 
			}
			fr.close(); // close streams
			br.close();
		}catch (Exception e){
			System.out.println(e.getMessage());
		} 
				
		double[][] d = stringToDouble(data); 
		
		return d; 
	}
	
	public static double[][] stringToDouble(String[][] data_str){
		
		double[][] data = new double[data_str.length][45];
		
		for (int i = 0; i < data.length; i++){
			for (int j = 0; j < data_str[i].length; j++){ 
				if(data_str[i][j] == null){
					break;
				}else{
					System.out.println("Data_Str: " + data_str[i][j]);
					//System.out.println("Data: " + data_str[i][j]);
					data[i][j] = Double.parseDouble(data_str[i][j]); // stops if it encounters and empty string.
					System.out.println("Parsed: " + data[i][j]);
				}
			}
		}
		
		System.out.println("Data :\n\n\n" + data); 
		return data; 	
	}
	
	
	public static double[] stringToDouble_1D(String[] data_str){
		
		double[] data = new double[data_str.length];
		
		for (int i = 0; i < data_str.length; i++){
			//System.out.println("Data: " + data_str[i][j]);
			data[i] = Double.parseDouble(data_str[i]); // stops if it encounters and empty string.
			//System.out.println("Parsed: " + data[i][j]);
		}
		
		return data; 	
	}
	
	public static void main(String[] args){
		
		Database db = new Database("mapmts", "3306", "root", "root"); 
		
		//db.selectRowInTable("ca7452", 1);
		db.searchBy("ca7452", "gain", "1");
		
	}
}
