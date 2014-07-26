package net.floodlightcontroller.headerextract;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SocketClient {
    private String address = "140.113.215.4";// 連線的ip
    private int port = 8765;// 連線的port
    private Socket client;
    private InetSocketAddress isa;
    private Boolean forwarding_or_not = true;
 
    public SocketClient() {
    	client  = new Socket();
    	isa = new InetSocketAddress(this.address, this.port);
    }
    public Boolean forwarding_or_not()
    {
    	return forwarding_or_not;
    }
    public void connect_server(JSONObject record) {

        try {
        	client.connect(isa, 10000);
        	
        	/*client.connect(isa,10000);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            // 送出object
            out.writeObject(record);
            out.flush();
            out.close();
            out = null ;
            client.close();
            client = null ;*/
            
            /*BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
            // 送出字串
            String msg = record.toString();
            out.write(msg.getBytes());
            out.flush();
            //out.close();
            //out = null;*/
        	
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF(record.toString());
			out.flush();
			
           /*BufferedInputStream in = new BufferedInputStream(client.getInputStream());
           // 接收字串
            byte[] b = new byte[1024];
            String data = "";
            int length;
            while ((length = in.read(b)) > 0)// <=0的話就是結束了
            {
                data += new String(b, 0, length);
            }
            System.out.println(data);//"Action complete!!"
            //in.close();
            //in = null;*/
            
            DataInputStream in = new DataInputStream(client.getInputStream());
            
            String result = in.readUTF(); 
            if(result.equals("true")) forwarding_or_not = true;
            else forwarding_or_not = false;
            
            System.out.println("Server："+result);
            
            client.close();
            client = null;
 
        } catch (java.io.IOException e) {
            System.out.println("Socket連線有問題 !");
            System.out.println("IOException :" + e.toString());
        }
    }
 
   /* public static void main(String args[]) {
        new SocketClient(1114555666);
    }*/
}
