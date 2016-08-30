package org.clas.detector;

import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.sql.*;
import java.util.LinkedList;
import java.util.PriorityQueue; 
import java.util.Comparator;
import java.util.*;

/**
 * A class that is a subclass of Database. This class is used for specifically dealing with the MAPMT 
 * database that is on my (Andrew Lendacky) MAMP server.
 */
public class DatabasePMT extends Database {

	String[] paraNames; // used to store parameter names
	String[] pmtNames;// used to store PMT names
        Hashtable<String, Integer> ht; // used to store where in the file pmt begins. This may not be needed anymore?
	private DBAnalysisGraphs analysis; 
	private LinkedList<String> ca_list; 
	private LinkedList<String> ga_list; 
	private LinkedList<String> za_list; 
	
	/**
	 * Constructor for the DatabasePMT class. Calling this constuctor connects to a Database of given name, port, username, and password from 
	 * the super class. Upon calling this constructor, the arrays that are used to contain the parameter names and PMT names are initialized as well as 
	 * the Hashtable<String, Integer> used for the indirection symbol table. 
	 * 
	 */
	public DatabasePMT (String databaseName, String port, String userName, String password){
		
		super(databaseName, port, userName, password); // calls super constructor
		
		try{
			// init some arrays to help with things. These are used for housekeeping things 
			//this.paraNames = this.getParameterName("C:/Users/Andrew/Documents/Research/Database_JLab/Text_Files/parameter_list.txt");
			//this.pmtNames = this.loadMAPMTNames("C:/Users/Andrew/Documents/Research/Database_JLab/Text_Files/pmts_db.txt");
                        
                        this.paraNames = this.getParameterName("/Users/Andrew/Desktop/DatabaseGUI/JLabDatabaseGUI/Text_Files/parameter_list.txt");
			this.pmtNames = this.loadMAPMTNames("/Users/Andrew/Desktop/DatabaseGUI/JLabDatabaseGUI/Text_Files/pmts_db.txt");
                        
			this.ht = createIndirectionTable(); 
			//this.analysis = new DBAnalysisGraphs(this); // pass itself 
			this.ca_list = this.getCA_MAPMTs();
			this.ga_list = this.getGA_MAPMTs(); 
			this.za_list = this.getZA_MAPMTs();
		}catch(Exception e){
			
			System.out.println(e.getMessage());
		}
	}
		
	/**
	 * Loads the names of the MAPMTS stored in this database from a given text file.
	 *
	 * @param filename the file that contains the data you want to load in. This name should be the full path name of the file (for some reason).
	 * @return returns the data stored in an array.
	 * @throws FileNotFoundException throws an exception if the file is not found. 
	 */
	public static String[] loadMAPMTNames(String filename) throws FileNotFoundException{
		
		String[] data = new String[430]; //FIXME: This needs to account for a variable length list of mapmts.
                String word;
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		int i = 0; 
		
		try {
			while((word = br.readLine()) != null) {
				data[i] = word;
				i++;   
			}
			br.close(); // close stream 
			fr.close();
                        /*String[] data = new String[i];
                        
                        for(i=0;i<list.length;i++)
                            data[i]=list[i];*/
                        
		}catch (Exception e) {
			
			e.printStackTrace();
		} 
		return data; 
	}
		
	/**
	 * Searches the whole MAPMT database and adds the retrived information of 'paraName' to a MAPMT object and then 
	 * adds that object to a PMT_PQ. Once all PMTs are added to the PQ, the top item (smallest data value) is removed and displayed to the 
	 * user as well as the PMT that contained this data. Each item in the PQ is removed, and each time this is done, the PQ is updated 
	 * and the new smallest value is put on top. Removing each top value until the PQ is empty will display the information contained in the PQ from 
	 * smallest to largest.
	 * 
	 * @param paraName the name of the parameter that you want to sort by. 
	 */
        
