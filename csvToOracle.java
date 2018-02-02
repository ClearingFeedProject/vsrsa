import java.io.*;
import java.util.*;
import au.com.bytecode.opencsv.CSVReader;
import java.sql.*; 
public class csvToOracle {  
        public static void main(String[] args) throws Exception{                
                /* Create Connection objects */
                Class.forName ("oracle.jdbc.driver.OracleDriver"); 
                Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "Rujuta", "oraclerujuta*");
                PreparedStatement sql_statement = null;
                /* Create the insert statement */
                String jdbc_insert_sql = "INSERT INTO SAMPLE"
                                + "(USER_ID, USER_NAME) VALUES"
                                + "(?,?)";
                sql_statement = conn.prepareStatement(jdbc_insert_sql);
                /* Read CSV file in OpenCSV */
                String inputCSVFile = "D:/CitiBridgeProject/sample.csv";
                CSVReader reader = new CSVReader(new FileReader(inputCSVFile));
                /* Variables to loop through the CSV File */
                String [] nextLine; /* for every line in the file */            
                int lnNum = 0; /* line number */
                while ((nextLine = reader.readNext()) != null) {
                        lnNum++;
                        /* Bind CSV file input to table columns */
                        sql_statement.setInt(1, Integer.parseInt(nextLine[0]));
                        /* Bind Age as double */
                        /* Need to convert string to double here */
                        sql_statement.setString(2,nextLine[1]);
                        /* execute the insert statement */
                        sql_statement.executeUpdate();
                }               
                /* Close prepared statement */
                sql_statement.close();
                /* COMMIT transaction */
                conn.commit();
                /* Close connection */
                conn.close();
        }
}