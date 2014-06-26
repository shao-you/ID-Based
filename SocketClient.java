package net.floodlightcontroller.headerextract;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.BufferedOutputStream;
 
public class SocketClient {
    private String address = "140.113.215.4";// 連線的ip
    private int port = 8765;// 連線的port
 
    public SocketClient() {
    	this(99999);
    }
    public SocketClient(int Association_ID) {
 
        Socket client = new Socket();
        InetSocketAddress isa = new InetSocketAddress(this.address, this.port);
        try {
            client.connect(isa, 10000);
            BufferedOutputStream out = new BufferedOutputStream(client
                    .getOutputStream());
            // 送出字串
            String msg = Integer.toString(Association_ID);
            out.write(msg.getBytes());
            out.flush();
            out.close();
            out = null;
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
