/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahkjavadesktop;

import ahkjavadesktop.AHKJavaDesktop;
import ahkjavadesktop.DBCommunicator;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author mifouche
 */
public class Register implements ActionListener {
    //Register
    JFrame jfR;
    JPanel panelRN, panelRC, panelRS;
    JLabel lblRLogo, lblRUser, lblRPass1, lblPass2, lblEmail;
    JTextField  txtRUser,  txtEmail;
    JPasswordField txtRPass1, txtPass2;
    JButton btnRRegister;
    DBCommunicator dbc = new DBCommunicator();
    
    
    public void registerGUI()
     {
        jfR = new JFrame("Registration - African Heritage King");
        
        panelRN = new JPanel();
        panelRC = new JPanel(new GridLayout(4,2));
        panelRS = new JPanel();
              
        lblRLogo = new JLabel("Logo");
        lblRUser = new JLabel("Username");
        lblRPass1 = new JLabel("Password");
        lblPass2 = new JLabel("Confirm Password");
        lblEmail = new JLabel("Email");
    
        
        txtRUser = new JTextField(10);
        txtRPass1 = new JPasswordField(10);
        txtPass2 = new JPasswordField(10);
        txtEmail = new JTextField(10);
        btnRRegister = new JButton ("Register user");
        btnRRegister.addActionListener(this);
        
        try
         {              
            BufferedImage bi = ImageIO.read(getClass().getResource("ahkMiniLogo.JPG"));
            ImageIcon image = new ImageIcon(bi); 
            lblRLogo = new JLabel(image);
            panelRN.add(lblRLogo );
         }
         catch(Exception e)
         {
             System.out.println("createAHKGui(load image): \n"+e);
         }
        
        
        panelRC.add(lblRUser);
        panelRC.add(txtRUser);
        panelRC.add(lblRPass1);
        panelRC.add(txtRPass1);
        panelRC.add(lblPass2);
        panelRC.add(txtPass2);
        panelRC.add(lblEmail);
        panelRC.add(txtEmail);
        
        panelRS.add(btnRRegister);
        
        jfR.add(panelRN, BorderLayout.NORTH);
        jfR.add(panelRC, BorderLayout.CENTER);
        jfR.add(panelRS, BorderLayout.SOUTH);
        jfR.pack();
        jfR.setVisible(true);
        jfR.setLocationRelativeTo(null);
        jfR.setDefaultCloseOperation(jfR.DISPOSE_ON_CLOSE);
        jfR.setResizable(false);
     }
    
    public void actionPerformed(ActionEvent e)
    {
        
        if(e.getSource() == btnRRegister)
        {
            String uname =txtRUser.getText();
            String pass1 = txtRPass1.getText();
            String pass2 = txtPass2.getText();
            String email = txtEmail.getText();
            if(uname.equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid username","AHK - Register Request",JOptionPane.ERROR_MESSAGE);
            }
            else if(pass1.equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid password","AHK - Register Request",JOptionPane.ERROR_MESSAGE);
            }
            else if(pass2.equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid confirmation password","AHK - Register Request",JOptionPane.ERROR_MESSAGE);
            }
            else if(email.equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter a valid email","AHK - Register Request",JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                boolean errors = false;
                ArrayList<String> listOfErrors;
                listOfErrors = dbc.registerUser(uname, email, pass1, pass2);
                for(int i=0;i<listOfErrors.size();i++)
                {
                    System.out.println(listOfErrors.get(i));
                    switch(listOfErrors.get(i))
                    {
                        case "userNotAdded":
                        {
                            errors = true;
                            JOptionPane.showMessageDialog(null, "The user was not added","AHK - Register",JOptionPane.ERROR_MESSAGE);
                        }break;
                        case "passwordMismatch":
                        {
                            errors = true;
                            JOptionPane.showMessageDialog(null, "The passwords does not match","AHK - Register",JOptionPane.ERROR_MESSAGE);
                        }break;
                        case "passwordInvalid":
                        {
                            errors = true;
                            JOptionPane.showMessageDialog(null, "Please enter a valid password\nA valid password is at least 6 characters long\nand should consist of at least a number, and a letter","AHK - Register",JOptionPane.ERROR_MESSAGE);
                            //password moet 6 characters long wees en bestaan uit lowercase letters en numbers
                        }break;
                        case "emailInvalid":                            
                        {
                            errors = true;
                            JOptionPane.showMessageDialog(null, "Please enter a valid email address","AHK - Register",JOptionPane.ERROR_MESSAGE);
                           /* . die email moet begin met _, A-Z(upper of lower), 0-9, na dit optionally n "." en a-z, 0-9, moet @ he, 
                            dan weer tussen a-z0-9, optional "." en a-z,0-9 en dan ".", a-z,0-9 met min length of 2 */
                        }break;
                        case "emailExists":
                        {
                            errors = true;
                            JOptionPane.showMessageDialog(null, "The email is already registered to a user","AHK - Register",JOptionPane.ERROR_MESSAGE);
                        }break;
                        case "UserExists":
                        {
                            errors = true;
                            JOptionPane.showMessageDialog(null, "This username is already in use","AHK - Register",JOptionPane.ERROR_MESSAGE);
                        }break;
                        default: ;
                        break;
                    }
                }
                if(!errors)
                {
                    jfR.dispose(); 
                }
               
                /*RegisterUser();String errors[]      (this is also used in login)
                -usernameExists(username);boolean
                -emailExists(email);boolean
                -passwordMatch(password1, password2);boolean
                -passwordValid(password);boolean
                -addUser(username, email, password);boolean(for successful adding)
                */
                //dbc.usernameExists()
            }
            
        }
    }
}
