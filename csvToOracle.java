package clearing.feed;

import java.io.*;
import au.com.bytecode.opencsv.CSVReader;
import java.sql.*;

public class csvToOracle 
{
	/*-----------------------------------------------------------------------------------------------------------------------*/
	static Connection conn = null;
	static PreparedStatement sql_statement = null;
	static Statement stmt=null;
	static String tablename=null;
	static ResultSet rs=null;
	static String inputCSVFile = null;
	static CSVReader reader = null;
	/*-----------------------------------------------------------------------------------------------------------------------*/
	public static boolean initialize()
	{
		try 
		{
			Class.forName ("oracle.jdbc.driver.OracleDriver");
			conn= DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "CITI", "root123");

			tablename = inputCSVFile.substring(inputCSVFile.lastIndexOf("/")+1, inputCSVFile.indexOf("."));

			/* Create table statement */
			String jdbc_create_sql="create table "+tablename+"(TransactionRef varchar(12)"
					+ ",ValueDate varchar(10) NOT NULL"
					+ ",PayerName varchar(35) NOT NULL"
					+ ",PayerAccount varchar(12) NOT NULL"
					+ ",PayeeName varchar(35) NOT NULL"
					+ ",PayeeAccount varchar(12) NOT NULL"
					+ ",Amount varchar(13) NOT NULL"
					+ ",CONSTRAINT PK_"+tablename+" PRIMARY KEY(TransactionRef))";
			System.out.println(jdbc_create_sql);
			stmt=conn.createStatement();
			stmt.execute(jdbc_create_sql);
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
			return false;
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			return dropQuery();
		}
		return true;
	}
	/*-----------------------------------------------------------------------------------------------------------------------*/
	public static boolean dropQuery()
	{
		String dropQuery="drop table "+tablename;
		try 
		{
			stmt=conn.createStatement();
			stmt.execute(dropQuery);
			stmt.close();
			sql_statement.close();
			stmt.close();
			conn.commit();
			conn.close();
			reader.close();
		} 
		catch (SQLException e1) 
		{
			e1.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}			
		return false;
	}
	/*-----------------------------------------------------------------------------------------------------------------------*/
	public static boolean csvToTable(String file)
	{
		System.out.println("Inside CSV TO ORACLE!!!!!!");
		
		inputCSVFile = file;
		boolean value = false;
		if(initialize())
		{
			/* Create the insert statement */
			String jdbc_insert_sql = "INSERT INTO "+tablename+ " VALUES(?,?,?,?,?,?,?)";
			try 
			{
				sql_statement = conn.prepareStatement(jdbc_insert_sql);
			} 
			catch (SQLException e1) 
			{
				e1.printStackTrace();
				return dropQuery();
			}

			/* Read CSV file in OpenCSV */
			try 
			{
				reader = new CSVReader(new FileReader(inputCSVFile));
				String [] nextLine; /* for every line in the file */            
				while ((nextLine = reader.readNext()) != null) 
				{
					/* Bind CSV file input to table columns */
					sql_statement.setString(1, nextLine[0]);
					sql_statement.setString(2,nextLine[1]);
					sql_statement.setString(3, nextLine[2]);
					sql_statement.setString(4,nextLine[3]);
					sql_statement.setString(5, nextLine[4]);
					sql_statement.setString(6,nextLine[5]);
					sql_statement.setString(7,nextLine[6]);
					/* execute the insert statement */
					sql_statement.executeUpdate();
				}               
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
				return false;
			}
			catch (SQLException e) 
			{
				System.out.println("Failed!!!");
				e.printStackTrace();
				return dropQuery();
			}
			catch(IOException e){e.printStackTrace();}
			catch(ArrayIndexOutOfBoundsException e)
			{
				System.out.println("Failed!!!");
				e.printStackTrace();
				return dropQuery();
			}
			if(retrieve())		
				value = true;
			else
				value = false;
		}
		try 
		{
			sql_statement.close();/* Close prepared statement */
			stmt.close();
			conn.commit();/* COMMIT transaction */
			conn.close();/* Close connection */
			reader.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}
	/*-----------------------------------------------------------------------------------------------------------------------*/

	public static boolean retrieve()
	{
		try
		{

			String query="select * from "+tablename;
			stmt=conn.createStatement();

			rs=stmt.executeQuery(query);

			String PayerName,PayeeName,Amount,TransactionRef,PayerAccount,PayeeAccount,d;
			while(rs.next())
			{
				TransactionRef=rs.getString(1);
				d=rs.getString(2);
				PayerName=rs.getString(3);
				PayerAccount=rs.getString(4);
				PayeeName=rs.getString(5);
				PayeeAccount=rs.getString(6);
				Amount=rs.getString(7);

				System.out.println(TransactionRef+"\t\t"+d+"\t\t"+PayerName+"\t\t"+PayerAccount+"\t\t"+PayeeName+"\t\t"+PayeeAccount+"\t\t"+Amount);
				Boolean[] b=new Boolean[7];
				b[0]=Validate.validateTransactionRef(TransactionRef);
				b[1]=Validate.validateDate(d);
				b[2]=Validate.validateName(PayerName);
				b[3]=Validate.validateAccount(PayerAccount);
				b[4]=Validate.validateName(PayeeName);
				b[5]=Validate.validateAccount(PayeeAccount);
				b[6]=Validate.validateAmount(Amount);
				for(int i=0 ;i<b.length ; i++)
				{
					if(b[i]==false)
						return dropQuery();
					System.out.println(b[i]);
				}
				Feed feed = new Feed();
				feed.setTransactionRef(TransactionRef);
				feed.setValueDate(d);
				feed.setPayer(PayerName+" "+PayerAccount);
				feed.setPayee(PayeeName+" "+PayeeAccount);
				feed.setAmount(Double.parseDouble(Amount));
				FeedList.FeedData.add(feed);
			}
			System.out.println(FeedList.FeedData.toString());
		}
		catch(SQLException se)
		{
			System.out.println("Failed!!!");
			se.printStackTrace();
			return dropQuery();
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Failed!!!");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/*public static void main(String[] args){ 
	}*/
}