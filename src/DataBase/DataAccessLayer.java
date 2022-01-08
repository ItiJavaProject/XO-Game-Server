/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.jdbc.ClientDriver;


public class DataAccessLayer {
   private static Connection conection;
   public static void connect() throws SQLException
   {
           DriverManager.registerDriver (new ClientDriver());
           conection=DriverManager.getConnection("jdbc:derby://localhost:1527/XOgame","root","root");
   }
   public static void registerInsertMethod(PlayerModel player) throws SQLException{
    PreparedStatement stmt = conection.prepareStatement("insert into PLAYER (USERNAME,NAME,EMAIL,PASSWORD,SCORE) Values(?,?,?,?,?)");
    stmt.setString(1,player.getUserName());
    stmt.setString(2,player.getName());
    stmt.setString(3,player.getEmail());
    stmt.setString(4,player.getPassword());
    stmt.setInt(5, player.getScore());
    stmt.executeUpdate();
   }
   
     public static boolean CheckUser(String userName) throws SQLException{
          PreparedStatement stmt = conection.prepareStatement("select * from PLAYER where USERNAME = ? ");
          stmt.setString(1,userName);
          ResultSet rs = stmt.executeQuery();
          boolean result=false;
          if(rs.next()){
              result=true;
          } 
       return result;
   }
        
    public static boolean UserLogin(String userName, String password) {
    
        boolean result=false;
       try {
           PreparedStatement stmt = conection.prepareStatement("select * from PLAYER where USERNAME = ? and PASSWORD = ?");
           stmt.setString(1,userName);
           stmt.setString(2,password);
           ResultSet rs = stmt.executeQuery();
           
           if(rs.next()){
                   result=true;
           }
           
       } catch (SQLException ex) {
          result = false;
       }
       return result;
    }
     
    public static void updateScore(String userName) throws SQLException {
        PreparedStatement stmt1 = conection.prepareStatement("select SCORE from PLAYER where USERNAME = ?");
        stmt1.setString(2, userName);
        ResultSet rs = stmt1.executeQuery();
        int score = rs.getInt("PASSWORD");
        PreparedStatement stmt = conection.prepareStatement(" UPDATE USERCONTACT  SET SCORE=? WHERE  USERNAME = ?");
        stmt.setInt(1, score + 1);
        stmt.setString(2, userName);
        stmt.executeUpdate();
    }
   
   
}