        /*
	public void sortValueOf_Database(String paraName){
		
		PMT_PQ pq = new PMT_PQ(400);
		MAPMT pmt; 
		
		// get the name of the pmt from the pmt array. 
		// loop through the array
		for (int i = 0; i < this.pmtNames.length-1; i++){
			
			try{
				// go to that table in the database
				PreparedStatement st = this.connection.prepareStatement("SELECT * FROM " + this.pmtNames[i] + " WHERE " + paraName + ";"); 
				ResultSet rs = st.executeQuery();
				
				rs.next();		
				pmt = new MAPMT(this.pmtNames[i], rs.getDouble(paraName)); // create a new MAPMT object that holds the data
			
				pq.add(pmt); 
			
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
		System.out.println(pq.size()); 
		while (pq.size() != 0){
			pmt = pq.remove(pq); 
			System.out.println("PMT Name: " + pmt.serialNum + ", Data: " + pmt.data);
		}
	}
	*/
	// Getters: 
	public LinkedList<String> getCAXXXX(){
		
		return this.ca_list; 
	}
	
	public LinkedList<String> getGAXXXX(){
		
		return this.ga_list; 
	}
	
	public LinkedList<String> getZAXXXX(){
		
		return this.za_list; 
	}
	
	
	/**
	 * Returns the value associated with the given MAPMT of the parameter value PMT in the databse. 
	 * 
	 * @param pmt a string correpsonding to the name of the MAPMT. 
	 * 
	 * @return returns a Double correspodningt to the value in the database. 
	 */
	private Double getPMT_Parameter(String pmt){
		
		try{
			PreparedStatement st = this.connection.prepareStatement("SELECT * FROM " + pmt);
			ResultSet rs = st.executeQuery();
			
			rs.next(); 	
			return rs.getDouble("PMT"); 
		}catch(Exception e){
			System.out.println(e.getMessage());
			return null; 
		}
	}
		
	/**
	 * Searches the database for all the CAXXXX MAPMTs and adds them into a LinkedList, and  
	 * then the LinkedList is returned.
	 * 
	 * FIXME: HOLY WOW! THE RUNTIME OF THIS IS BAD
	 * 
	 * @param LinkedList<String> a LinkedList of String that contain the PMTs that start with CA
	 */
	private LinkedList<String> getCA_MAPMTs(){
		
		try{
			LinkedList<String> list = new LinkedList<String>(); 
		
			// go through the database tables
			for (int i = 0; i < this.pmtNames.length - 1; i++){
				// if the parameter: PMT equals 0
				if (this.getPMT_Parameter(this.pmtNames[i]) == 0){
					
					list.add(this.pmtNames[i]); 
				}
			}
				return list; 
			}catch(Exception e){
				
				System.out.println(e.getMessage());
				return null; 
			}	
	}
		
	/**
	 * Searches the database for all the GAXXXX MAPMTs and adds them into a LinkedList, and  
	 * then the LinkedList is returned.
	 * 
	 * FIXME: HOLY WOW! THE RUNTIME OF THIS IS BAD
	 * 
	 * @param LinkedList<String> a LinkedList of String that contain the PMTs that start with GA
	 */
	private LinkedList<String> getGA_MAPMTs(){
		
		try{
			LinkedList<String> list = new LinkedList<String>(); 
		
			// go through the database tables
			for (int i = 0; i < this.pmtNames.length - 1; i++){
		
				// if the parameter: PMT equals 1
				if (this.getPMT_Parameter(this.pmtNames[i]) == 1){
					
					list.add(this.pmtNames[i]); 
				}
			}
				return list; 	
			}catch(Exception e){
				
				System.out.println(e.getMessage());
				return null; 
			}		
	}
			
	/**
	 * Searches the database for all the ZAXXXX MAPMTs and adds them into a LinkedList, and  
	 * then the LinkedList is returned.
	 * 
	 * FIXME: HOLY WOW! THE RUNTIME OF THIS IS BAD
	 * 
	 * @param LinkedList<String> a LinkedList of String that contain the PMTs that start with ZA
	 */
	private LinkedList<String> getZA_MAPMTs(){
		
		try{
			LinkedList<String> list = new LinkedList<String>(); 
		
			// go through the database tables
			for (int i = 0; i < this.pmtNames.length - 1; i++){
			
				// if the parameter: PMT equals 1
				if (this.getPMT_Parameter(this.pmtNames[i]) == 2){
					
					list.add(this.pmtNames[i]); 
				}
			}
				return list; 	
			}catch(Exception e){
				
				System.out.println(e.getMessage());
				return null; 
			}			
	}
	
	/**
	 * Returns the DBAnalysisGraphs that allows for graphs to be produced from parameters from the database. 
	 * 
	 * @return the DBAnalysisGraphs object of this class. 
	 */
	public DBAnalysisGraphs Analyze(){
		
		return this.analysis; 
	} 
        
