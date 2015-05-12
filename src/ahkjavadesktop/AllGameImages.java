/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahkjavadesktop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author mifouche
 */
public class AllGameImages {
    ArrayList<String> allImagesForGame = new ArrayList();
    
    public AllGameImages()
    {
        
    }
    
    public ArrayList getAllImages() throws IOException
     {
         try
         {
            allImagesForGame.clear();
            String path = "./src/ahkjavadesktop/resources/images"; 

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
             System.out.println("Get all images"+e +"\n");
             e.printStackTrace();
         }
         System.out.println("Images size in  all game images class: "+allImagesForGame.size());
         return allImagesForGame;
     }
}
