package xogameserver;

import DataBase.DataAccessLayer;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import static java.lang.Thread.sleep;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
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
        
        
         String res = null;
         boolean flag =false;
         while(!flag){
            try {
                System.out.println("from while");
                res = dis.readLine();
                if (!(res.equals("getusers"))) {
                    JSONObject js = new JSONObject(res);
                    username = (String) js.get("username");
                    flag=DataAccessLayer.UserLogin(username, (String) js.get("password"));         
                    if(flag){
                    ps.println("true");
                    Handler2.clientsVector.add(myHandler);
                    }
                    else{
                     ps.println("false");
                    }

                }

            } catch (IOException ex) {
                Logger.getLogger(Handler2.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(Handler2.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Handler2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
                        
        

        
            new Thread(){
            @Override
            public void run() {
                while(true){
                     try {
                         String res = null;
                        
                         res = dis.readLine();
                         JSONObject js = new JSONObject(res);
                         
                         if(js.get("header").equals("request")){
                            res = (String) js.get("username");
                             for(Handler2 h:clientsVector){
                                 if((h.username.equals(res))){
                                    js = new JSONObject();
                                    js.put("header","request");
                                    js.put("username",username);
                                    h.ps.println(js.toString());
                             }
                             }
                           
                         }
                         
                       } 
                       catch (IOException ex) {
                        Logger.getLogger(Handler2.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JSONException ex) {
                        Logger.getLogger(Handler2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                     
                      try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Handler2.class.getName()).log(Level.SEVERE, null, ex);
                    }
              }
            }
      }.start();       
    
    
    

        new Thread() {
            @Override
            public void run() {
              while(true) {
                    String str = new String();
                    for (Handler2 h : clientsVector) {
                        if (!(h.username.equals(username))) {
                            str += h.username + "*";
                        }
                    }
                    JSONObject js = new JSONObject();
                    try {
                        js.put("header", "usersList");
                        js.put("list", str);
                        ps.println(js.toString());
                    } catch (JSONException ex) {
                        Logger.getLogger(Handler2.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Handler2.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
            

        }.start();
    }

    public String getUsername() {
        return username;
    }
}
