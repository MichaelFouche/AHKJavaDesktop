/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahkjavadesktop;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author mifouche
 */
public class WinningGui implements ActionListener{
    //Scoreboard
    DBCommunicator dbc = new DBCommunicator();
    JFrame jfW;
    JPanel panelWN, panelWC, panelWS;
    JLabel lblWLogo, lblWplayer;
    JProgressBar pbWPlayer1, pbWPlayer2;
    JButton btnWReturn;
    
    Boolean scoreboardOpen;
    Boolean imTheJoiningUser;
    String loggedInUsername;
    String opponentUsername0;
    int sessionID;
    String opponentUsername1;
    String userToJoin;
    int matchID;
    
    ScheduledExecutorService ses5 = Executors.newScheduledThreadPool(10);
    
    public WinningGui()
    {
        
        ses5.scheduleAtFixedRate(new Runnable() 
        {
            @Override
            public void run() 
            {
                if(scoreboardOpen)
                {
                    if(imTheJoiningUser)//the user from the pool
                    {
                        //System.out.println("username:"+loggedInUsername+" opponent: "+opponentUsername[0]+ " session: "+sessionID+" match: "+opponentUsername[1]);
                        System.out.println("Results for joiner: ");
                        lblWplayer.setText(loggedInUsername+" VS "+opponentUsername0);
                        int score1 = dbc.getResults(sessionID,loggedInUsername, opponentUsername0);
                        int score2 = dbc.getOpponentScore(Integer.parseInt(opponentUsername1), opponentUsername0);
                        pbWPlayer1.setValue(score1);
                        pbWPlayer1.setString(loggedInUsername+" scored "+score1);
                        pbWPlayer2.setValue(score2);
                        pbWPlayer2.setString(opponentUsername0+" scored "+score2);
                        
                    }
                    else//the user that joined the user in the pool
                    {
                        //System.out.println("username:"+loggedInUsername+"opponent: "+userToJoin+ "session: "+sessionID+" match: "+matchID);
                        lblWplayer.setText(loggedInUsername+" VS "+userToJoin);
                        int score1 = dbc.getResults(sessionID,loggedInUsername, userToJoin);
                        int score2 = dbc.getOpponentScore(matchID, userToJoin);
                        pbWPlayer1.setValue(score1);
                        pbWPlayer1.setString(loggedInUsername+" scored "+score1);
                        pbWPlayer2.setValue(score2);
                        pbWPlayer2.setString(userToJoin+" scored "+score2);
                        boolean competitorAlsoDone = dbc.getGameComplete(sessionID,userToJoin, loggedInUsername);
                        System.out.println("competitorAlsoDone: "+competitorAlsoDone);
                        
                    }
                    //update who won here
                    
                }
            }
        }, 5, 5, TimeUnit.SECONDS);  // execute every x seconds
    }
    public void setAttributes(String loggedInUsername, String opponentUsername0, int sessionID,  String opponentUsername1,String userToJoin,int matchID)
    {
        this.loggedInUsername = loggedInUsername;
        this.opponentUsername0 = opponentUsername0;
        this.sessionID = sessionID;
        this.opponentUsername1 = opponentUsername1;
        this.userToJoin = userToJoin;
        this.matchID = matchID;
    }
    
    public Boolean getOpenStatus()
    {
        return scoreboardOpen;
    }
    public void createWinningGui()
     {   
         
         jfW = new JFrame("AHK - Scoreboard");
         panelWN = new JPanel();
         panelWC = new JPanel(new GridLayout(3,1));
         panelWS = new JPanel();
         
         try
         {              
            BufferedImage bi = ImageIO.read(getClass().getResource("ahkMiniLogo.JPG"));
            ImageIcon image = new ImageIcon(bi); 
            lblWLogo = new JLabel(image);
            panelWN.add(lblWLogo );
         }
         catch(Exception e)
         {
             System.out.println("createWinningGui(load image): \n"+e);             
         }
         
         lblWplayer = new JLabel("Player 1 vs Player 2");
         pbWPlayer1 = new JProgressBar(0,100);
         pbWPlayer1.setStringPainted(true);
         pbWPlayer1.setValue(0);
         pbWPlayer1.setString("Player 1 score loading");  
         
         pbWPlayer2 = new JProgressBar(0,100);
         pbWPlayer2.setStringPainted(true);
         pbWPlayer2.setValue(0);
         pbWPlayer2.setString("Player 1 score loading");
         
         panelWC.add(lblWplayer);
         panelWC.add(pbWPlayer1);
         panelWC.add(pbWPlayer2);
         
         btnWReturn = new JButton("Return to main window");
         btnWReturn.addActionListener(this);
         panelWS.add(btnWReturn);
         
         jfW.add(panelWN,BorderLayout.NORTH);
         jfW.add(panelWC,BorderLayout.CENTER);
         jfW.add(panelWS,BorderLayout.SOUTH);
         //jfW.setDefaultCloseOperation(jfW.DISPOSE_ON_CLOSE);
         jfW.pack();
         jfW.setResizable(false);
         jfW.setVisible(true);
         jfW.setLocationRelativeTo(null);//implement getLocation() or getLocationOnScreen() and call this class's method with it
         
         jfW.addWindowListener(new java.awt.event.WindowAdapter() 
         {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) 
            {
                {
                    jfW.dispose();
                    scoreboardOpen = false;
                }
            }
        });
     }
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()== btnWReturn)
        {
            jfW.dispose();
            scoreboardOpen = false;
            imTheJoiningUser = false;
            
            
        }
    }
}
