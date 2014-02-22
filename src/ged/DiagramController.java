/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Comp
 */
public class DiagramController
{
  private static DiagramController instance = null;
  private ConfigurationManager cfg_mgr = null;
  private static final String DIAG_EXTENSION = ".dgm";
  private final String varWsPath = ConfigurationManager.WORKSPACE_PATH;
  private Diagram cur_diagram;
  
  
  private DiagramController() throws IOException
  {
    cfg_mgr = ConfigurationManager.getInstance();
  }
  
  public static DiagramController getInstance() throws IOException
  {
    if(instance == null)
    {
      instance = new DiagramController();
    }
    
    return instance;
  }
  
  public Diagram createDiagram(String diagName) throws IOException
  {
    Diagram diag = new Diagram(diagName);
    String workspacePath = cfg_mgr.getConfigValue(varWsPath);
    String fileName = diagName + DIAG_EXTENSION;
    String filePath = workspacePath + "\\" + fileName;
    try (PrintWriter outFile = new PrintWriter(filePath))
    {
      outFile.println(diagName);
      outFile.close();
    }
    catch(FileNotFoundException ex)
    {
      System.err.println("ERROR:File failed to open:" + filePath);
    }
    
    return diag;
  }
  
  public void deleteDiagram(String diagName)
  {
    String workspacePath = cfg_mgr.getConfigValue(varWsPath);
    String path = workspacePath + "\\" + diagName + DIAG_EXTENSION;
    File diagFile = new File(path);
    if(!diagFile.delete())
      System.out.println("Unable to delete diagram:" + diagName);
  }
  
}
