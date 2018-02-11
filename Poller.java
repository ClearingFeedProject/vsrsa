package clearing.feed;

import java.io.File;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.util.Set;
//import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.nio.file.WatchKey;
public class Poller 
{
	static String pathOfFile = "D:/Abc";
	Set<String> files = new HashSet<String>();
	int flag = 1;
	public void pollExisting()
	{
		System.out.println("Checking Existing Files If not Polled ..");
		File folder = new File(pathOfFile);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) 
	    {
			  if (listOfFiles[i].isFile()) 
			  {
				  //System.out.println("Found a File :: "+listOfFiles[i].getName());
				  if(listOfFiles[i].getName().contains(".csv"))
				  {
					  //System.out.println("Sending a File :: "+listOfFiles[i].getName());
					  //flag = 1;
					  files.add(listOfFiles[i].getName());
				  }
			  } 
	    }		
	}
	public void poll()
	{
		//WatchService lets u watch the directory continuously
		try(WatchService service = FileSystems.getDefault().newWatchService())
		//try with resources syntax bcz of this u don't need to explicitly write close method
		//and then ref to current file system
		{
			Map<WatchKey, Path> keyMap = new HashMap<>();//to store
			Path path = Paths.get(pathOfFile);
			keyMap.put(path.register(service, 
					StandardWatchEventKinds.ENTRY_CREATE, 
					StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY),
					path);
			//getting a path object and detail abt watch wat and store it in hashmap
			System.out.println("POLLING...");
			
			
			WatchKey watchKey;
			boolean status = false;
			Path eventPath = null;
			do
			{
				//System.out.println(".");
				watchKey = service.take();//get changes
				//Path eventDir = keyMap.get(watchKey);//see if it is in hashmap and get path from there				
				for(WatchEvent<?> event : watchKey.pollEvents())
				{
					System.out.println("....");
					if(flag==1)
					{
						pollExisting();
						flag = 0;						
					}
					//System.out.println("Existing : "+files);
					WatchEvent.Kind<?> kind = event.kind();//get the kind of change
					eventPath = (Path)event.context();//get the path of change
					//System.out.println(eventDir + " : " + kind + " : "+eventPath);//display
					String fileName = eventPath.toString();
					String fileExtension = eventPath.toString().substring(fileName.indexOf('.'), fileName.length());
					if((kind.equals(StandardWatchEventKinds.ENTRY_CREATE) && fileExtension.equals(".csv")))
					{			
						System.out.println("Polled File : "+fileName);
						files.add(eventPath.toString());
						status = csvToOracle.csvToTable(pathOfFile+"/"+fileName);
						//TimeUnit.SECONDS.sleep(30);
						System.out.println("Coming Back to Polling  -  " + status);
						if(status)
						{
							moveToArchive(fileName);
						}
						else
						{
							System.out.println("DELETE : "+pathOfFile+"/"+fileName);
							Files.delete(Paths.get(pathOfFile+"/"+fileName));
							files.remove(fileName);
						}
					}
					
				}
 				System.out.println("out of for loop------\n"+files);
				//System.out.println("Come back!!!!!");
			}while(watchKey.reset());//to clear things can also be written as while true
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	public void moveToArchive(String file)
	{
		try
		{
			System.out.println("Before removing Removing \n"+files);
			//TimeUnit.SECONDS.sleep(30);
			Files.move(Paths.get(pathOfFile+"/"+file), Paths.get("D:/Archive/"+file));
			//Files.delete(Paths.get(pathOfFile+"/"+file));
			files.remove(file);
			System.out.println("After removing Removing \n"+files);
		}
		catch(FileAlreadyExistsException e) {e.printStackTrace();return;}
		catch(IOException e) {e.printStackTrace();return;}
		catch(Exception e) {e.printStackTrace(); return;}
	}
	public static void main(String[] args) 
	{
		new Poller().poll();
	}
}