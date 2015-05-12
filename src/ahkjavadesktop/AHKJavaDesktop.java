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
public class AHKJavaDesktop implements ActionListener{
DBCommunicator dbc = new DBCommunicator();
   
    
    //Scoreboard
    
    
    public AHKJavaDesktop() throws SQLException
    {
        
    } 
    
    
    

    

        
     /*
     
     public void getRecords()
     {
         try
         {
            conn = makeConnection();
            Statement s = conn.createStatement();

            s.executeQuery("Select userID, email, password, score from users"); // select the data from the table

            ResultSet rs = s.getResultSet(); // get any ResultSet that came from our query
            if (rs != null) // if rs == null, then there is no ResultSet to view  
            {
                while (rs.next())
                {
                    System.out.println( rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getInt(4));
                }
            }
            s.close(); // close the Statement to let the database know we're done with it
            conn.close();
         }
         catch(Exception e)
         {
             System.out.println(e);
         }
         
     }*/
     
     
     
     
     
     
     
     public void getAllImages() throws IOException
     {
         try
         {
            allImagesForGame.clear();
            String path = "./src/ahkjava/resources/images"; 

            String fileItem;
            File folder = new File(path);
            File[] listOfFiles = folder.listFiles(); 

            for (int i = 0; i < listOfFiles.length; i++) 
            {

                if (listOfFiles[i].isFile()) 
                {
                    fileItem = listOfFiles[i].getName();
                    if (fileItem.endsWith(".jpg") || fileItem.endsWith(".JPG"))
                    {
                        allImagesForGame.add(fileItem);
                    }
                }
            }
         }
         catch(Exception e)
         {
             System.out.println("Get all images"+e);
         }
                 
        
        
     }
     public void getNextQuestion()
     {
         imageID = allImagesForGame.get(currentImageViewing);
        
         //System.out.println("Imageid: "+imageID.substring(0, imageID.length() - 4));
         questionForGameImage = dbc.requestQuestionForImage(imageID.substring(0, imageID.length() - 4));
         try
         {              
            BufferedImage bi = ImageIO.read(getClass().getResource("resources/images/"+imageID));
            ImageIcon image = new ImageIcon(bi); 
            lblGamePic.setIcon(image);
            //panelGameW.add(lblGamePic );
         }
         catch(Exception e)
         {
             System.out.println("getNextQuestion(load image): \n"+e);
         }
         try
         {
            rbc1.setText(questionForGameImage.get(0));
            rbc2.setText(questionForGameImage.get(1));
            rbc3.setText(questionForGameImage.get(2));
            rbc4.setText(questionForGameImage.get(3));
         }
         catch(Exception e)
         {
             System.out.println("Error setting radiobuttons \n"+e);
         }
         currentImageViewing=currentImageViewing+1;
         if(currentImageViewing>allImagesForGame.size()-1)
         {
             currentImageViewing=0;
         }
         
         
         
         //System.out.println("currentImageViewing: "+currentImageViewing);
         //need to update that entire center panel to allow the length of each place to not f**k everything up
         //set the question number
         //set the amount of correct answers
         
     }
     
     public void actionPerformed(ActionEvent e)
    {
        //Execute when button is pressed
        
        
        /*btnJoin*/
        if(poolSize>0)
        {
            
            for(int a =0;a<poolSize;a++)
            {
                if(e.getSource()==btnJoin[a])
                {
                    userToJoin = lblUser[a].getText();
                    if(dbc.userAvailable(userToJoin))
                    {  
                        getNextQuestion();
                        
                        gameTimeEnable(true);
                        gamePoolEnable(false);
                        progressSize = 30;
                        gameTimeLeft = true;
                        flagInGame = true;
                        matchID = 0;
                        try
                        {
                            matchID = dbc.getNextMatchID();
                            dbc.joinUserInPool(loggedInUsername,userToJoin,matchID);
                            sessionID = dbc.createMatch(matchID, loggedInUsername, userToJoin, 1, 0);
                            //System.out.println("Session: "+sessionID);
                        }
                        catch(Exception ee)
                        {
                            System.out.println("Could not create the game session: \n"+ee);
                        }
                        System.out.println("username:"+loggedInUsername+"opponent: "+userToJoin+ "session: "+sessionID+" match: "+matchID);
                        lblQuest2.setText(dbc.getCurrentQuestionForUser(sessionID, loggedInUsername)+"");
                        lblCorrect2.setText(dbc.getScoreForUser(sessionID, loggedInUsername)+"");
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Sorry, the user is not available anymore","AHK - Pool",JOptionPane.ERROR_MESSAGE);
                    }
                    
                }
            }
            
        }
        if(e.getSource()==btnAddUserToPool)
        {
            if(btnAddUserToPool.getText().equals("Join Pool"))
            {
                if(dbc.checkUserInPool(loggedInUsername))
                {
                    JOptionPane.showMessageDialog(null, "You are already in the pool","AHK - Pool",JOptionPane.ERROR_MESSAGE);
                    btnJoinPoolText = "Leave Pool";
                    btnAddUserToPool.setText(btnJoinPoolText);
                }
                else if(dbc.addUserToPool(loggedInUsername))
                {                    
                    btnJoinPoolText = "Leave Pool";
                    btnAddUserToPool.setText(btnJoinPoolText);
                    updatePoolPanel();
                    waitingInPool = true;
                }
            }
            else
            {
                if(dbc.checkUserInPool(loggedInUsername))
                {
                    if(dbc.userAvailable(loggedInUsername))
                    {
                        dbc.removeUserFromPool(loggedInUsername);
                        btnJoinPoolText = "Join Pool";
                        btnAddUserToPool.setText(btnJoinPoolText);
                        updatePoolPanel();
                        waitingInPool = false;
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "You are not in the pool","AHK - Pool",JOptionPane.ERROR_MESSAGE);
                    }

                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You are not in the pool","AHK - Pool",JOptionPane.ERROR_MESSAGE);
                    btnJoinPoolText = "Join Pool";
                    btnAddUserToPool.setText(btnJoinPoolText);
                    updatePoolPanel();
                }
                btnAddUserToPool.setText("Join Pool");
            }
            
            
        }        
            
