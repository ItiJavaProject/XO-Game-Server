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
  
    public Handler2(Socket s) {
        mySocket = s;
        try {
            dis = new DataInputStream(s.getInputStream());
            ps = new PrintStream(s.getOutputStream());

        } catch (IOException ex) {
            Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
                  
                    try {
                            if(dis.readLine().length()!=0){
                            JSONObject js = new JSONObject(dis.readLine());
                             username = (String) js.get("username");
                                System.out.println("usrname"+username);
                             ps.println("true");
                            //String password = (String) js.get("password");
                            }
                       } catch (JSONException ex) {
                            Logger.getLogger(Handler2.class.getName()).log(Level.SEVERE, null, ex);
                        }
                       catch (IOException ex) {
                        Logger.getLogger(Handler2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
             Handler2.clientsVector.add(this);
             /*String str = new String();
             for(Handler2 h:clientsVector){
                 if(!(h.username.equals(username))){
                     str = (str+h.username+"\\*");
                 }
             }
             ps.println(str);*/
    }
}
