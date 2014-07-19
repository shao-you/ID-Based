import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SocketServer extends java.lang.Thread {
 
    private boolean OutServer = false;
    private ServerSocket server;
    private final int ServerPort = 8765;// 要監控的port
    Dispatcher dispatcher;
    
    public SocketServer(Dispatcher dispatcher) {
        try {
            server = new ServerSocket(ServerPort);
            this.dispatcher = dispatcher;
 
        } catch (java.io.IOException e) {
            System.out.println("Socket啟動有問題 !");
            System.out.println("IOException :" + e.toString());
        }
    }
 
    public void run() {
        Socket socket;
        //java.io.ObjectInputStream in ;//receive object
        java.io.BufferedInputStream in;//receive string
 
        System.out.println("伺服器已啟動 !");
        while (!OutServer) {
            socket = null;
            try {
                synchronized (server) {
                    socket = server.accept();
                }
                System.out.println("取得連線 : InetAddress = "
                        + socket.getInetAddress().getHostAddress());
                // TimeOut時間
                socket.setSoTimeout(15000);
 
                /*in = new java.io.ObjectInputStream(socket.getInputStream());
                Asso_record record = (Asso_record) in.readObject();//restore to JSON format, class Asso_record位置(package): 送端要等於收端
                record.Printout_record();*/
                
                in = new java.io.BufferedInputStream(socket.getInputStream());
                byte[] b = new byte[1024];
                
                String data = "";
                int length;
                while ((length = in.read(b)) > 0)// <=0的話就是結束了
                {
                    data += new String(b, 0, length);
                }
                JSONObject record_json = new JSONObject(data);
                System.out.println(record_json);
                //System.out.println("我取得的值:" + data);
                this.dispatcher.trigger(record_json);
                
                //String record_of_Association_ID = "SELECT * FROM `Association` where `Association_ID`='"+ data +"'";
                //ResourceAdaptor DB_manipulate = new ResourceAdaptor();
                //Object [] result = DB_manipulate.SelectTable(record_of_Association_ID);
                
                
                //do some simple handling of IDs (notification, classification, register/publish...)
                
                in.close();
                in = null;
                socket.close();
 
            } catch (java.io.IOException e) {
                System.out.println("Socket連線有問題 !");
                System.out.println("IOException :" + e.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 
        }
    }
 
    /*public static void main(String args[]) {
        (new SocketServer()).start();
    }*/
 
}
