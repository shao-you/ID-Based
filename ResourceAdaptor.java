import java.sql.*;

public class ResourceAdaptor {
	  private Connection con = null; //Database objects 
	  //�s��object 
	  private Statement stat = null; 
	  //����,�ǤJ��sql�������r�� 
	  private ResultSet rs = null; 
	  //���G�� 
	  private PreparedStatement pst = null; 
	  //����,�ǤJ��sql���w�x���r��,�ݭn�ǤJ�ܼƤ����m 
	  //���Q��?�Ӱ��Х� 
	  
	  private String dropdbSQL = "DROP TABLE User "; 
	  
	  private String createdbSQL = "CREATE TABLE User (" + 
	    "    id     INTEGER " + 
	    "  , name    VARCHAR(20) " + 
	    "  , passwd  VARCHAR(20))"; 
	  
	  private String insertdbSQL = "insert into User(id,name,passwd) " + 
	      "select ifNULL(max(id),0)+1,?,? FROM User"; 
	  
	  private String selectSQL = "SELECT * FROM `match`"; 
	  
	  public ResourceAdaptor() 
	  { 
	    try { 
	      Class.forName("com.mysql.jdbc.Driver"); 
	      //���Udriver 
	      String url1 = "jdbc:mysql://dbhome.cs.nctu.edu.tw/wusy_cs_ttt?"
	    		  + "useUnicode=true&characterEncoding=Big5";
	      String url2 = "jdbc:mysql://140.113.215.4/test?"
	    		  + "useUnicode=true&characterEncoding=Big5";
	      //con = DriverManager.getConnection(url1,"wusy_cs",""); 
	      con = DriverManager.getConnection(url2,"dlink","123456"); 
	      //���oconnection

	//jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=Big5
	//localhost�O�D���W,test�Odatabase�W
	//useUnicode=true&characterEncoding=Big5�ϥΪ��s�X 
	      
	    } 
	    catch(ClassNotFoundException e) 
	    { 
	      System.out.println("DriverClassNotFound :"+e.toString()); 
	    }//���i���|����sqlexception 
	    catch(SQLException x) { 
	      System.out.println("Exception :"+x.toString()); 
	    } 
	    System.out.println("SUCCESSFUL CONNECTION!!");
	    
	  } 
	  //�إ�table���覡 
	  //�i�H�ݬ�Statement���ϥΤ覡 
	  public void createTable() 
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
	  //�s�W���� 
	  //�i�H�ݬ�PrepareStatement���ϥΤ覡 
	  public void insertTable( String name,String passwd) 
	  { 
	    try 
	    { 
	      pst = con.prepareStatement(insertdbSQL); 
	      
	      pst.setString(1, name); 
	      pst.setString(2, passwd); 
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
	  //�R��Table, 
	  //���إ�table�ܹ� 
	  public void dropTable() 
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
	  //�d�߸��� 
	  //�i�H�ݬݦ^�ǵ��G���Ψ��o���Ƥ覡 
	  public void SelectTable() 
	  { 
	    try 
	    { 
	      stat = con.createStatement(); 
	      rs = stat.executeQuery(selectSQL); 
	      System.out.println("ID\t\tName\t\tPASSWORD"); 
	      while(rs.next()) 
	      { 
	        System.out.println(rs.getInt("ID")+"\t\t"+ 
	            rs.getString("PASS")+"\t\t"+rs.getString("MAC")+"\t\t"+rs.getInt("lifetime")); 
	      }/**/
	      rs.close();
	    } 
	    catch(SQLException e) 
	    { 
	      System.out.println("SelectDB Exception :" + e.toString()); 
	    } 
	    finally 
	    { 
	      Close(); 
	    } 
	  } 
	  //�����ϥΧ����Ʈw��,�O�o�n�����Ҧ�Object 
	  //�_�h�b����Timeout��,�i���|��Connection poor�����p 
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
	  

	  public static void main(String[] args) 
	  { 
	    //���ݬݬO�_���` 
		ResourceAdaptor test = new ResourceAdaptor(); 
	    //test.dropTable(); 
	    //test.createTable(); 
	    //test.insertTable("yku", "12356"); 
	    //test.insertTable("yku2", "7890"); 
	    test.SelectTable(); 
		test.Close();
	  } 
}
