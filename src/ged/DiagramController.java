/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

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
    diag.save(filePath);
    
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
  
  public void closeDiagram()
  {
    cur_diagram = null;
  }
  
  public boolean openDiagram(String diagName)
  {
    boolean openedSuccessfully = false;
    
    // Close diagram if one is already open
    closeDiagram();
    
    String workspacePath = cfg_mgr.getConfigValue(varWsPath);
    String path = workspacePath + "\\" + diagName + DIAG_EXTENSION;
    cur_diagram = Diagram.loadDiagram(path);
    if(cur_diagram != null)
      openedSuccessfully = true;
    
    return openedSuccessfully;
  }
  
  public boolean saveDiagram()
  {
    boolean success = false;
    
    if(cur_diagram != null)
    {
      String workspacePath = cfg_mgr.getConfigValue(varWsPath);
      String diagName = cur_diagram.getName();
      String fileName = diagName + DIAG_EXTENSION;
      String filePath = workspacePath + "\\" + fileName;
      success = cur_diagram.save(filePath);
    }
    return success;
  }
  
  public String getOpenDiagramName()
  {
    String diagName = "";
    if(cur_diagram != null)
      diagName = cur_diagram.getName();
    
    return diagName;
  }
  
  public Diagram getOpenDiagram()
  {
    return cur_diagram;
  }
  
  public void addClass(int x, int y) throws IOException
  {
    if(cur_diagram != null)
    {
      DiagramElement e = new ClassElement("", x, y);
      cur_diagram.addElement(e);
    }
  }
  
  public void addRelationship(int x, int y, RelationshipType type) throws IOException
  {
    if(cur_diagram != null)
    {
      DiagramElement e = new Relationship(type, x, y);
      cur_diagram.addElement(e);
    }
  }
  
  public void addDiagramElement(DiagramElement e)
  {
    if(cur_diagram != null)
    {
      cur_diagram.addElement(e);
    }
  }
  
  public void draw(Graphics g)
  {
    if(cur_diagram != null)
    {
      cur_diagram.draw(g);
    }
  }
  
  public Dimension getDiagramDimension()
  {
    Dimension d = new Dimension(25000, 25000);
    if(cur_diagram != null)
    {
      int x = cur_diagram.getMaxX();
      int y = cur_diagram.getMaxY();
      d.setSize(new Dimension(x, y));
    }
    
    return d;
  }
  
}
