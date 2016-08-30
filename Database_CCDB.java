
public class Database_CCDB{

	String mySQL_ConnectionString = "";

	public Database_CCDB(String connectionString){

		this.mySQL_ConnectionString = connectionString;

		// connect to the datbase
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
}
