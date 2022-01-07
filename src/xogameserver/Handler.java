package xogameserver;

import DataBase.DataAccessLayer;
import DataBase.PlayerModel;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import static java.lang.Thread.sleep;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Handler {

    private DataInputStream dis;
    private PrintStream ps;
    private Socket mySocket;
    private String username;
    private String opponent;
    public static Vector<Handler> clientsVector = new Vector<Handler>();
    private Handler myHandler;
    private PlayerModel player;
    private JSONObject json ;

    public Handler(Socket s) {
        mySocket = s;
        myHandler = this;
        opponent = null;
        player= new PlayerModel();
        try {
            dis = new DataInputStream(s.getInputStream());
            ps = new PrintStream(s.getOutputStream());

        } catch (IOException ex) {
            Logger.getLogger
        (Handler.class.getName()).log(Level.SEVERE, null, ex);
        }

        String res = null;
        boolean flag = false;
        while (!flag) {
            try {
                //boolean f = true;
                res = dis.readLine();
                json = new JSONObject(res);
                 if (json.get("header").equals("register")) {
                 flag = requestRegister(json);
                }
                else if (json.get("header").equals("login")) {
                    flag = requestLogin(json);
                }

                //String header =(String) json.get("header");
                /*username = (String) json.get("username");
                for (Handler h : clientsVector) {
                    if (h.username.equals(username)) {
                        f = false;
                    }
                }*/

              /*if(header.equals("login")){
                  player.setUserName(username);
             
              if (f && DataAccessLayer.CheckUser(username)) {
                    flag = DataAccessLayer.UserLogin(username, (String) js.get("password"));
                }
                if (flag) {
                    ps.println("true");
                    Handler.clientsVector.add(myHandler);
                } else {
                    ps.println("false");
                }
              }*/
              /*else if(header.equals("register")){
                  player.setUserName(username);
                  player.setPassword((String) js.get("password"));
                  player.setEmail((String) js.get("email"));
                  player.setName((String) js.get("name"));
                  player.setScore(0);
                  if(DataAccessLayer.CheckUser(username)){
                    ps.println("false");
                  }
                  else{
                  DataAccessLayer.registerInsertMethod(player);
                  Handler.clientsVector.add(myHandler);
                    flag=true;
                     ps.println("true");
                  }
                
              }*/

            } catch (IOException ex) {
                Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
     private boolean requestLogin(JSONObject js){
        try {
            username = (String) js.get("username");
            ps.println("true");
            Handler.clientsVector.add(myHandler);
            threadUserOnline.start();
            threadGame.start();
           
        } catch (JSONException ex) {
            Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
         return true;
    }   
     private boolean requestRegister(JSONObject js){
        try {
            username = (String) js.get("username");
            ps.println("true");
            Handler.clientsVector.add(myHandler);
        } catch (JSONException ex) {
            Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
          }
     public String getUsername() {
        return username;
    }
     
     Thread threadUserOnline = new Thread(){
            @Override
            public void run() {
                while(true){
                try {
                    System.out.println("hello thread UserOnline");
                    ArrayList<String> list = new ArrayList<String>();
                    json = new JSONObject();
                    for(Handler h:clientsVector){
                       if (!(h.username.equals(username)) && h.opponent == null ){
                            list.add(h.username);
                        } 
                        JSONArray jsonArr = new JSONArray(list);
                        json.put("header","getOnlineUsers");
                        json.put("listUsersOnline",jsonArr);
                        ps.println(json.toString());
                    }
                    
                } catch (JSONException ex) {
                    Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                      sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
            }
        };
     Thread threadGame = new Thread(){
            @Override
            public void run() {    
               while(true){
                try {
                     System.out.println("hello thread game");
                    String res = dis.readLine();
                    json = new JSONObject(res);
                    if(json.get("header").equals("request")){
                    for(Handler h:clientsVector){
                        if((h.username.equals(json.get("username")))&& h.opponent == null){
                            json = new JSONObject();
                            json.put("header","request");
                            json.put("username",username);
                            h.ps.println(json.toString());
                        }
                    }
                    }
                    
                    else if (json.get("header").equals("requestConfirm")) {
                             for (Handler h : clientsVector) {
                                if ((h.username.equals(json.get("username")))) {
                                    JSONObject js1 = new JSONObject();
                                    js1.put("header", "requestConfirm");
                                    js1.put("username", username);
                                    js1.put("res", (String) json.get("res"));
                                    if(json.get("res").equals("yes")){
                                        opponent = h.username;
                                        h.opponent = username;
                                    }
                                    h.ps.println(js1.toString());
                                }
                            }
                        }
                    else if(json.get("header").equals("move")) {
                             for (Handler h : clientsVector) {
                                if ((h.username.equals(opponent))) {
                                    h.ps.println(json.toString());
                                }
                            }
                        
                        }
                } catch (IOException ex) {
                    try {
                            dis.close();
                            ps.close();
                            mySocket.close();
                            clientsVector.remove(myHandler);
                        } catch (IOException ex1) {
                        Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                        sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }          
         };

}