	/**
	 * Returns an array of doubles of a specific column in the database corresponding to a specified column
	 * at a given HV and OD. 
	 * 
	 * @param tablename the name of the table you want to access in the database. 
	 * @param hv the HV value you want the data for. This can be 1000, 1050, 1075, or 1100
	 * @param od the OD value you want the data for. This can be 13, 14, 15
	 * @param parameter the paramater whose values you want
	 * 
	 * @return returns an array of doubles holding the values for 64 pixels at a given HV and OD. 
	 * 
	 */
	public double[] getSpecificColumn_HVandOD(String tablename, int hv, int od, String parameter){
			
		try{
			int i = 0; 
			double[] data = new double[64]; // 64 pixels
			
			PreparedStatement statement = this.connection.prepareStatement("SELECT id, HV, RUNT, " + parameter +  " FROM " + tablename
					+ " WHERE HV= " + "'" + hv + "'" + " AND RUNT= "  + "'" + od + "';"); 
		
			ResultSet rs = statement.executeQuery(); 
			
			while (rs.next()){
				data[i] = rs.getDouble(parameter);
				i++; 
			}
			return data; 
		}catch(Exception e){
			
			System.out.println(e.getMessage());
			return null; 
		}	
	}
	
	/**
	 * Prints a specific column in the database corresponding to a specified column/parameter
	 * at a given HV and OD. 
	 * 
	 * @param tablename the name of the table you want to access in the database. 
	 * @param hv the HV value you want the data for. This can be 1000, 1050, 1075, or 1100
	 * @param od the OD value you want the data for. This can be 13, 14, 15
	 * @param parameter the paramater whose values you want
	 * 
	 * @return returns an array of doubles holding the values for 64 pixels at a given HV and OD. 
	 * 
	 */
	public void printSpecificColumn_HVandOD(String tablename, int hv, int od, String parameter){
			
		try{
			
			PreparedStatement statement = this.connection.prepareStatement("SELECT id, HV, RUNT, " + parameter +  " FROM " + tablename
					+ " WHERE HV= " + "'" + hv + "'" + " AND RUNT= "  + "'" + od + "';"); 
		
			ResultSet rs = statement.executeQuery(); 
			
			while (rs.next()){
				System.out.println(rs.getDouble(parameter)); 
			}
		}catch(Exception e){
			
			System.out.println(e.getMessage());

		}	
	}
	
