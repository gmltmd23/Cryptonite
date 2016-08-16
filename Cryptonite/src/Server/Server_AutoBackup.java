package Server;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import Function.*;

/*
 * Developer : Youn Hee Seung
 * Date : 2016 - 07 - 29
 * 
 * Name : AutoBackup System
 * Description : It can backup encrypted files
 * 
 * */

public class Server_AutoBackup extends Server_Funtion implements PacketRule
{
	public Server_AutoBackup(Server_Client_Activity activity) {
		super(activity);
		// TODO 자동 생성된 생성자 스텁
	}

	// protectedFolder
	private String _address = "Server_Folder\\Backup\\";
	private File _protectedFolder;
	
	// Instance
	private int _count = 1;
	
	// About File
	private String _checkProperty = null;
	private File _downloadFile = null;
	private String _fileName = null;
	private long _fileSize = 0;
	
	private RandomAccessFile _raf = null;
	private FileChannel _fileChannel = null;
	private PacketProcessor p = null;
	

	private void setFolder()
	{
		_address = _address + _activity.getClientCode();
		_protectedFolder = new File(_address);
		if(!_protectedFolder.exists())
		{
			_protectedFolder.mkdirs();
		}
	}
	
	// Methods
	private int sendPacketSize(long fileSize)
	{
		int remainder = (int)(fileSize / FILE_BUFFER_SIZE);
		if((fileSize % FILE_BUFFER_SIZE) > 0)
		{
			remainder++;
		}
		
		return remainder;
	}
	
	private void setFileInformation(byte[] packet)
	{
		_checkProperty = "FILE";
		
		int end = 0;
		
		byte[] sizeTemp = new byte[packet[2]];
		for(int i = 0; i < sizeTemp.length; i++)
		{
			sizeTemp[i] = packet[i + 4];
			end = i+4;
		}
		_fileSize = Long.parseLong(new String(sizeTemp).trim());
		
		int max = end;
		while(packet[max] != 0)
		{
			max++;
		}
		
		byte[] nameTemp = new byte[packet[3]];
		for(int i = 0; i < nameTemp.length; i++)
		{
			nameTemp[i] = packet[i + end + 1];
		}
		_fileName = new String(nameTemp).trim();
		
		System.out.println("파일 이름 : " + _fileName);
		System.out.println("파일 용량 : " + _fileSize + " (Byte)");
	}
	
	@Override
	public void Checker(byte[] packet) 
	{
		setFolder();
		if(packet[1] == DIRECTORY)
		{
			_checkProperty = "DIRECTORY";
			_packetMaxCount = 1 + 1;
		}
		else if(packet[1] == FILE)
		{
			setFileInformation(packet);
			_packetMaxCount = 1 + 1 + sendPacketSize(_fileSize);
			
			/*try 
			{
				_raf = new RandomAccessFile(_address + "\\" + _fileName, "rw");
				_fileChannel = _raf.getChannel();
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}

			p = new PacketProcessor(_fileChannel, false);
			_activity.receive.setAllocate(_fileSize);*/
		}
	}

	@Override
	public void running(int count) throws IOException 
	{
		if(count == 1) 
		{ 
			Checker(_activity.getReceiveEvent());
			_activity.send.setPacket(_address.getBytes(), 500).write();
			_activity.receive.setAllocate(1024);
		}
		else if(count == 2)
		{
			if(_checkProperty.equals("DIRECTORY"))
			{
				_address = new String(_activity.receive.getByte()).trim();
				System.out.println("어드레스 : " + _address);
				File newFolder = new File(_address);
				newFolder.mkdir();
				System.out.println("AUTOBACKUP COMPLETE !!");
			}
			else if(_checkProperty.equals("FILE"))
			{	
				_address = new String(_activity.receive.getByte()).trim();
				System.out.println("어드레스 : " + _address);
				try 
				{
					_raf = new RandomAccessFile(_address + "\\" + _fileName, "rw");
					_fileChannel = _raf.getChannel();
				} 
				catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				}

				p = new PacketProcessor(_fileChannel, false);
				_activity.receive.setAllocate(_fileSize);
			}
		}
		else
		{
			if(_checkProperty.equals("FILE"))
			{
				p.setPacket(_activity.receive.getByte()).write();
				
				if(count == _packetMaxCount)
				{
					System.out.println("AUTOBACKUP COMPLETE !!");
					p.close();
					count = 1;
				}
			}
		}
	}
}