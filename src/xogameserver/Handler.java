
package xogameserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

class Pair {
    InetAddress player1;
    InetAddress player2;  
}

public class Handler {
    DataInputStream loginData;
    PrintStream dos;
    Socket mySocket;
    static Vector<Handler> clinetsVector=new Vector<Handler>();
    static Vector <Pair> players= new Vector<>();
    
    
    public Handler(Socket s) {
        mySocket=s;
        try {
            loginData= new DataInputStream(s.getInputStream());
            dos=new PrintStream(s.getOutputStream());
            Handler.clinetsVector.add(this);
            
        } catch (IOException ex) {
            Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        new Thread(){
          public void run(){
       while(true){
           try {
               String str= loginData.readLine();
               String arr[]=str.split("*");
               
               if(arr[0].equals("1")){
                   System.out.println("userName is "+arr[1] +" password "+arr[2]);
                   //String userName=arr[1];
                   //String password=arr[2];
                   //deal with DataBase Class
                   //if 0 succesfully login
                   dos.print("0");
               }
               else if(arr[0].equals("2")){
                //deal with DataBase Class
                // if 0 succesfully rigester
                dos.print("0");
               }
               else if(arr[0].equals("3")){
                   
                   Handler player = new Handler(mySocket);
                   String answer=new String();
                   for (Handler h :clinetsVector ){
                   if(h.mySocket.getInetAddress().toString()==arr[1])
                    {  
                        player=h;
                                  
                    }
                        
                    }
                    // if 4 some one wants to connect you
                    player.dos.println("4");
                    for(int z=0; z<5&&answer==null;z++){
                         answer= player.loginData.readLine();
                            try {
                                sleep(1000);
                                
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                        // if 5 user accespt 
                    if(answer.equals("5")){
                        while(true){
                        String str1= loginData.readLine();
                        player.dos.println(str);
                        }
                   
                   }
               
               
               }
               
              
              
           }catch(SocketException e) {
               try {
                   loginData.close();
                   clinetsVector.remove( this);
               } catch (IOException ex) {
                   Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
            catch (IOException ex) {
               Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
   } 
        }.start();

    }
    
    
    
}
