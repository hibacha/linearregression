package cs6140.hw3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
/**
 * 
 * @author zhouyf
 *
 */
public class PersistRandomizedHelper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6867380222361956389L;
	
	public static final String PATH ="/Users/zhouyf/address.ser";
	
	/**
	 * 
	 * @param randomizedListOfEmails
	 */
	public  void serilize(ArrayList<Email> randomizedListOfEmails) {
		
		try {
			FileOutputStream fout = new FileOutputStream(PATH);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(randomizedListOfEmails);
			oos.close();
			System.out.println("Done");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * 
	 * @param path
	 * @return
	 */
	public  ArrayList<Email> deserialize(String path)
    {

		ArrayList<Email> randomSetFromFile = null;

        FileInputStream fis = null;
        ObjectInputStream in = null;

        try
        {
            fis = new FileInputStream(path);
            in = new ObjectInputStream(fis);
            randomSetFromFile = (ArrayList<Email>)in.readObject();
            in.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        catch(ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }  
        
        return randomSetFromFile;
    }

}
