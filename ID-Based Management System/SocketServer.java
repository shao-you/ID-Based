import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SocketServer extends java.lang.Thread {
    Register register;
    Socket socket;
    
    public SocketServer(Register register, Socket socket) {
            this.register = register;
            this.socket = socket;
    }
 
    public void run() {
        	System.out.println("伺服器已啟動 !");     
        	
            try {
                System.out.println("取得連線 : InetAddress = " + socket.getInetAddress().getHostAddress());
                // TimeOut時間
                socket.setSoTimeout(15000);
 
                /*ObjectInputStream in = new ObjectInputStream(socket.getInputStream());//receive object
                Asso_record record = (Asso_record) in.readObject();//restore to JSON format, class Asso_record位置(package): 送端要等於收端
                record.Printout_record();*/
               
                /*BufferedInputStream in = new BufferedInputStream(socket.getInputStream());//receive string
                byte[] b = new byte[1024];
                String data = "";
                int length;
                while ((length = in.read(b)) > 0)// <=0的話就是結束了
                {
                    data += new String(b, 0, length);
                }
                //in.close();
                //in = null;*/
               
                DataInputStream in = new DataInputStream(socket.getInputStream());
                String data = in.readUTF();
                JSONObject record_json = new JSONObject(data);
                //System.out.println(record_json);
                //System.out.println("Client：" + data);
                Dispatcher newdispatcher = new Dispatcher(record_json, register);
                Thread newThread = new Thread(newdispatcher);
                newThread.start();
                try {
					newThread.join();//will wait for the upper layer to complete
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                /*BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());System.out.println("=====================================================");
                // 送出字串
                String msg = "Action complete!!";
                out.write(msg.getBytes());
                out.flush();
                //out.close();
                //out = null;*/
                
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                String result = newdispatcher.forwarding_or_not().toString();
                System.out.println("Forwarding or not: "+result);
				out.writeUTF(result);
				out.flush();

                //String record_of_Association_ID = "SELECT * FROM `Association` where `Association_ID`='"+ data +"'";
                //ResourceAdaptor DB_manipulate = new ResourceAdaptor();
                //Object [] result = DB_manipulate.SelectTable(record_of_Association_ID);  
                
                //do some simple handling of IDs (notification, classification, register/publish...)

                socket.close();
                socket = null;
                
            } catch (java.io.IOException e) {
                System.out.println("Socket連線有問題 !");
                System.out.println("IOException :" + e.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
 
    /*public static void main(String args[]) {
        (new SocketServer()).start();
    }*/
 
}
