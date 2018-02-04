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
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
				  System.out.println("Found a File :: "+listOfFiles[i].getName());
				  if(listOfFiles[i].getName().contains(".csv"))
				  {
					  System.out.println("Sending a File :: "+listOfFiles[i].getName());
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
					if(flag==1)
					{
						pollExisting();
						flag = 0;
					}
					System.out.println("Existing : "+files);
					WatchEvent.Kind<?> kind = event.kind();//get the kind of change
					eventPath = (Path)event.context();//get the path of change
					//System.out.println(eventDir + " : " + kind + " : "+eventPath);//display
					String fileName = eventPath.toString();
					String fileExtension = eventPath.toString().substring(fileName.indexOf('.'), fileName.length());
					if((kind.equals(StandardWatchEventKinds.ENTRY_CREATE) && fileExtension.equals(".csv")))
					{					
						files.add(eventPath.toString());
						status = csvToOracle.csvToTable(pathOfFile+"/"+eventPath.toString());
						TimeUnit.SECONDS.sleep(30);
					}
					
				}
 				//System.out.println("------\n"+files);
				if(status)
				{
					new java.util.Timer().schedule( 				
				        new java.util.TimerTask() {
				            @Override
				            public void run() {
				                moveToArchive();
				            }
				        }, 1000 );
				}
				else
				{
					System.out.println("DELETE : "+pathOfFile+eventPath.toString());
					Files.delete(Paths.get(pathOfFile+"/"+eventPath.toString()));
					files.remove(eventPath.toString());
				}
				//System.out.println("Come back!!!!!");
			}while(watchKey.reset());//to clear things can also be written as while true
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	public void moveToArchive()
	{
		Iterator<String> it = files.iterator();
		while(it.hasNext())
		{  
			
			String file = it.next();
			//System.out.println("------>"+file);
			try
			{
				TimeUnit.SECONDS.sleep(30);
				Files.move(Paths.get(pathOfFile+"/"+file), Paths.get("D:/Archive/"+file+it.hashCode()));
				Files.delete(Paths.get(pathOfFile+"/"+file));
				files.remove(file);
			}
			catch(FileAlreadyExistsException e) {return;}
			catch(IOException e) {return;}
			catch(Exception e) {;return;}
		}
	}
	public static void main(String[] args) 
	{
		new Poller().poll();
	}
}