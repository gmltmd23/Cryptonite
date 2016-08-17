package Server;

import java.io.IOException;

/*
 * @author In Jae Lee
 * 
 */

public class Server_ImDie extends Server_Funtion
{

	public Server_ImDie(Server_Client_Activity activity) {
		super(activity);
	}

	@Override
	public void Checker(byte[] packet) 
	{

	}

	@Override
	public void running(int count) throws IOException 
	{
		Checker(_activity.getReceiveEvent()); 
		Server_Client_Manager.getInstance().stopManaging(_activity.getClientCode());
	}
}
