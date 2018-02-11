import java.io.*;
import java.util.*;
import java.util.Date;
import au.com.bytecode.opencsv.CSVReader;
import java.sql.*;
import java.text.SimpleDateFormat; 

public class csvToOracle {  
        
	public static void main(String[] args) throws Exception{ 
		
                /* Create Connection objects */
                Class.forName ("oracle.jdbc.driver.OracleDriver"); 
                Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
                PreparedStatement sql_statement = null;
                Statement stmt=null;
                
                String inputCSVFile = "D:/CitiBridgeProject/inputfile.csv"; //will be received from poller so this will change later
                String tablename=inputCSVFile.substring(inputCSVFile.lastIndexOf("/")+1, inputCSVFile.indexOf("."));
                
                /* Create table statement */
                String jdbc_create_sql="create table "+tablename+"(TransactionRef# number(12),ValueDate date,PayerName varchar(50),PayerAccount# number(12),PayeeName varchar(50),PayeeAccount# number(12),Amount number(12))";
                System.out.println(jdbc_create_sql);
                stmt=conn.createStatement();
                stmt.execute(jdbc_create_sql);
            
                /* Create the insert statement */
                String jdbc_insert_sql = "INSERT INTO "+tablename+ " VALUES(?,?,?,?,?,?,?)";
                sql_statement = conn.prepareStatement(jdbc_insert_sql);
                
                SimpleDateFormat dt = new SimpleDateFormat("dd-mm-yyyy"); 
                
                /* Read CSV file in OpenCSV */
                CSVReader reader = new CSVReader(new FileReader(inputCSVFile));
                /* Variables to loop through the CSV File */
                String [] nextLine; /* for every line in the file */            
                int lnNum = 0; /* line number */
                while ((nextLine = reader.readNext()) != null) {
                        lnNum++;
                        /* Bind CSV file input to table columns */
                        sql_statement.setLong(1, Long.parseLong(nextLine[0]));
                        Date date = dt.parse(nextLine[1]); 
                        sql_statement.setDate(2, new java.sql.Date(date.getTime()));
                        sql_statement.setString(3, nextLine[2]);
                        sql_statement.setLong(4,Long.parseLong(nextLine[3]));
                        sql_statement.setString(5, nextLine[4]);
                        sql_statement.setLong(6,Long.parseLong(nextLine[5]));
                        sql_statement.setLong(7,Long.parseLong(nextLine[6]));
                        /* execute the insert statement */
                        sql_statement.executeUpdate();
                }               
                /* Close prepared statement */
                sql_statement.close();
                stmt.close();
                /* COMMIT transaction */
                conn.commit();
                /* Close connection */
                conn.close();
                System.out.println("Success");
        }
}