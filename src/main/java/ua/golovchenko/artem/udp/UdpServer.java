package ua.golovchenko.artem.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

import static java.lang.Thread.sleep;

/**
 * Created by art on 16.04.2016.
 */
public class UdpServer {

    public static void main(String[] args) throws InterruptedException {

        multiServer();
    }

    private static void simple() {

        try (DatagramSocket socket = new DatagramSocket(5555)) {

            byte buffer[] = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
            String answer;

            while (true){

                //принимаем ответ
                socket.receive(packet);
                int byteRecived = packet.getLength();

                System.out.println("Recived: " + byteRecived + "bytes from" + packet.getSocketAddress().toString());
                System.out.println("Recived message: " + new String(packet.getData()) + "\n");

                InetAddress inetAddress = packet.getAddress();

                // Ответ в верхнем регистре
                answer = new String(packet.getData()).toUpperCase();
                System.out.println(new String(packet.getData()).toUpperCase());
                //packet.setData(answer.getBytes(StandardCharsets.UTF_8));

                //packet = new DatagramPacket(buffer,buffer.length, inetAddress,6666);
                packet = new DatagramPacket(answer.getBytes(StandardCharsets.UTF_8)
                        ,buffer.length
                        , inetAddress,6666);

                socket.send(packet);
            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }


    private static void multiServer() throws InterruptedException {

        try(MulticastSocket multicastSocket = new MulticastSocket(5555);){

            String msg = "Hello from ServerSide";
            InetAddress inetAddress = InetAddress.getByName("228.5.6.7");
            //InetAddress inetAddress = InetAddress.getByName("localhost");
            multicastSocket.joinGroup(inetAddress);

            DatagramPacket multiMessage = new DatagramPacket(msg.getBytes(), msg.length(),inetAddress,7777);

            boolean run = true;
            while (run){
                System.out.println("Делаем рассылку с сервера всем");
                multicastSocket.send(multiMessage);
 /*               //get response
                byte[] buf = new byte[1000];
                DatagramPacket recive = new DatagramPacket(buf,buf.length);
                multicastSocket.receive(recive);*/
                // OK, I'm done talking - leave the group...

                sleep(20000);
            }
            multicastSocket.leaveGroup(inetAddress);
        } catch (IOException e) {
            System.out.println("Не могу подключиться к сокету");
            e.printStackTrace();
        }
    }

}
