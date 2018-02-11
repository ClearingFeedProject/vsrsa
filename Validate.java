package clearing.feed;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.NumberFormat;


//all methods here are static so they can be directly called with classname.methodname :)
public class Validate 
{
	//should be unique i think that has to be checked before this function is called bcz in this no data will be available
	public static boolean validateTransactionRef(String s)//Payee or Payer account
	{
		if(s.length()==12)
		{
			String pattern= "^[a-zA-Z0-9]*$";//regex for alpha-numeric string
			return s.matches(pattern);
		}
		else
			return false;
	}

	public static boolean validateDate(String tdate)
	{
		if(tdate.length()==10)//length of input shud b 10
		{
			//get current date
		    Date date = Calendar.getInstance().getTime();
		    System.out.println("-------uuuu> "+date);
		    // Display a date in day, month, year format
		    SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
		    String today = formatter.format(date);
		    
			System.out.println(tdate + "          "+today);

			if(today.equalsIgnoreCase(tdate))
			{
				return true;
			}
				
			else
				return false;
		}
		else
			return false;
	}
	public static boolean validateName(String s)//Payee or Payer name
	{
		if(s.length()>=1 && s.length()<=35)
		{
			String pattern= "^[a-zA-Z0-9]*$";//regex for alpha-numeric string
			return s.matches(pattern);
		}
		else
			return false;
	}
	public static boolean validateAccount(String s)//Payee or Payer account
	{
		if(s.length()==12)
		{
			String pattern= "^[a-zA-Z0-9]*$";//regex for alpha-numeric string
			return s.matches(pattern);
		}
		else
			return false;
	}
	public static boolean validateAmount(String s)
	{	
		try
		{
			 Double amount = Double.parseDouble(s);
			 if(s.contains("."))
			 {
				String[] splitter = s.split("\\.");
			
				if (splitter[1].length() == 2 && splitter[0].length()==10)//length : 10,2
				{
					 Locale indian = new Locale("en", "IN");
					 NumberFormat indianFormat = NumberFormat.getCurrencyInstance(indian);
					 System.out.println("Indian: " + indianFormat.format(amount));
					return true;
				}
				else
					return false;
			}
			else
			{
				return false;//for now i am considering it must contain decimal point
			}
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public static void main(String[] args) 
	{
	/*	System.out.println("\n___Transaction___");
		System.out.println("111111111111 - "+validateAccount("111111111111"));
		System.out.println("1111111 - "+validateAccount("11111111"));
		System.out.println("1111111*** - "+validateAccount("1111111***"));
		
		System.out.println("\n___Amount___");
		System.out.println("5555555555.02 - "+validateAmount("5555555555.02"));
		System.out.println("1555555.025555 - "+validateAmount("1555555.025555"));
		System.out.println(".02 - "+validateAmount(".02"));
		System.out.println("vidhi - "+validateAmount("vidhi"));
		System.out.println("vidhi. - "+validateAmount("vidhi."));
		System.out.println("$150000.02 - "+validateAmount("$150000.02"));//$not allowed
		
		System.out.println("\n___Account(either payer or payee)___");
		System.out.println("111111111111 - "+validateAccount("111111111111"));
		System.out.println("1111111 - "+validateAccount("11111111"));
		System.out.println("1111111*** - "+validateAccount("1111111***"));
		
		System.out.println("\n___Name(either payer or payee)___");
		System.out.println("Agrawal Vidhi123 - "+validateAccount("Agrawal Vidhi123"));
		System.out.println("Empty - "+validateAccount(""));
		System.out.println("*** - "+validateAccount("***"));*/
		
		System.out.println("\n___Date___");
		System.out.println("12-05-2018 - "+validateDate("12-05-18"));
		System.out.println("02-02-2018 - "+validateDate("02-02-2018"));//will work only for todays date i.e date on ur system
	}
}