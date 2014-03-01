/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Comp
 */
public class Diagram extends DiagramElement
{
  private final String name;
  
  public Diagram(String diagName)
  {
    super(0, 0);
    name = diagName;
  }
  
  public Diagram(String diagName, int x, int y)
  {
    super(x, y);
    name = diagName;
  }
  
  public String getName()
  {
    return name;
  }
  
  public boolean save(String filePath)
  {
    boolean success = false;
    
    try (PrintWriter outFile = new PrintWriter(filePath))
    {
      outFile.println(name);
      // TODO: Write out elements
      outFile.close();
      success = true;
    }
    catch(FileNotFoundException ex)
    {
      System.err.println("ERROR:File failed to open:" + filePath);
    }
    
    return success;
  }

  public static Diagram loadDiagram(String filePath)
  {
    Diagram loadedDiagram = null;
    File diagFile = new File(filePath);
    if(diagFile.exists())
    {
      try (BufferedReader diagRdr = 
              new BufferedReader(new FileReader(diagFile)))
      {
        String name = diagRdr.readLine();
        
        loadedDiagram = new Diagram(name);
        String diagramName = diagRdr.readLine();
        // TODO: Read in elements
        diagRdr.close();
      }
      catch(FileNotFoundException ex)
      {
        System.err.println("Diagram:loadDiagram:ERROR:Unable to read file" 
                + filePath);
      }
      catch(IOException ex)
      {
        System.err.println("Diagram:loadDiagram:ERROR:IO Error:" + filePath);
      }
    }
    
    return loadedDiagram;
  }
  
}
