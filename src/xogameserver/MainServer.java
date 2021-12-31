package xogameserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

public class MainServer extends AnchorPane {

    protected final ListView lstOnlineUsers;
    protected final Label label;
    protected final Button btnStop;
    protected final Button btnStart;
    public ServerSocket mySocket;
    public Socket socket;

    public MainServer() {

        lstOnlineUsers = new ListView();
        label = new Label();
        btnStop = new Button();
        btnStart = new Button();

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

        btnStop.setLayoutX(213.0);
        btnStop.setLayoutY(342.0);
        btnStop.setMnemonicParsing(false);
        btnStop.setPrefHeight(39.0);
        btnStop.setPrefWidth(122.0);
        btnStop.setText(" Stop Services");

        btnStart.setLayoutX(37.0);
        btnStart.setLayoutY(342.0);
        btnStart.setMnemonicParsing(false);
        btnStart.setPrefHeight(39.0);
        btnStart.setPrefWidth(122.0);
        btnStart.setText("Start Services");

        getChildren().add(lstOnlineUsers);
        getChildren().add(label);
        getChildren().add(btnStop);
        getChildren().add(btnStart);
        try {
            mySocket = new ServerSocket(5005);
            socket = new Socket();
            new Thread() {
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

        } catch (IOException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
