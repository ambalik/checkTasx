package business;

import java.io.*;
import java.util.regex.*;

/** 
 * This class encapsulate some common methods used throughout the development.
 * 
 * @author Johan Karlsson
 */
public class IPUtils
{
  /** Make the code safe for HTML injections (inspired by SDN code) */
  public static String checkStringForHTML(String testString) 
  {
    // Create a pattern to match <>
    Pattern p = Pattern.compile("<.*>");

    // Create a matcher with an input string
    Matcher m = p.matcher(testString);
    StringBuffer sb = new StringBuffer();
    boolean test = m.find();

    // Loop through and create a new String with the replacements
    while (test) {
      m.appendReplacement(sb, "CENSUR");
      test = m.find();
    }

    // Add the last segment of input to the new String
    m.appendTail(sb);

    // Convert the buffer to String
    String returnValue = new String(sb);
    return returnValue;
  }
  
  /** This method saves a file on the local file system */
  public static void saveFile(File file, String absolutePath) throws IOException 
  {
    FileInputStream fin = null;
    FileOutputStream fos = null;
    try
    {
      // Store the file in the img folder.
      fin = new FileInputStream(file);
      fos = new FileOutputStream(new File(absolutePath));
      byte[] buffer = new byte[1024];
      int len = fin.read(buffer);
      while (len != -1) 
      {
        fos.write(buffer, 0, len);
        len = fin.read(buffer);
      }
    }
    finally 
    {
      try { fin.close(); fos.close(); } catch (Exception exc) { }  
    } 
  }
  
  /** This method deletes the specified file that resides on the servers file system */
  public static void deleteFile(String fileName) throws IllegalArgumentException
  {
    File f = new File(fileName);
    
    // Throw IllegalArgumentException if the process fails. 
    // Internal error that the user do not care about.
    if (!f.exists())
    {
      // If the file doesn't exist we throw exception and display error page.
      throw new IllegalArgumentException();
    }
    else if (!f.canWrite()) 
    {
      throw new IllegalArgumentException();
    }
    else if(f.isDirectory()) 
    {
      // If it is a directory, something went wrong, really wrong.
      throw new IllegalArgumentException();
    }
      
    // Delete the file
    boolean success = f.delete();

    if(!success)
    {
      // Write to log file, since this have nothing to do with the user. Hopefully.
    }
  }
}
