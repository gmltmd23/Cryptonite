package Server;

import java.util.*;

import Crypto.Base64Coder;
import Function.PacketProcessor;
import Function.SecurePacketProcessor;

import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;

/*
 * Developer : Youn Hee Seung
 * Date : 2016 - 08 - 08
 * 
 * Name : FileShare
 * Description : It can share file
 * 
 * */

public class Server_FileShare_Send extends Server_Funtion
{
	public Server_FileShare_Send(Server_Client_Activity activity) {
		super(activity);
	}

	// OTP Instance
	private String _OTP = null;
	private File _shareFolder = null;
	private String[] _fileList = null;
	private StringTokenizer _st = null;
	private boolean _temporaryFlag = false;
	private String _downloadFlag = null;
	private Server_DataBase db = null;
	private ResultSet rs = null;
	
	// File Instance
	private RandomAccessFile _raf = null;
	private String _searchedFile = "DEFAULT";
	private String _fileName = null;
	private long _fileSize = 0;
	private String hash = null;
	
	private void OTP_Check()
	{
		try
		{
			
			hash = md5(_activity.getUserId().concat(_OTP).getBytes());
			_shareFolder = new File("Server_Folder\\Share");
			_fileList = _shareFolder.list();
			
			for(int i = 0; i < _fileList.length; i++)
			{
				_st = new StringTokenizer(_fileList[i], "@");
				if(_st.nextToken().equals(hash))
				{
					_searchedFile = _fileList[i];
					_temporaryFlag = true;
					break;
				}
			}
			if(_temporaryFlag)
			{
				_downloadFlag = "TRUE";
			}
			else
			{
				_downloadFlag = "FALSE";
			}
		}
		catch (NullPointerException e)
		{
			System.out.println("There is no file.");
		}
	}

	@Override
	public void Checker(byte[] packet) 
	{
		_packetMaxCount = 1 + 1;
		_activity.receive.setAllocate(30);
		_cutSize = 1;
	}

	@Override
	public void running(int count) throws IOException 
	{
		if(count == 1) { Checker(_activity.getReceiveEvent()); }
		else
		{
			Charset cs = Charset.forName("UTF-8");
			_OTP = new String(_activity.receive.getByte()).trim();
			OTP_Check();
			_activity.send.setPacket(_downloadFlag.getBytes(),500).write();
			
			if(_temporaryFlag)
			{
				try 
				{
					File sendingFile = new File("Server_Folder\\Share\\" + _searchedFile);
					StringTokenizer st_temp = new StringTokenizer(_searchedFile, "@");
					while(st_temp.hasMoreTokens())
					{
						_fileName = st_temp.nextToken();
					}
					_fileSize =  sendingFile.length();
					
					//_activity.send.setPacket(cs.encode(_fileName).array(), 500).write();
					_activity.send.setPacket(_fileName.getBytes(), 500).write();
					
					_activity.send.setPacket(String.valueOf(_fileSize).getBytes(), 500).write();
					
					_raf = new RandomAccessFile("Server_Folder\\Share" + "\\" + _searchedFile, "rw");
					SecurePacketProcessor p = new SecurePacketProcessor(_raf.getChannel(), false);
					p.init(_activity.getFileKey());
					p.setAllocate(_fileSize);
					
					while(!p.isAllocatorEmpty())
					{
						_activity.send.setPacket(p.read().getByte()).write();
					}
					p.close();
					
					sendingFile.delete();
				}
				catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	public String md5(byte[] _password) {
		MessageDigest _md = null;
		try {
			_md = MessageDigest.getInstance("MD5");
			byte[] temp = _password;
			_md.update(temp);
			return URLEncoder.encode(new String(Base64Coder.encode(_md.digest())));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}
}
