package clearing.feed;

import java.io.*;
import java.util.Date;
import au.com.bytecode.opencsv.CSVReader;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat; 

public class csvToOracle {  
     
	@SuppressWarnings("resource")
	public static boolean csvToTable(String inputCSVFile)
	{
		System.out.println("Inside CSV TO ORACLE!!!!!!");
		Connection conn = null;
		PreparedStatement sql_statement = null;
        Statement stmt=null;
        //String inputCSVFile = null;
        String tablename=null;
        ResultSet rs=null;
        //int PKcntr=0; 
            /* Create Connection objects */
        try 
        {
			Class.forName ("oracle.jdbc.driver.OracleDriver");
			conn= DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "CITI", "root123");
				
                
            //inputCSVFile = "D:/CitiBridgeProject/Files/input.csv"; //will be received from poller so this will change later
            if(inputCSVFile.substring(inputCSVFile.indexOf(".")+1).equals("csv"))
            {
                System.out.println("Input file naming is in correct format");
                tablename=inputCSVFile.substring(inputCSVFile.lastIndexOf("/")+1, inputCSVFile.indexOf("."));
	                
	            /* Create table statement */
	            String jdbc_create_sql="create table "+tablename+"(TransactionRef number(12)"
	                		+ ",ValueDate date NOT NULL"
	                		+ ",PayerName varchar(35) NOT NULL,PayerAccount number(12) NOT NULL"
	                		+ ",PayeeName varchar(35) NOT NULL,PayeeAccount number(12) NOT NULL"
	                		+ ",Amount varchar(13) NOT NULL"
	                		+ ",CONSTRAINT PK_"+tablename+" PRIMARY KEY(TransactionRef))";
			    System.out.println(jdbc_create_sql);
			    stmt=conn.createStatement();
			    stmt.execute(jdbc_create_sql);
			}
			else
			{
				System.out.println("File not in correct format");
			}               
        } 
        catch (ClassNotFoundException e) 
        {
        	return false;
			//e.printStackTrace();
		}
        catch (SQLException e) 
        {
        	//e.printStackTrace();
        	//System.out.println("Errorcode:"+e.getErrorCode());
            	
			String dropQuery="drop table "+tablename;
        	try 
        	{
				stmt=conn.createStatement();
				stmt.execute(dropQuery);
				//System.out.println("Run program again.");
				//System.exit(0);
				return false;
			} 
        	catch (SQLException e1) 
        	{
				//e1.printStackTrace();
        		return false;
			}
		}

            
        /* Create the insert statement */
        String jdbc_insert_sql = "INSERT INTO "+tablename+ " VALUES(?,?,?,?,?,?,?)";
        try 
        {
			sql_statement = conn.prepareStatement(jdbc_insert_sql);
		} 
        catch (SQLException e1) 
        {
        	//System.out.println("Errorcode:"+e1.getErrorCode());
			//e1.printStackTrace();
			try 
        	{
				String dropQuery="drop table "+tablename;
				stmt=conn.createStatement();
				stmt.execute(dropQuery);
				System.out.println("Run program again.");
				//System.exit(0);
				stmt.close();
				return false;
			} 
        	catch (SQLException e11) {
				// TODO Auto-generated catch block
				e11.printStackTrace();
			}
			//System.exit(0);
			return false;
		}
                           
        SimpleDateFormat dt = new SimpleDateFormat("dd-mm-yyyy"); 
            
        /* Read CSV file in OpenCSV */
        CSVReader reader = null;
		try 
		{
			reader = new CSVReader(new FileReader(inputCSVFile));
			/* Variables to loop through the CSV File */
            String [] nextLine; /* for every line in the file */            
            //int lnNum = 0; /* line number */
            while ((nextLine = reader.readNext()) != null) 
            {
                    //lnNum++;
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
		} 
		catch (FileNotFoundException e) 
		{
			//e.printStackTrace();
			return false;
		}
		catch (SQLException e) 
        {
			System.out.println("Failed!!!");
			e.printStackTrace();
			//System.out.println("Errorcode:"+e.getErrorCode());
			String dropQuery="drop table "+tablename;
        	try 
        	{
				stmt=conn.createStatement();
				stmt.execute(dropQuery);
				//System.out.println("Run program again.");
				//System.exit(0);
				reader.close();
				return false;
			} 
        	catch (SQLException e1) 
        	{
        		try {
					reader.close();
				} catch (IOException e2) {
					//e2.printStackTrace();
				}
				//e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			//System.exit(0);
        	return false;
		}
		catch(IOException e)
		{
			
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Failed!!!");
			e.printStackTrace();
			
			String dropQuery="drop table "+tablename;
        	try 
        	{
				stmt=conn.createStatement();
				stmt.execute(dropQuery);
				System.out.println("Number of fields in record is incorrect");				
				return false;
				//System.exit(0);
			} 
        	catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	//System.exit(0);
        	return false;
		}
			try{
				
				String query="select * from "+tablename;
				stmt=conn.createStatement();
				
				rs=stmt.executeQuery(query);
				long TransactionRef,PayerAccount,PayeeAccount;
				Date d;
				String PayerName,PayeeName,Amount;
				while(rs.next())
				{
					TransactionRef=rs.getLong(1);
					d=rs.getDate(2);
					PayerName=rs.getString(3);
					PayerAccount=rs.getLong(4);
					PayeeName=rs.getString(5);
					PayeeAccount=rs.getLong(6);
					Amount=rs.getString(7);
					
					System.out.println(TransactionRef+"\t\t"+d+"\t\t"+PayerName+"\t\t"+PayerAccount+"\t\t"+PayeeName+"\t\t"+PayeeAccount+"\t\t"+Amount);
					
				}
				System.out.println("Success");
				/* Close prepared statement */
                sql_statement.close();
                stmt.close();
                /* COMMIT transaction */
                conn.commit();
                /* Close connection */
                conn.close();
                try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}

			}
			catch(SQLException se)
			{
				System.out.println("Failed!!!");
				System.out.println("Errorcode:"+se.getErrorCode());
				se.printStackTrace();
				String dropQuery="drop table "+tablename;
            	try 
            	{
					stmt=conn.createStatement();
					stmt.execute(dropQuery);
				} 
            	catch (SQLException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				}
				/* Close prepared statement */
                try 
                {
					sql_statement.close();
					stmt.close();
	                /* COMMIT transaction */
	                conn.commit();
	                /* Close connection */
	                conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
                

			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				System.out.println("Failed!!!");
				//System.exit(0);
				//e.printStackTrace();
				return false;
			}
            
            return true;
    }

	/*public static void main(String[] args){ 
	}*/
}