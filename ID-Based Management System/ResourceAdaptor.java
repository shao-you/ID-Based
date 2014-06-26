import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class ResourceAdaptor {
	  private Connection con = null; //Database objects 
	  //連接object 
	  private Statement stat = null; 
	  //執行,傳入之sql為完整字串 
	  private ResultSet rs = null; 
	  //結果集 
	  private PreparedStatement pst = null; 
	  //執行,傳入之sql為預儲之字申,需要傳入變數之位置 
	  //先利用?來做標示 

	  private String dropdbSQL = "DROP TABLE Association";

	  private String createdbSQL = "CREATE TABLE User (" + 
	    "    id     INTEGER " + 
	    "  , name    VARCHAR(20) " + 
	    "  , passwd  VARCHAR(20))"; 

	  //private String insertdbSQL = "insert into User(id,name,passwd) select ifNULL(max(id),0)+1,?,? FROM User"; 
	  private String insertdb_Association = "insert into `Association` (`Association_ID`, `uid`, `in_port`, `sw_dpid`, `src_mac`, `dst_mac`, "+
	  " `src_ip`, `dst_ip`, `src_port`, `dst_port`, `protocol`, `time`) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";//12 fields
	  
	  private String insert_Registered_mac = "INSERT INTO `Registered_mac` (`src_mac`, `time`, `idle`, `pass`) VALUES ('10:00:00:00:00:04', CURRENT_TIMESTAMP, '1', '0')";
	  
	  private String selectSQL = "SELECT * FROM `Association`"; 
	  private String latest_record = "SELECT `uid` FROM `Association` where `src_mac`='10:00:00:00:00:03' ORDER BY `time` DESC LIMIT 1";
	  
	  public ResourceAdaptor() 
	  {
	    try { 
	      Class.forName("com.mysql.jdbc.Driver"); 
	      //註冊driver 
	      //String url1 = "jdbc:mysql://dbhome.cs.nctu.edu.tw/wusy_cs_ttt?useUnicode=true&characterEncoding=Big5";
	      String url2 = "jdbc:mysql://140.113.215.4/test?useUnicode=true&characterEncoding=Big5";
	      //con = DriverManager.getConnection(url1,"wusy_cs","*****"); 
	      con = DriverManager.getConnection(url2,"dlink","123456"); 
	      //取得connection

	//jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=Big5
	//localhost是主機名,test是database名
	//useUnicode=true&characterEncoding=Big5使用的編碼 

	    } 
	    catch(ClassNotFoundException e) 
	    { 
	      System.out.println("DriverClassNotFound :"+e.toString()); 
	    }//有可能會產生sqlexception 
	    catch(SQLException x) { 
	      System.out.println("Exception :"+x.toString()); 
	    } 
	    System.out.println("SUCCESSFUL CONNECTION!!");

	  } 
	  //建立table的方式 
	  //可以看看Statement的使用方式 
	  public void createTable()//no use 
	  { 
	    try 
	    { 
	      stat = con.createStatement(); 
	      stat.executeUpdate(createdbSQL); 
	    } 
	    catch(SQLException e) 
	    { 
	      System.out.println("CreateDB Exception :" + e.toString()); 
	    } 
	    finally 
	    { 
	      Close(); 
	    } 
	  } 
	  //新增資料 
	  //可以看看PrepareStatement的使用方式 
	  public void insertTable(int Association_ID, String uid, short in_port, long sw_dpid, String src_mac, String dst_mac, String src_ip, String dst_ip, 
			  short src_port, short dst_port, byte protocol, java.sql.Timestamp time) 
	  { 
	    try 
	    { 
	      pst = con.prepareStatement(insertdb_Association); 
	      pst.setInt(1, Association_ID);
	      pst.setString(2, uid);
	      pst.setShort(3, in_port);
	      pst.setLong(4, sw_dpid);
	      pst.setString(5, src_mac);
	      pst.setString(6, dst_mac);
	      pst.setString(7, src_ip);
	      pst.setString(8, dst_ip);
	      pst.setShort(9, src_port);
	      pst.setShort(10, dst_port);
	      pst.setByte(11, protocol);
	      pst.setTimestamp(12, time);
	      
	      pst.executeUpdate(); 
	    } 
	    catch(SQLException e) 
	    { 
	      System.out.println("InsertDB Exception :" + e.toString()); 
	    } 
	    finally 
	    { 
	      Close(); 
	    } 
	  } 
	  //刪除Table, 
	  //跟建立table很像 
	  private void dropTable()//dangerous 
	  { 
	    try 
	    { 
	      stat = con.createStatement(); 
	      stat.executeUpdate(dropdbSQL); 
	    } 
	    catch(SQLException e) 
	    { 
	      System.out.println("DropDB Exception :" + e.toString()); 
	    } 
	    finally 
	    { 
	      Close(); 
	    } 
	  } 
	  //查詢資料 
	  //可以看看回傳結果集及取得資料方式 
	  public Object [] SelectTable(String command)//only query a record, return array of object
	  { 
		  Object [] meatadata = new Object[12];
		try 
	    { 
	      stat = con.createStatement(); 
	      rs = stat.executeQuery(command); 
    	  System.out.println(command+": ");
	      System.out.println("Association_ID     uid     in_port     sw_dpid     src_mac     dst_mac     src_ip     dst_ip     src_port     dst_port     protocol     time");
	      
	      //ResultSetMetaData rsmd = rs.getMetaData();
	      //int columnsNumber = rsmd.getColumnCount();
	      while(rs.next())
	      { 
	    	  /*for (int i = 1; i <= columnsNumber; i++)
	    	  {
	    		  String columnValue = rs.getString(i);
	              System.out.println(columnValue + " " + rsmd.getColumnName(i));
	    	  }*/
	    	  meatadata[0] = rs.getInt("Association_ID");
	    	  meatadata[1] = rs.getString("uid");
	    	  meatadata[2] = rs.getShort("in_port");
	    	  meatadata[3] = rs.getLong("sw_dpid");
	    	  meatadata[4] = rs.getString("src_mac");
	    	  meatadata[5] = rs.getString("dst_mac");
	    	  meatadata[6] = rs.getString("src_ip");
	    	  meatadata[7] = rs.getString("dst_ip");
	    	  meatadata[8] = rs.getShort("src_port");
	    	  meatadata[9] = rs.getShort("dst_port");
	    	  meatadata[10] = rs.getByte("protocol");
	    	  meatadata[11] = rs.getTimestamp("time");
	    	  //System.out.println(rs.getInt("Association_ID")+"\t"+rs.getString("uid")+"\t"+rs.getShort("in_port")
	    			  //+"\t"+rs.getLong("sw_dpid")+"\t"+rs.getString("src_mac")+"\t"+rs.getString("dst_mac")
	    			  //+"\t"+rs.getString("src_ip")+"\t"+rs.getString("dst_ip")+"\t"+rs.getShort("src_port")+"\t"+rs.getShort("dst_port")
	    			  //+"\t"+rs.getByte("protocol")+"\t"+rs.getTimestamp("time"));
	      }
	    } 
	    catch(SQLException e) 
	    { 
	      System.out.println("SelectDB Exception :" + e.toString()); 
	    } 
	    finally 
	    { 
	      Close(); 
	    } 
		return meatadata;
	  } 
	  //完整使用完資料庫後,記得要關閉所有Object 
	  //否則在等待Timeout時,可能會有Connection poor的狀況 
	  private void Close() 
	  { 
	    try 
	    { 
	      if(rs!=null) 
	      { 
	        rs.close(); 
	        rs = null; 
	      } 
	      if(stat!=null) 
	      { 
	        stat.close(); 
	        stat = null; 
	      } 
	      if(pst!=null) 
	      { 
	        pst.close(); 
	        pst = null; 
	      } 
	    } 
	    catch(SQLException e) 
	    { 
	      System.out.println("Close Exception :" + e.toString()); 
	    } 
	  } 

	/*public static void main(String[] args) 
	  { 
	      //測看看是否正常 
		  java.util.Date date = new java.util.Date();
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
		  test.SelectTable(selectSQL);
		  //test.insertTable((int)7788, "Alex", (short)3, (long)15, "10:00:00:00:00:02", "10:00:00:00:00:03", "140.113.215.4", "8.8.8.8", (short)17, (short)6, (byte)4, sqlTimestamp);
	  }*/ 
}
