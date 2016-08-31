import org.jlab.ccdb.CcdbPackage;
import org.jlab.ccdb.JDBCProvider;
import org.jlab.ccdb.SQLiteProvider;
import org.jlab.ccdb.MySqlProvider;
import org.jlab.ccdb.Assignment;
import org.jlab.ccdb.*;
import java.util.Vector;


public class Database_CCDB{

	private String mySQL_ConnectionString = "";
	private JDBCProvider provider;


	public Database_CCDB(String connectionString){

		this.mySQL_ConnectionString = connectionString;

		// connect to the datbase
		this.connect(this.mySQL_ConnectionString);
	}

	/**
	 * Connects the JDBCProvider to the CCDB.
	 * @param connectionStr the string that corresponds to the connection to the CCDB. This string must either begin with "mysql://" or "sqlite://". An error will be thrown if it does not start with those two cases.
	 * @return returns true if the connection was successfully closed, false otherwise.
	 */
	public boolean connect(String connectionStr){

		this.provider = getProvider(connectionStr);
		this.provider.connect();

		if (this.provider.getIsConnected()){
			return true;
			System.out.println("Connection to : " + connectionStr + ".");
		}else{
			return false;
		}
	}

	public boolean closeConnection(){

	}

	public void selectRowInTable(String tablename, int rowNum){

	}

	public String[] getRowInTable(String tablename, int rowNum){

	}

	public void searchBy(String tablename, String columnName, String filterBy){

	}

	public void describeTable(){

	}

	public boolean alterTable(String tablename, String newColumn){

	}

	public boolean deleteTable(String tablename){

	}

	public boolean insertIntoTable(double[] values, String tablename){

	}

	public void showTables(){

	}
	
	public static void main (String[] args){

		Database_CCDB ccdb = new Database_CCDB("")
	}
}
