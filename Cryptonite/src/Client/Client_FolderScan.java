package Client;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;

/*
 * Developer : Youn Hee Seung
 * Date : 2016 - 07 - 22
 * 
 * Name : Folder Auto Scan
 * Description : When you select SecureFolder, then it can perceive file event(CREATE, DELETE, MODIFICATION)
 * 
 * */


public class Client_FolderScan extends Thread
{
	// WatchService Instance
	private WatchService _watchService = null;
	private WatchKey _watchKey = null;
	private Vector<String> _directoryVector = null;
	
	// Filenames, to make absolute directory, check directory
	private String _fileName = null;
	private String _address = null;
	private String _absoluteDirectory = null;
	private File _isDirectory = null;
	
	// StopFlag
	private boolean _stopFlag = false;
	
	// Methods
	public synchronized void run()
	{
		try {
			_watchService = FileSystems.getDefault().newWatchService();
			Path directory = Paths.get(_address);
		    directory.register(_watchService, StandardWatchEventKinds.ENTRY_CREATE,
		                                  StandardWatchEventKinds.ENTRY_DELETE);
		    
		    while(_stopFlag == false)
		    {
	        	_watchKey = _watchService.take();
	        	List<WatchEvent<?>> _list = _watchKey.pollEvents();
        
	        	for(WatchEvent _watchEvent : _list) 
	        	{
	        		Kind _kind = _watchEvent.kind();
	        		Path _path = (Path)_watchEvent.context();
              
	        		if(_kind == StandardWatchEventKinds.ENTRY_CREATE)
	        		{
	        			_isDirectory = new File(_address + "\\" + _path.getFileName().toString());
	        			if(_isDirectory.isDirectory() == true) 
	        			{
	        				// This is folder, write new work.
	        			}
	        			else
	        			{
	        				_fileName = _path.getFileName().toString();
	        				System.out.println("New File is Created >> " + _fileName);
	        				_absoluteDirectory = _isDirectory.getPath();
	        				_directoryVector.add(_absoluteDirectory);
	        			}
	        		}
	        		else if(_kind == StandardWatchEventKinds.ENTRY_DELETE)
	        		{
	        			for(int i = 0; i < _directoryVector.size(); i++) 
	        			{
	        				if(_path.getFileName().toString().equals(_directoryVector.get(i)))
	        				{
	        					System.out.println("File is Deleted >> " + _directoryVector.get(i));
	        					_directoryVector.remove(i);
	        					break;
	        				}
	        			}
	        		}
	        		else if(_kind == StandardWatchEventKinds.OVERFLOW) 
	        		{
	        			System.out.println("Directory is gone...");
	        			break;
	        		}
	        	}
           
	        	boolean _valid = _watchKey.reset();
	        	
	        	if(!_valid)
	        		break;
	        }
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		catch(ClosedWatchServiceException cwe)
		{
			System.out.println("WatchService Thread was closed successfully.");
			this.interrupt();
		}
	}
	
	public void stopThread()
	{
		try 
		{
			_watchService.close();
			this._stopFlag = true;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}