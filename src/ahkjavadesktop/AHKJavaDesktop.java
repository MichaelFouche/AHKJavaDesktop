/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahkjavadesktop;

import com.mysql.jdbc.Driver;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 *
 * @author mifouche
 */
public class AHKJavaDesktop {
   
    public AHKJavaDesktop() throws SQLException
    {
        
    } 
         
    public static void main(String[] args) throws IOException {
        ArrayList<String> allImagesForGame = new ArrayList();
        AllGameImages agi = new AllGameImages();
        allImagesForGame.addAll(agi.getAllImages());
        MainGui mg = new MainGui();
        mg.setAllImagesForGame(allImagesForGame);
        //System.out.println("hello updater");
        try {
             AHKJavaDesktop c = new AHKJavaDesktop();
             
             //c.AmortizationLayout();
            mg.createAHKGui();
            mg.gameTimeEnable(false);
            mg.gamePoolEnable(false);
            System.out.println("Connection Established");
             
         }
         catch (SQLException e) {
             e.printStackTrace();
             System.err.println("Connection Failure");
         } 
        

        
                
    }

}
