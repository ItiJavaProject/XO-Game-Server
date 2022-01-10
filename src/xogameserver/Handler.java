package xogameserver;

import DataBase.DataAccessLayer;
import DataBase.PlayerModel;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Handler {

    public DataInputStream dis;
    public PrintStream ps;
    public Socket mySocket;
    private String username;
    private String opponent;
    public static Vector<Handler> clientsVector = new Vector<Handler>();
    private Handler myHandler;
    private JSONObject json ;
    private PlayerModel player ;
 

    public Handler(Socket s) {
        mySocket = s;
        myHandler = this;
        opponent = null;
        player = new PlayerModel();
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
                res = dis.readLine();
                json = new JSONObject(res);
                 if (json.get("header").equals("register")) {
                 flag = requestRegister(json);
                }
                else if (json.get("header").equals("login")) {
                    flag = requestLogin(json);
                }


            } catch (IOException ex) {
                Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
     private boolean requestLogin(JSONObject js){
         boolean notLogedBefore = true;
        boolean resLogin = true;
        try {
            for (Handler h : clientsVector) {
                    if (h.username.equals(username)) {
                        notLogedBefore = false;
                    }
                }
            username = (String) js.get("username");
            if (notLogedBefore && DataAccessLayer.UserLogin(username, (String) js.get("password"))){
                resLogin = true;
                Handler.clientsVector.add(myHandler);
                threadUserOnline.start();
                threadGame.start();
                }
            else{
                resLogin = false;
            }
             ps.println(String.valueOf(resLogin));
           
        } catch (JSONException ex) {
            Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
        } 
         return resLogin;
    }   
     private boolean requestRegister(JSONObject js) throws SQLException{
                 boolean resRegister= true;

        try {
                  player.setUserName((String) js.get("username"));
                  player.setPassword((String) js.get("password"));
                  player.setEmail((String) js.get("email"));
                  player.setName((String) js.get("name"));
                  player.setScore(0);
                  if(DataAccessLayer.CheckUser(username)){
                    resRegister = false;
                  }
                  else{
                  DataAccessLayer.registerInsertMethod(player);
                  Handler.clientsVector.add(myHandler);
                  threadUserOnline.start();
                  threadGame.start();
                  }
                 ps.println(String.valueOf(resRegister));
        } catch (JSONException ex) {
            Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resRegister;
          }
     public String getUsername() {
        return username;
    }
     
     public void closeStreams(){
        try {
            dis.close();
            ps.close();
        } catch (IOException ex) {
            Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
     
     }
     
     Thread threadUserOnline = new Thread(){
            @Override
            public void run() {
                while(true){
                try {                   
                    ArrayList<String> list = new ArrayList<String>();
                    json = new JSONObject();
                    for(Handler h:clientsVector){
                        System.out.println(h.username+" VS "+h.opponent);
                       if (!(h.username.equals(username)) && h.opponent == null ){
                            list.add(h.username);
                        }
                    }
                     JSONArray jsonArr = new JSONArray(list);
                        json.put("header","getOnlineUsers");
                        json.put("listUsersOnline",jsonArr);
                        ps.println(json.toString());
                    
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
                    
                    String res = dis.readLine();
                    json = new JSONObject(res);
                    if(json.get("header").equals("request")){
                    for(Handler h:clientsVector){
                            if ((h.username.equals(json.get("username"))) && h.opponent == null) {
                                JSONObject jsons = new JSONObject();
                                jsons.put("header", "request");
                                jsons.put("username", username);
                                
                                h.ps.println(jsons.toString());
                                break;
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
                    else if(json.get("header").equals("move") || json.get("header").equals("playingChar")) {
                             for (Handler h : clientsVector) {
                                if ((h.username.equals(opponent))) {
                                    if (json.get("header").equals("move") && (json.getInt("row") == -1 || json.getString("move").equals("full") || json.getString("move").equals("win"))) {
                                        opponent = null;
                                        h.opponent = null;
                                    }
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
