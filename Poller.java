package clearing.feed;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.WatchKey;
public class Poller 
{
	public static void main(String[] args) 
	{
		//WatchService lets u watch the directory continuously
		try(WatchService service = FileSystems.getDefault().newWatchService())
		//try with resources syntax bcz of this u don't need to explicitly write close method
		//and then ref to current file system
		{
			Map<WatchKey, Path> keyMap = new HashMap<>();//to store
			Path path = Paths.get("D:\\abc");
			keyMap.put(path.register(service, 
					StandardWatchEventKinds.ENTRY_CREATE, 
					StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY),
					path);
			//getting a path object and detail abt watch wat and store it in hashmap
			System.out.println("POLLER");
			
			WatchKey watchKey;
			do
			{
				watchKey = service.take();//get changes
				Path eventDir = keyMap.get(watchKey);//see if it is in hashmap and get path from there
				for(WatchEvent<?> event : watchKey.pollEvents())
				{
					WatchEvent.Kind<?> kind = event.kind();//get the kind of change
					Path eventPath = (Path)event.context();//get the path of change
					System.out.println(eventDir + " : " + kind + " : "+eventPath);//display
				}
			}while(watchKey.reset());//to clear things can also be written as while true
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}