	/**
	 * Prints the gain for a given PMT in the database. 
	 * 
	 * @param pmt a String representing the name of the MAPMT in the database. 
	 */
	public void getGain(String pmt){
			
		try{
			PreparedStatement st = this.connection.prepareStatement("SELECT * FROM " + pmt + " WHERE GAIN;"); 
			ResultSet rs = st.executeQuery(); 
			
			rs.next();
			System.out.println(rs.getDouble("GAIN")); 
		
		}catch(Exception e){
			
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Loads the names of the parameters stored in this database from a given file. 
	 * This array is used to error check accessing parameters not in the database. 
	 *
	 * @param filename the file that contains the data you want to load in. This name should be the full path name of the file (for some reason).
	 * @return returns the data stored in an array.
	 * @throws FileNotFoundException throws an exception if the file is not found. 
	 * 
	 * FIXME: change this method to account for a varibale number of parameters. Right now it's set to 45. 
	 * FIXME: change it so that the file coming in is of type LargeFile, we can get row numbers this way. 
	 */
	public static String[] getParameterName(String filename) throws FileNotFoundException{
		
		String[] names = new String[45]; // fixme 
		String row;
		FileReader fr = new FileReader(filename); 
		BufferedReader br = new BufferedReader(fr); 
		
		int counter = 0; // used to keep track of index 
		try {	
			while((row = br.readLine()) != null){
				names[counter] = row;
				counter++; 
			}	
		}catch (Exception e){
			System.out.println(e.getMessage());
		} 
						
		return names; 
	}
	
	/**
	 * Creates the indirection symbol table corresponding to where in the file each MAPMT begins. 
	 * For example, CA7452 has a value of 0, meaning it begins on the first line of the file. 
	 * This allows for quick access creating the chunk sizes from the file. 
	 * 
	 * Runtime: O(n) - where n is the number of PMTs. This runtime could be greater depending on what the runtime of Java's hashtable's put method. 
	 * 
	 * @return returns the Hashtable created. 
	 * FIXME: change this method to account for a varying starting position for the PMT. 
	 * 
	 */
	private Hashtable<String, Integer> createIndirectionTable(){
		
		Hashtable<String, Integer> ht = new Hashtable<String, Integer>(); 
		Integer start = 0; 
	
		for (int i = 0; i < this.pmtNames.length - 1; i++){

			ht.put(this.pmtNames[i], start);
			start += 768; 
		}
				
		return ht; 
	}
	
	/**
	 * Deletes and creates a table in the database. For testing purposes, this method first deletes the table, and then recreates it. 
	 * This method also creates the columns of the table via 'alterTable()'
	 * FIXME: In the Database class, it should use a parameter to create a varibale number of columns from a given list. 
	 * 
	 * Runtime: O(kn) - where n is the number of PMTs and k is the number of parameters to add. 
	 */
	public void createNewTable(String tablename){
		
		try{
			deleteTable(tablename); // delete table 
			createTable(tablename); // craete table
		
			for (int i = 0; i < this.paraNames.length; i++){
			
				alterTable(tablename, this.paraNames[i]);
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Adds all MA-PMTs to the database from a file name called "/Users/Andrew/Downloads/fruns1_passpt11.ntab.txt". 
	 * As of right now, this method is specific to the file mentioned above. Since this file contains the information for each PMT separated
	 * by 768 rows in the file, and uses the pmtArray contained in this class to find where each PMT begins in the file this method should only
	 * be used in this case, and should be changed if incorporating a different file or if the file has changed. 
	 * 
	 * Runtime: O(kn) - where k is the number for rows in the data array and n is the number of PMTs. 
	 * 
	 *  FIXME: Change this method so that it can add all PMTs from a file of varibale length and variable contents. 
	 */
        
        /*
	public void addAllPMTsToDatabase(){

		try{
			double[][] data; 
			// loop through the array of pmt names
			for (int i = 0; i < this.pmtNames.length - 1; i++){
				LargeFile file = new LargeFile("/Users/Andrew/Downloads/fruns1_passpt11.ntab.txt"); // load the with all parameter values
				
				createNewTable(this.pmtNames[i]); // delete and create a new table of given name
				// return a 768 x 45 array of doubles corresponding to the parameters for given pmt.
				// this method uses the indirection hashtable to find where each pmt starts in the file. 
				// FIXME: maybe create a data structure that keeps all starting positions for EVERY pmt, even ones not added. 
				data = file.getMatrixFromData(768, this.ht.get(this.pmtNames[i]));  
				// loop through each row in the data and enter it in the table. 
				for(int k = 0; k < data.length; k++){
					
					insertIntoTable(data[k], this.pmtNames[i]);
				}
				
				file.close(); // close file
			}			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}	
	}
	*/				
	/**
	 * A private subclass of PriorityQueue that is used to sort MAPMTs by a given piece of data obtained in the database. 
	 */
        
        /*
	private class PMT_PQ extends PriorityQueue<MAPMT>{
		
		/**
		 * The constructor for the PMT_PQ class. This constructor takes the size of the PriorityQueue as an argument and also 
		 * set the Comparartor to a new Comparator_MAPMT object. 
		 * 
		 */
                 /*
		public PMT_PQ(int size){
			
			super(size, new Comparator_MAPMT()); // calls superclass constructor
		}	
	}
	
	/**
	 * A private class that is used to compare data stored in a MAPMT object to be sorted in a PriorityQueue.
	 */
        
        /*
	private class Comparator_MAPMT implements Comparator<MAPMT>{
		
		/**
		 * Compares the data stored in a MAPMT object to another piece of data stored in another MAPMT. 
		 * Both of these pieces of data are doubles. 
		 * This is used to sort MAPMTS by a given piece of data in a PriorityQueue.
		 */
        
                 /*
		public int compare(MAPMT pmtOne, MAPMT pmtTwo){
			
			if (pmtOne.data < pmtTwo.data){
				return -1;
			}
			
			if (pmtOne.data > pmtTwo.data){
				return 1; 
			}
			return 0;
		}	
	}
	*/
        
	public static void main(String args[]){
	
		DatabasePMT db = new DatabasePMT("mapmts", "3306", "root", "root"); 

	}
}
