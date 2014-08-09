import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Main {//main function of ID-Based Management System
	
	public static void main(String[] args) throws SQLException, IOException {
		  int number_of_policy_engine = 1;
		  int record_field_num = 12;
		  Register register = new Register(number_of_policy_engine, record_field_num);

		  Location_time_aware p1 = new Location_time_aware();
		  p1.register_IDs(register);//register to dispatcher
		  //p2, p3...
		  
		  final int ServerPort = 8765;
		  ServerSocket server = new ServerSocket(ServerPort);
		  System.out.println("*** Welcome to the ID-Based Management System ***");
		  
		  while (true) {
			  Socket s=null;
			  synchronized (server) {
				  s = server.accept();
              }
	            Thread newServer = new SocketServer(register, s);
	            newServer.start();//thread-enabled server
	            
	            /*try {
					newServer.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
	       }
		  //server.close();//unreachable

		  /*java.util.Date date = new java.util.Date();
	      long t = date.getTime();
	      //java.sql.Date sqlDate = new java.sql.Date(t);
	      //java.sql.Time sqlTime = new java.sql.Time(t);
	      java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);
	      
	      //System.out.println("sqlDate=" + sqlDate);
	      //System.out.println("sqlTime=" + sqlTime);
	      System.out.println("sqlTimestamp=" + sqlTimestamp);
		  //MySQL 的 timestamp 型態 的屬性比較特別, 預設 timestamp 的屬性是, 只要有值 新增/修改(同一個row), MySQL 會自動幫你將 "timestamp 型態的欄位" 寫入現在時間.*/

    }
}
