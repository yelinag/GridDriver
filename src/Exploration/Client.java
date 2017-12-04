package Exploration;

import java.io.*;
import java.net.*;


public class Client
{
   private Socket socket = null;
   private BufferedReader reader = null;
   private BufferedWriter writer = null;

   public Client(InetAddress host, int port) throws IOException
   {
      socket = new Socket(host, port);
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
   }

   public void send(String msg) throws IOException
   {
      writer.write(msg, 0, msg.length());
      writer.flush();
   }

   public String recv() throws IOException
   {
      return reader.readLine();
   }

   public static void main(String[] args)
   {
      try {
    	  InetAddress host = InetAddress.getByName("192.168.20.20");
    	  //String serverName = "192.168.7.7";
          int port = 5000;
          Client client = new Client(host, port);

         client.send("Hello server123.\n");
         
         String response = client.recv();
         System.out.println("Client received: " + response +" for real!!" );
         
         while(true){
        	 String msg = client.recv();
        	 //if(msg != null)
        		 System.out.println("Msg is =>" + msg);
         }
         
      }
      catch (IOException e) {
         System.out.println("Caught Exception: " + e.toString());
      }
   }
   
}