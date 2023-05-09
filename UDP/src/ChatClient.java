import java.io.*;
import java.net.*;
import java.util.*;

//tạo một class để gửi tin
class MessageSender implements Runnable {
    //Tao datagramsocket de gui goi tin
    private DatagramSocket sock;
    //tao host hostname
    private String hostname;
    //tạo constructor 
    MessageSender(DatagramSocket s, String h) {
        sock = s;
        hostname = h;
    }
    //tạo hàm gửi 
    private void sendMessage(String s) throws Exception {
    	//tạo chuỗi byte để gửi dữ liệu s 
        byte buf[] = s.getBytes();
        //Tạo địa chỉ của máy 
        InetAddress address = InetAddress.getByName(hostname);
        //tạo packet để gửi 
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 7331);
       //gửi gói tin vừa tạo
        sock.send(packet);
    }
    //sử dụng hàm run để tạo luồng và thực thi riêng biệt
    public void run() {
    	//kiểm tra có kết nối hay không
        boolean connected = false;
        do {
            try {
            	//gửi gói tin thông báo đã kết nói máy chủ
                sendMessage("GREETINGS");
                connected = true;
            } catch (Exception e) {
                
            }
        } while (!connected);
        //Tạo biến để nhập từ bàn phím
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
            	//nếu biến in không dc nhập thì tiểu trình chờ mỗi 100 mili giây
                while (!in.ready()) {
                    Thread.sleep(100);
                }
                //gửi gói tin
                sendMessage(in.readLine());
            } catch(Exception e) {
            	//nếu có lỗi thì thông báo
                System.err.println(e);
            }
        }
    }
}

//tạo class khi nhận gói tin
class MessageReceiver implements Runnable {
	//tạo DatagramSocket đẻ nhận gói tin
    DatagramSocket sock;
    //tạo mảng byte để truyền dữ liệu vào
    byte buf[];
    MessageReceiver(DatagramSocket s) {
        sock = s;
        buf = new byte[1024];
    }
  //sử dụng hàm run để tạo luồng và thực thi riêng biệt
    public void run() {
        while (true) {
            try {
            	//tạo DatagramPacket để nhận thông tin bắt được
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                //gói tin nhận được sẽ được gán vào packet
                sock.receive(packet);
                //lưu dữ liệu bắt được vào chuỗi
                String received = new String(packet.getData(), 0, packet.getLength());
                //hiển thị chuỗi
                System.out.println(received);
            } catch(Exception e) {
                System.err.println(e);
            }
        }
    }
}
public class ChatClient {
	public static void main(String args[]) throws Exception {
    	//tạo địa chỉ
        String host = "127.0.0.1";
        //tạo DatagramSocket đẻ gửi gói tin
        DatagramSocket socket = new DatagramSocket();
        //khởi tạo biến gửi và nhận
        MessageReceiver r = new MessageReceiver(socket);
        MessageSender s = new MessageSender(socket, host);      
       //tạo tiểu trình gửi và nhận gói
        Thread rt = new Thread(r);
        Thread st = new Thread(s);
        //bắt đầu tiểu trình
        rt.start(); st.start();
    }
}
