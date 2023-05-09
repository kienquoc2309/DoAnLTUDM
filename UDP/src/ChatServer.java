import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer extends Thread {
	//Tạo port 
    public final static int PORT = 7331;
    
    private final static int BUFFER = 1024;
    //tạo DatagramSocket để gửi hoạt nhận gói tin
    private DatagramSocket socket;
    //tạo mảng để lưu các địa chỉ gửi gói tin
    private ArrayList<InetAddress> clientAddresses;
    //Tạo mảng để lưu port gửi gói tin
    private ArrayList<Integer> clientPorts;
    //Tạo hàm băm để lưu id client
    private HashSet<String> existingClients;
    //Tạo constructor
    public ChatServer() throws IOException {
        socket = new DatagramSocket(PORT);
        clientAddresses = new ArrayList();
        clientPorts = new ArrayList();
        existingClients = new HashSet();
    }
    //tạo luồng để gửi hoặc nhận gói tin
    public void run() {
    	//Tạo chuỗi byte để nhận gói tin
        byte[] buf = new byte[BUFFER];
        while (true) {
            try {
            	//điền giá trị của một phần tử được chỉ định vào tất cả các phần tử của một mảng.
                Arrays.fill(buf, (byte)0);
                //tạo DatagramPacket để nhận gói tin
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                //nhận gói tin
                socket.receive(packet);
                
                String content = new String(buf, buf.length);
                //tạo biến lưu địa chỉ gói tin gửi về
                InetAddress clientAddress = packet.getAddress();
                //lưu port
                int clientPort = packet.getPort();
                //lưu địa chỉ và port để nhận biết từng client
                String id = clientAddress.toString() + "," + clientPort;
                //kiểm tra có client nào trùng lặp hay không
                if (!existingClients.contains(id)) {
                	//thêm id của client mới
                    existingClients.add( id );
                  //thêm port của client mới
                    clientPorts.add( clientPort );
                  //thêm địa chỉ của client mới
                    clientAddresses.add(clientAddress);
                }
                //hiển thị ra màn hình
                System.out.println(id + " : " + content);
                //tạp mảng data để gửi tin nhắn nhận được cho các client đang kết nối
                byte[] data = (id + " : " +  content).getBytes();
                for (int i=0; i < clientAddresses.size(); i++) {
                    InetAddress cl = clientAddresses.get(i);
                    int cp = clientPorts.get(i);
                    packet = new DatagramPacket(data, data.length, cl, cp);
                    socket.send(packet);
                }
            } catch(Exception e) {
                System.err.println(e);
            }
        }
    }
    
    public static void main(String args[]) throws Exception {
    	//tạo luồng
        ChatServer s = new ChatServer();
        //bắt đầu chạy luồng
        s.start();
    }
}