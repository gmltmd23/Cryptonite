package Client;

import java.io.IOException;


/**
 * @author user
 *
 */
public class Client_Main
{
	public static void main(String[] args)
	{
			Client_Server_Connecter  ccs;
			try {
				ccs = new Client_Server_Connecter (4444);
				 ccs.SendByte(null, 0);
			} catch (InterruptedException e) {
				// TODO 자동 생성된 catch 블록
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 자동 생성된 catch 블록
				e.printStackTrace();
			}
	       
	}
}
