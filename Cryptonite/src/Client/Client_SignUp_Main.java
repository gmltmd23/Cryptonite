package Client;

import java.io.IOException;

import Server.Server_DataBase;

public class Client_SignUp_Main {
	public static void main(String[ ] args)throws InterruptedException, IOException{
		Server_DataBase db;
		db=Server_DataBase.getInstance();
		db.Init_DB("com.mysql.jdbc.Driver", "jdbc.mysql://127.0.0.1:3306/"+"cryptonite", "root", "yangmalalice3349!");
		db.connect();
		db.Update("INSERT INTO USER_INFORMATION VALUES('MJ','alfzl1','yangmalalice3349','alfzl1@naver.com')");
	}
}
