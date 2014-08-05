import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {//main function of ID-Based Management System
	
	public static void main(String[] args) throws SQLException, IOException {
		  int number_of_policy_engine = 1;
		  int record_field_num = 12;
		  Register register = new Register(number_of_policy_engine, record_field_num);
		  //Dispatcher dispatcher = new Dispatcher();
		  Location_time_aware p1 = new Location_time_aware();//register to dispatcher
		  p1.register_IDs(register);
		  //p2, p3...
		  
		  final int ServerPort = 8765;
		  ServerSocket server = new ServerSocket(ServerPort);
		  
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
		  
    	  //測看看是否正常 
		  /*java.util.Date date = new java.util.Date();
	      long t = date.getTime();
	      //java.sql.Date sqlDate = new java.sql.Date(t);
	      //java.sql.Time sqlTime = new java.sql.Time(t);
	      java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);
	      
	      //System.out.println("sqlDate=" + sqlDate);
	      //System.out.println("sqlTime=" + sqlTime);
	      System.out.println("sqlTimestamp=" + sqlTimestamp);
		  //MySQL 的 timestamp 型態 的屬性比較特別, 預設 timestamp 的屬性是, 只要有值 新增/修改(同一個row), MySQL 會自動幫你將 "timestamp 型態的欄位" 寫入現在時間.

	      ResourceAdaptor test = new ResourceAdaptor(); 
		  //test.dropTable();
		  //test.createTable();//no use
	      String latest_record = "SELECT * FROM `Association` where `src_mac`='10:00:00:00:00:03' ORDER BY `time` DESC LIMIT 1";
	      Object [] result = test.SelectTable(latest_record);
	      int columnsNumber = result.length;
	    	  for (int i = 0; i < columnsNumber; i++)
	    	  {
	    		  //String columnValue = rs.getString(i);
	              System.out.print(result[i]+"  ");
	    	  }*/
		  //test.insertTable((int)7788, "Alex", (short)3, (long)15, "10:00:00:00:00:02", "10:00:00:00:00:03", "140.113.215.4", "8.8.8.8", (short)17, (short)6, (byte)4, sqlTimestamp);

        /*Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.print("輸入名稱：");
            String name = scanner.next();

            System.out.print("輸入密碼： ");
            // 啟動 Eraser 執行緒
            EraserThread eraserThread = new EraserThread('#');
            eraserThread.start();
            String password = scanner.next();
            eraserThread.setActive(false);

            if("caterpillar".equals(name) &&
               "123456".equals(password)) {
                System.out.println("歡迎 caterpillar ");
                break;
            }
            else {
                System.out.printf("%s，名稱或密碼錯誤，請重新輸入！%n", name);
            }
        }*/

    }
}
