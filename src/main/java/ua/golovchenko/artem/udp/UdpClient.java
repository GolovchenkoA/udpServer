package ua.golovchenko.artem.udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by art on 16.04.2016.
 */
public class UdpClient {

    public static void main(String[] args) {
        multiClient();
    }

    private static void simple(){

        try(DatagramSocket socket = new DatagramSocket(6666);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))){

            String line;
            byte[] buffer;

        while (true){

            line = stdIn.readLine();
            buffer = line.getBytes();

            //InetAddress inetAddress = InetAddress.getByName("localhost"); //for simple server
            InetAddress inetAddress = InetAddress.getByName("228.5.6.7"); //for multi server
            DatagramPacket packet = new DatagramPacket(buffer,buffer.length,inetAddress, 5555);


            socket.send(packet);


            socket.receive(packet);
            int bytesRecived = packet.getLength();

            System.out.println("Recived: " + bytesRecived + " from " + packet.getSocketAddress().toString());
            System.out.println("Recived Data: " + new String(packet.getData()) + "\n");
        }

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }


    private static void multiClient(){

        try(MulticastSocket socket = new MulticastSocket(7777);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))){

            String line;

            InetAddress inetAddress = InetAddress.getByName("228.5.6.7");
            socket.joinGroup(inetAddress);


            // Отдельный поток на прослушивание входящих данных
            Thread t = new Thread(() ->{
                //boolean run = true;
                try {
                    byte[] buffer = new byte[256];
                    //while (stdIn != null){
                    while (true) {

                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                        socket.receive(packet);
                        int byteRecived = packet.getLength();

                        System.out.println("Recived: " + byteRecived + "From: " + packet.getSocketAddress());
                        System.out.println("Принятые данные: " + new String(packet.getData(),0,packet.getLength()));

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

            t.setDaemon(true);
            t.start();

            while (true){
                line = stdIn.readLine();
                byte[] buffer = line.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer,buffer.length,inetAddress,7777);
                socket.send(packet);
                if ("exit".equals(line)) break;
            }

            socket.leaveGroup(inetAddress);

        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    }