        if(e.getSource()==btnSubmitAnswer)
        {
            String answerText = "";
            if(rbc1.isSelected())
            {
                answerText = rbc1.getText();
            }
            else if(rbc2.isSelected())
            {
                answerText = rbc2.getText();
            }
            else if(rbc3.isSelected())
            {
                answerText = rbc3.getText();
            }
            else if(rbc4.isSelected())
            {
                answerText = rbc4.getText();
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Oops, you didn't select an answer","AHK - Pool",JOptionPane.ERROR_MESSAGE);
            }
            
            boolean answerCorrect = dbc.submitAnswer(imageID.substring(0, imageID.length() - 4),answerText);
            dbc.updateAnswer(sessionID, loggedInUsername,answerCorrect);
            
            lblQuest2.setText(dbc.getCurrentQuestionForUser(sessionID, loggedInUsername)+"");
            lblCorrect2.setText(dbc.getScoreForUser(sessionID, loggedInUsername)+"");
            //get the radio button selected
            //match the rb text to the asnwer for the question
            //add the mark if correct
            getNextQuestion();
        }
        if(e.getSource()== btnWReturn)
        {
            jfW.dispose();
            scoreboardOpen = false;
            imTheJoiningUser = false;
            
            gamePoolEnable(true);
            gameTimeEnable(false);
        }
    }
 
    /*public void AmortizationLayout() 
    {
        JFrame jf = new JFrame();
        JPanel gui = new JPanel(new BorderLayout(2,2));
        JPanel labelFields = new JPanel(new BorderLayout(2,2));
        labelFields.setBorder(new TitledBorder("BorderLayout"));

        JPanel labels = new JPanel(new GridLayout(0,1,1,1));
        labels.setBorder(new TitledBorder("GridLayout"));
        JPanel fields = new JPanel(new GridLayout(0,1,1,1));
        fields.setBorder(new TitledBorder("GridLayout"));

        for (int ii=1; ii<4; ii++) {
            labels.add(new JLabel("Label " + ii));
            // if these were of different size, it would be necessary to
            // constrain them using another panel
            fields.add(new JTextField(10));
        }

        labelFields.add(labels, BorderLayout.CENTER);
        labelFields.add(fields, BorderLayout.EAST);

        JPanel guiCenter = new JPanel(new BorderLayout(2,2));
        guiCenter.setBorder(new TitledBorder("BorderLayout"));
        JPanel buttonConstrain = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonConstrain.setBorder(new TitledBorder("FlowLayout"));
        buttonConstrain.add( new JButton("Click Me") );
        guiCenter.add( buttonConstrain, BorderLayout.NORTH );

        guiCenter.add(new JScrollPane(new JTextArea(5,30)));

        gui.add(labelFields, BorderLayout.NORTH);
        gui.add(guiCenter, BorderLayout.CENTER);

        //JOptionPane.showMessageDialog(null, gui);
        jf.add(gui);
        jf.setVisible(true);
    }*/
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        //System.out.println("hello updater");
        try {
             AHKJavaDesktop c = new AHKJavaDesktop();
             c.getAllImages();
             c.getRecords();
             //c.AmortizationLayout();
             c.createAHKGui();
             System.out.println("Connection Established");
             c.gameTimeEnable(false);
             c.gamePoolEnable(false);
         }
         catch (SQLException e) {
             e.printStackTrace();
             System.err.println("Connection Failure");
         }  
       
        DBCommunicator dbc = new DBCommunicator();
        
        //System.out.println("User exists: "+dbc.usernameExists("foosh"));
        //System.out.println("User exists: "+dbc.usernameExists("ryno"));
        

        
                
    }

}
