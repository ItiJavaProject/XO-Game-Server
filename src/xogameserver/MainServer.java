package xogameserver;

import DataBase.DataAccessLayer;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class MainServer extends AnchorPane {

     protected  ListView lstOnlineUsers;
    protected  Label label;
    protected  Button btnStart;
    protected final Label lbIp;
    protected  Label lbIpAdd;
    public ServerSocket mySocket;
    public Socket socket;
    

    public MainServer() {
        try {
            DataAccessLayer.connect();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        lstOnlineUsers = new ListView();
         label = new Label();
        btnStart = new Button();
        lbIp = new Label();
        lbIpAdd = new Label();

        setId("AnchorPane");
        setPrefHeight(476.0);
        setPrefWidth(618.0);

        lstOnlineUsers.setLayoutX(389.0);
        lstOnlineUsers.setLayoutY(57.0);
        lstOnlineUsers.setPrefHeight(395.0);
        lstOnlineUsers.setPrefWidth(215.0);

        label.setAlignment(javafx.geometry.Pos.CENTER);
        label.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
        label.setLayoutX(389.0);
        label.setLayoutY(20.0);
        label.setPrefHeight(27.0);
        label.setPrefWidth(215.0);
        label.setText("Online Users");

        btnStart.setLayoutX(177.0);
        btnStart.setLayoutY(269.0);
        btnStart.setMnemonicParsing(false);
        btnStart.setPrefHeight(39.0);
        btnStart.setPrefWidth(122.0);
        btnStart.setText("Start Services");

        lbIp.setAlignment(javafx.geometry.Pos.CENTER);
        lbIp.setLayoutX(186.0);
        lbIp.setLayoutY(115.0);
        lbIp.setPrefHeight(17.0);
        lbIp.setPrefWidth(74.0);
        lbIp.setText("Ip Address");

        lbIpAdd.setAlignment(javafx.geometry.Pos.CENTER);
        lbIpAdd.setLayoutX(145.0);
        lbIpAdd.setLayoutY(148.0);
        lbIpAdd.setPrefHeight(27.0);
        lbIpAdd.setPrefWidth(162.0);
        lbIpAdd.setText("0.0.0.0");
        
         try {
             lbIpAdd.setText(Inet4Address.getLocalHost().getHostAddress());
         } catch (UnknownHostException ex) {
             Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
         }


        getChildren().add(lstOnlineUsers);
        getChildren().add(label);
        getChildren().add(btnStart);
        getChildren().add(lbIp);
        getChildren().add(lbIpAdd);
        
       /* new Thread(){
            @Override
            public void run() {
                while(true){
                    ObservableList<String> listUsersOnline = FXCollections.observableArrayList();
                    for(Handler h:Handler.clientsVector){
                        listUsersOnline.add(h.getUsername());
                        
                    }
                    Platform.runLater(() -> {
                          lstOnlineUsers.setItems(listUsersOnline);
                    });
                      
                          
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }   
                }
           
        }.start();*/

        //try {
            //mySocket = new ServerSocket(7001);
            socket = new Socket();
            t.start();
            t.suspend();
            onlineUserThread.start();
            onlineUserThread.suspend();
            
            /*new Thread() {
                public void run() {
                    while (true) {
                        try {
                            socket = mySocket.accept();
                            new Handler(socket);
                        } catch (IOException ex) {
                            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }.start();

       /* } catch (IOException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        btnStart.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
           public void handle(MouseEvent event)  {
               String txt= btnStart.getText();
               if(txt.equals("Start Services")){
                   try {
                       mySocket = new ServerSocket(7001);
                       socket=new Socket();
                        t.resume();
                        onlineUserThread.resume();
                        btnStart.setText("Stop Services");
                        Handler.clientsVector=new Vector<Handler>();
                   } catch (IOException ex) {
                       Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
                   }
               }
               else if(txt.equals("Stop Services")){
                   try {
                       mySocket.close();
                       onlineUserThread.suspend();
                       t.suspend();
                       for(Handler h : Handler.clientsVector){
                           h.threadGame.suspend();
                           h.threadUserOnline.suspend();
                           h.ps.close();
                           h.dis.close();
                           h.mySocket.close();
                           Handler.clientsVector.remove(h);
                       }
                       btnStart.setText("Start Services");
                   } catch (IOException ex) {
                       Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
                   }
                   
               }
            }
        });
    }
    Thread t= new Thread() {
                public void run() {
                    while (true) {
                        try {
                            socket = mySocket.accept();
                            new Handler(socket);
                        } catch (IOException ex) {
                            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            };
    
    Thread onlineUserThread= new Thread(){
            @Override
            public void run() {
                while(true){
                    ObservableList<String> listUsersOnline = FXCollections.observableArrayList();
                    for(Handler h:Handler.clientsVector){
                        listUsersOnline.add(h.getUsername());
                        
                    }
                    Platform.runLater(() -> {
                          lstOnlineUsers.setItems(listUsersOnline);
                    });
                      
                          
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }   
                }
           
        };
}
