/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahkjavadesktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.sql.Connection;
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
public class MainGui implements ActionListener{
    
     private JButton btnSignIn,btnRegister;
    JLabel lblLogin, lblPW;
    JTextField txtLogin;
    JPasswordField txtPW;
    
    public JFrame jf;
    JPanel panelMain, panelHeading, panelLogo, panelLogin;
    JPanel panelPool,panelPoolN,panelPoolS;
       
    JLabel lblGamePic;
    JProgressBar pbGame;
    JPanel panelGame, panelGameW, panelGameE,panelGameS,panelGameN;
    JRadioButton rbc1, rbc2, rbc3, rbc4;
    ButtonGroup rbGroup;
    JLabel lblQuest1, lblQuest2, lblCorrect1, lblCorrect2;
    
    //pool
    JLabel lblUser[], lblScore[];
    JButton btnJoin[];
    JButton btnAddUserToPool;
    JScrollPane scrollPane;
    JPanel contentPane;
    int poolSize;
    String imageID;
    
    JButton btnSubmitAnswer;
    Boolean imTheJoiningUser;
    //game variables
    
    String loggedInUsername;
    ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);
    ScheduledExecutorService ses5 = Executors.newScheduledThreadPool(10);
    public int progressSize;
    boolean gameTimeLeft;
    boolean flagInGame;
    String userToJoin;
    
    ArrayList<ArrayList<String>> poolList;
    ArrayList<String> allImagesForGame;
    ArrayList<String> questionForGameImage;
    String btnJoinPoolText ;
    String[] opponentUsername = {"",""};
    int currentImageViewing;
    int sessionID;
    boolean waitingInPool;
    boolean justFinishedGame;
    int matchID;
    
    //winningGui
    boolean mgScoreboardOpen;
    
    private Connection conn;  
    public MainGui()
    {
        DBCommunicator dbc = new DBCommunicator();
        WinningGui wg = new WinningGui();
        
        try
        {
            MakeConnection mc = new MakeConnection();
            mc.makeConnection();
        }
        catch(Exception e)
        {
            System.out.println("Failed: Connecting to database: \n"+e);
        }
        loggedInUsername = "";
        poolSize = 0;        
        flagInGame = false;
        btnJoinPoolText = "Join Pool";
        currentImageViewing = 0;
        allImagesForGame = new ArrayList<>();
        waitingInPool = false;
        justFinishedGame = false;
        mgScoreboardOpen = false;
        imTheJoiningUser = false;
        gameTimeLeft = false;
        
        
        ses.scheduleAtFixedRate(new Runnable() 
        {
            @Override
            public void run() 
            {
                //System.out.println("execute the timer query");
                //Update Pool
                //check if user in pool, then whether the user was matched yet to another user.
                
                if(gameTimeLeft)
                {
                    pbGame.setValue(progressSize);
                    progressSize = progressSize-1;
                    pbGame.setString(progressSize + " Seconds Remaining");
                    if(progressSize<1)
                    {
                        gameTimeLeft = false;
                        flagInGame = false;
                        justFinishedGame = true;
                        mgScoreboardOpen = true;
                        wg.setAttributes(loggedInUsername, opponentUsername[0], sessionID, opponentUsername[1],userToJoin, matchID);
                        wg.createWinningGui();
                    } 
                }
                
                
                
                
            }
        }, 5, 1, TimeUnit.SECONDS);  // execute every x seconds
        
        ses5.scheduleAtFixedRate(new Runnable() 
        {
            @Override
            public void run() 
            {
                //System.out.println("execute the timer query");
                //Update Pool
                //check if user in pool, then whether the user was matched yet to another user. 
                if(waitingInPool)
                {
                    //System.out.println("Waiting in pool");
                    opponentUsername = dbc.matchFoundInPool(loggedInUsername);
                    //System.out.println("Opponent try: "+opponentUsername);
                    //opponentUsername[1] is the matchID  <-- IMPORTANT
                    if(!opponentUsername[0].equals(""))
                    {
                        //System.out.println("Partner found");
                        dbc.removeUserFromPool(loggedInUsername);
                        gameTimeEnable(true);
                        gamePoolEnable(false);
                        progressSize = 30;
                        gameTimeLeft = true;
                        flagInGame = true;
                        waitingInPool = false;
                        imTheJoiningUser = true;
                        //--------
                        
                        try
                        {
                            //opponentUsername[1] is the matchID  <-- IMPORTANT
                            sessionID = dbc.createMatch(Integer.parseInt(opponentUsername[1]), loggedInUsername, opponentUsername[0], 1, 0);
                            //dbc.createMatch(matchID, loggedInUsername, userToJoin, 1, 0);
                            //System.out.println("Session: "+sessionID);
                        }
                        catch(Exception ee)
                        {
                            System.out.println("Could not create the game session: \n"+ee);
                        }
                        System.out.println("username:"+loggedInUsername+" opponent: "+opponentUsername[0]+ " session: "+sessionID+" match: "+opponentUsername[1]);
                        getNextQuestion();
                        //start game.
                    }       
                }
                
                if(justFinishedGame && imTheJoiningUser)
                {
                    int score1 = dbc.getResults(sessionID,loggedInUsername, userToJoin);
                    int score2 = dbc.getOpponentScore(matchID, userToJoin);
                    if(score2!=-1&&score1!=-1)
                    {
                        //call the function to updatefinal marks
                        justFinishedGame = false;
                    }
                    
                    
                   // JOptionPane.showMessageDialog(null, "Your time is up!\nYou answered "+dbc.getCurrentQuestionForUser(sessionID, loggedInUsername)+" questions, with "+dbc.getScoreForUser(sessionID, loggedInUsername)+" correct","AHK - Game",JOptionPane.ERROR_MESSAGE);
                   //check if both has a score more than 1, the user who was in the pool should be the one updating the score
                }
                
                updatePoolPanel();
                    
                
            }
        }, 5, 5, TimeUnit.SECONDS);  // execute every x seconds

    }
    
    public void createAHKGui()
     {
         jf = new JFrame();
         panelMain = new JPanel(new BorderLayout(2,2));
         panelHeading = new JPanel(new FlowLayout(FlowLayout.CENTER));
         panelHeading.setBorder(new TitledBorder("African Heritage King"));
         panelLogo = new JPanel();
         panelLogin = new JPanel(new GridLayout(3,2));
         
         
         try
         {              
            BufferedImage bi = ImageIO.read(getClass().getResource("ahkLogo.JPG"));
            ImageIcon image = new ImageIcon(bi); 
            JLabel l1 = new JLabel(image);
            panelLogo.add(l1 );
         }
         catch(Exception e)
         {
             System.out.println("createAHKGui(load image): \n"+e);
         }         
            
         
         lblLogin = new JLabel("Username");
         lblPW = new JLabel("Password");
         txtLogin = new JTextField(10);
         txtPW = new JPasswordField(10);
         btnSignIn = new JButton("Sign in");
         btnSignIn.addActionListener(this);
         btnRegister = new JButton("Register");
         btnRegister.addActionListener(this);
         txtLogin.setText(null);
         txtPW.setText(null);
         
         //panelLogin.add(lblTitle);
         panelLogin.add(lblLogin);
         panelLogin.add(lblPW);
         panelLogin.add(txtLogin);
         panelLogin.add(txtPW);
         panelLogin.add(btnSignIn);
         panelLogin.add(btnRegister);
         panelHeading.add(panelLogo,BorderLayout.WEST);
         panelHeading.add(panelLogin,BorderLayout.EAST);
         
         //PANEL POOL
         panelPool = new JPanel(new BorderLayout(2,2));
         this.addGamePoolToGUI();
         //GAME PANEL
         
         
         panelGame = new JPanel(new BorderLayout(2,2));
         panelGame.setBorder(new TitledBorder("Game Time"));
         panelGameW = new JPanel();
         panelGameE = new JPanel(new GridLayout(4,1));
         panelGameS = new JPanel();
         panelGameN = new JPanel(new BorderLayout(1,1));
         
         DefaultBoundedRangeModel model = new DefaultBoundedRangeModel(100, 50, 0, 250);
         pbGame = new JProgressBar(0,30);
         pbGame.setStringPainted(true);
         pbGame.setValue(30);
         pbGame.setString("30 Seconds remaining");    
         panelGameN.add(pbGame);
         
         try
         {              
            BufferedImage bi = ImageIO.read(getClass().getResource("tablemountain.jpg"));
            ImageIcon image = new ImageIcon(bi); 
            lblGamePic = new JLabel(image);
            panelGameW.add(lblGamePic );
         }
         catch(Exception e)
         {
             System.out.println("createAHKGui(load image): \n"+e);
         }
         rbc1 = new JRadioButton("Choice 1");
         rbc1.addActionListener(this);
         rbc2 = new JRadioButton("Choice 2");
         rbc1.addActionListener(this);
         rbc3 = new JRadioButton("Choice 3");
         rbc1.addActionListener(this);
         rbc4 = new JRadioButton("Choice 4");
         
         rbGroup = new ButtonGroup();
         rbGroup.add(rbc1);
         rbGroup.add(rbc2);
         rbGroup.add(rbc3);
         rbGroup.add(rbc4);
         
         panelGameE.add(rbc1);
         panelGameE.add(rbc2);
         panelGameE.add(rbc3);
         panelGameE.add(rbc4);
         
         
         btnSubmitAnswer = new JButton("Submit Answer");
         btnSubmitAnswer.addActionListener(this);
         lblQuest1 = new JLabel("Question ");
         lblQuest2 = new JLabel("1");
         lblCorrect1 = new JLabel("Correct");
         lblCorrect2 = new JLabel("0");
         panelGameS.add(btnSubmitAnswer);
         panelGameS.add(lblQuest1);
         panelGameS.add(lblQuest2);
         panelGameS.add(new JLabel("~ ~"));
         panelGameS.add(lblCorrect1);
         panelGameS.add(lblCorrect2);         
         
         panelGame.add(panelGameN, BorderLayout.NORTH);
         panelGame.add(panelGameW,BorderLayout.WEST);
         panelGame.add(panelGameE,BorderLayout.EAST);
         panelGame.add(panelGameS,BorderLayout.SOUTH);
         
         
         //END OF GAME PANEL
         JPanel panelSouth;
         panelSouth = new JPanel();
         panelSouth.add(new JLabel(""));
         
         jf.add(panelHeading, BorderLayout.NORTH);
         jf.add(panelPool, BorderLayout.WEST);
         jf.add(panelGame, BorderLayout.EAST);
         jf.add(panelSouth, BorderLayout.SOUTH);
         jf.setSize(1350,700);
         jf.setLocationRelativeTo(null);
         jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
         
         
         jf.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //DISPOSE_ON_CLOSE,  DISPOSE_ON_CLOSE 
        jf.addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosing(WindowEvent e) 
            {
                int result = JOptionPane.showConfirmDialog(jf, "Are you sure you would like to exit?");
                if( result==JOptionPane.OK_OPTION)
                {
                    // NOW we change it to dispose on close..
                    jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
                    jf.setVisible(false);
                    jf.dispose();
                    try
                    {
                        
                    }
                    catch(Exception ee)
                    {
                        System.out.println("ERROR in leaving pool when exiting program\n"+ee);
                    }
                    //check if in pool, if in pool, delete
                    if(dbc.checkIfUserInPool(loggedInUsername))
                    {
                        dbc.removeUserFromPool(loggedInUsername);
                    }
                }
            }
        });
         
         jf.setVisible(true);
     }
    public void addGamePoolToGUI()
     {
         //--PANEL POOL   
         
         
         panelPool.setBorder(new TitledBorder("Game Pool"));
         panelPoolN = new JPanel();
         panelPoolS = new JPanel();
         
         poolList = dbc.getPoolList();  
         poolSize = poolList.size(); 
         lblUser = new JLabel[poolSize];
         lblScore = new JLabel[poolSize];
         btnJoin = new JButton[poolSize];
         
           
         for(int i=0;i<poolSize;i++)
         {
             ArrayList<String> currentList = poolList.get(i);
             lblUser[i] = new JLabel(currentList.get(0));

             lblScore[i] = new JLabel(currentList.get(1));
             btnJoin[i] = new JButton("Join");
             btnJoin[i].addActionListener(this);
         }
         
         //get the amount of users in pool, then print those, and print empty labels for the rest (10rows) to display nicely.
         JPanel panel;
         if(poolSize<11)
         {
             panel = new JPanel(new GridLayout(10,3) );
         }
         else
         {
             panel = new JPanel(new GridLayout(poolSize,3) );             
         }
         
         for (int i = 0; i < poolSize; i++) 
         {             
            panel.add(lblUser[i]);
            panel.add(lblScore[i]);
            panel.add(btnJoin[i]);
         }
         if(poolSize<11)
         {
            for(int i=0;i<10-poolSize;i++)
            {
               panel.add(new JLabel(""));
               panel.add(new JLabel(""));
               panel.add(new JLabel(""));
            }    
         }
         
         scrollPane = new JScrollPane(panel);
         scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
         scrollPane.setBounds(10, 10, 400, 300);
         contentPane = new JPanel(null);
         contentPane.setPreferredSize(new Dimension(450, 300));
         contentPane.add(scrollPane);
         panelPoolN.add(contentPane);
         
         if(dbc.checkUserInPool(loggedInUsername))
         {
            btnJoinPoolText = "Leave Pool";
         }
         btnAddUserToPool = new JButton(btnJoinPoolText);
         btnAddUserToPool.addActionListener(this);
         
         panelPoolS.add(btnAddUserToPool);
         
         panelPool.add(panelPoolN,BorderLayout.NORTH);
         panelPool.add(panelPoolS, BorderLayout.SOUTH);
         //END OF PANEL POOL
         
     }
     public void updatePoolPanel()
     {
        panelPool.removeAll();
        addGamePoolToGUI();
        panelPool.revalidate();
        panelPool.repaint();
        panelPool.updateUI();
        if(loggedInUsername.equals("")&&!flagInGame)
        {
            gameTimeEnable(false);
            gamePoolEnable(false);
        }
        else if(flagInGame)
        {
            gameTimeEnable(true);
            gamePoolEnable(false);
        }
        else if(!loggedInUsername.equals("")&&!flagInGame)
        {
            gameTimeEnable(false);
            gamePoolEnable(true);
        }
     }
     public void gamePoolEnable(boolean flag)
     {
         for(int a=0;a<poolList.size();a++)
         {
             lblUser[a].setEnabled(flag);
         }
         for(int a=0;a<poolList.size();a++)
         {
             lblScore[a].setEnabled(flag);
         }
         for(int a=0;a<poolList.size();a++)
         {
             btnJoin[a].setEnabled(flag);
         }    
         panelPool.setEnabled(flag);
         panelPoolN.setEnabled(flag);
         panelPoolS.setEnabled(flag);
         scrollPane.setEnabled(flag);
         contentPane.setEnabled(flag);
         btnAddUserToPool.setEnabled(flag);
     }
     public void gameTimeEnable(boolean flag)
     {
         btnSubmitAnswer.setEnabled(flag);
         pbGame.setEnabled(flag);
         lblGamePic.setEnabled(true);
         rbc1.setEnabled(flag);
         rbc2.setEnabled(flag);
         rbc3.setEnabled(flag);
         rbc4 .setEnabled(flag);
         lblQuest1.setEnabled(flag);
         lblQuest2.setEnabled(flag);
         lblCorrect1 .setEnabled(flag);
         lblCorrect2.setEnabled(flag);
         
         panelGame.setEnabled(flag);
        panelGameW.setEnabled(flag);
        panelGameE.setEnabled(flag);
        panelGameS.setEnabled(flag);
        panelGameN.setEnabled(flag);
     }
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()==btnSignIn)
        {
            if(btnSignIn.getText().equals("Sign in"))
            {
                if(txtLogin.getText()==null||txtLogin.getText().equals("")||txtLogin.getText().equals(" "))
                {
                    JOptionPane.showMessageDialog(null, "Please enter a username to login","AHK - Login Request",JOptionPane.ERROR_MESSAGE);
                }
                else if(txtPW.getText()==null||txtPW.getText().equals("")||txtPW.getText().equals(" "))
                {
                    JOptionPane.showMessageDialog(null, "Please enter a password to login","AHK - Login Request",JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    String uname = txtLogin.getText();
                    String pw = txtPW.getText();
                    if(dbc.usernameExists(uname))
                    {
                        if(dbc.usernameMatchPassword(uname, pw))
                        {
                            if(dbc.checkUserInPool(loggedInUsername))
                            {
                                btnJoinPoolText = "Leave Pool";
                                btnAddUserToPool.setText(btnJoinPoolText);
                            }
                            //poolList = dbc.getPoolList();  
                            loggedInUsername = uname;
                            txtLogin.setEnabled(false);
                            txtPW.setEnabled(false);
                            txtPW.setText(null);
                            btnSignIn.setText("Sign Out");
                            gameTimeEnable(false);
                            gamePoolEnable(true);
                            updatePoolPanel();
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null, "Sorry, the password entered is incorrect!","AHK - Login Request",JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Please enter a valid username","AHK - Login Request",JOptionPane.ERROR_MESSAGE);
                    }
                        
                }            
            }
            else
            {
                loggedInUsername = "";
                txtLogin.setEnabled(true);
                txtPW.setEnabled(true);
                txtPW.setText(null);
                btnSignIn.setText("Sign in");
                gameTimeEnable(false);
                gamePoolEnable(false);                
            }
            
            
        }
    }
    if(e.getSource()==btnRegister)
    {
        Register r = new Register();
        r.registerGUI();
        System.out.println("You clicked the button btnRegister");
    }
}
