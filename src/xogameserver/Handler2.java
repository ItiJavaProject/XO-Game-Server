package xogameserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import static java.lang.Thread.sleep;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;

public class Handler2 {

    private DataInputStream dis;
    private PrintStream ps;
    private Socket mySocket;
    private String username;
    public static Vector<Handler2> clientsVector = new Vector<Handler2>();
    private Handler2 myHandler;
  
    public Handler2(Socket s) {
        mySocket = s;
        myHandler = this;
        try {
            dis = new DataInputStream(s.getInputStream());
            ps = new PrintStream(s.getOutputStream());

        } catch (IOException ex) {
            Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
            new Thread(){
            @Override
            public void run() {
                while(true){
                     try {
                         String res = null;
                         res = dis.readLine();
                         if(!(res.equals("getusers"))){
                            JSONObject js = new JSONObject(res);
                            username = (String) js.get("username");
                            System.out.println("usrname"+username);
                             ps.println("true");
                             Handler2.clientsVector.add(myHandler);
                            //String password = (String) js.get("password");
                         }
                         else if(res.equals("getusers")){
                             String str = new String();
                             for(Handler2 h:clientsVector){
                                 if(!(h.username.equals(username))){
                                 str += h.username+"*";
                                 System.out.println( username+" users"+str);
                             }
                             }
                            ps.println(str);
                         }
                         
                         else if(res.equals("request")){
                             res = dis.readLine();
                             for(Handler2 h:clientsVector){
                                 if((h.username.equals(res))){
                                    h.ps.println(username);
                                    res = h.dis.readLine();
                             }
                             }
                               ps.println(res);
                         }
                            
                       } catch (JSONException ex) {
                            Logger.getLogger(Handler2.class.getName()).log(Level.SEVERE, null, ex);
                        }
                       catch (IOException ex) {
                        Logger.getLogger(Handler2.class.getName()).log(Level.SEVERE, null, ex);
                    }        
              }
            }
      }.start();       
    }
    
    public String getUsername(){
        return username;
    }
}
