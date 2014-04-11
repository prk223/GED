/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JViewport;

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
  private UndoRedo undo_redo;
  private DiagramPanel diag_panel;
  private JViewport view_port;
  private DiagramState state;
  
  
  private DiagramController() throws IOException
  {
    cfg_mgr = ConfigurationManager.getInstance();
    diag_panel = null;
    view_port = null;
    state = null;
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
  
  public void closeDiagram() throws IOException
  {
    cur_diagram = null;
    state = new SelectDiagramState(view_port);
  }
  
  public boolean openDiagram(String diagName) throws IOException
  {
    boolean openedSuccessfully = false;
    
    // Close diagram if one is already open
    closeDiagram();
    
    String workspacePath = cfg_mgr.getConfigValue(varWsPath);
    String path = workspacePath + "\\" + diagName + DIAG_EXTENSION;
    cur_diagram = Diagram.loadDiagram(path);
    if(cur_diagram != null)
    {
      openedSuccessfully = true;
      undo_redo = new UndoRedo(cur_diagram);
    }
    
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
  
  public void addRelationship(int x, int y) throws IOException
  {
    if(cur_diagram != null)
    {
      DiagramElement e = new Relationship(x, y);
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
  
  public void removeDiagramElement(DiagramElement e)
  {
    if(cur_diagram != null)
    {
      cur_diagram.alertDestroyedElement(e);
      Iterator<DiagramElement> elIt = cur_diagram.getElements().iterator();
      while(elIt.hasNext())
      {
        DiagramElement element = elIt.next();
        element.alertDestroyedElement(e);
      }
    }
  }
  
  public void draw(Graphics g)
  {
    if(cur_diagram != null)
    {
      cur_diagram.draw(g);

      // Draw anything state specific
      state.draw(g);
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
  
  public ArrayList<DiagramElement> getDiagramElements()
  {
    ArrayList<DiagramElement> elements = new ArrayList<>();
    if(cur_diagram != null)
      elements = cur_diagram.getElements();
    
    return elements;
  }
  
  public DiagramElement getUniqueElement(int uniqueId)
  {
    DiagramElement e = null;
    
    if(cur_diagram != null)
    {
      ArrayList<DiagramElement> elements = cur_diagram.getElements();
      Iterator<DiagramElement> itEl = elements.iterator();
      while(itEl.hasNext())
      {
        DiagramElement element = itEl.next();
        if(element.getUniqueId() == uniqueId)
        {
          e = element;
          break;
        }
      }
    }
    
    return e;
  }
  
  public void undoLastChange() throws IOException
  {
    if(cur_diagram != null)
    {
      Diagram undoneDiag = undo_redo.undo();
      if(undoneDiag != null)
        cur_diagram = undoneDiag;
      diag_panel.repaint();
    }
  }
  
  public void redoLastChange() throws IOException
  {
    if(cur_diagram != null)
    {
      Diagram redoneDiag = undo_redo.redo();
      if(redoneDiag != null)
        cur_diagram = redoneDiag;
      diag_panel.repaint();
    }
  }
  
  public void setupDiagramPanel(DiagramPanel p, JViewport v) throws IOException
  {
    diag_panel = p;
    view_port  = v;
    state = new SelectDiagramState(v);
  }
  
  
  public void mouseDoubleClicked(MouseEvent evt) throws IOException
  {
    if(cur_diagram != null)
    {
      undo_redo.saveState(cur_diagram);
      state = state.mouseDoubleClicked(evt);
      diag_panel.repaint();
    }
  }
  
  public void mouseMoved(MouseEvent evt)
  {
    state = state.mouseMoved(evt);
    diag_panel.repaint();
  }
  
  public void mouseEntered(MouseEvent evt)
  {
    state = state.mouseEntered(evt);
    diag_panel.repaint();
  }
  
  public void mouseExited(MouseEvent evt)
  {
    state = state.mouseExited(evt);
    diag_panel.repaint();
  }
  
  public void mouseDragged(MouseEvent evt)
  {
    state = state.mouseDragged(evt);
    diag_panel.repaint();
  }
  
  public void mousePressed(MouseEvent evt) throws IOException
  {
    if(cur_diagram != null)
    {
      undo_redo.saveState(cur_diagram);
      state = state.mousePressed(evt);
      diag_panel.repaint();
    }
  }
  
  public void mouseReleased(MouseEvent evt) throws IOException
  {
    if(cur_diagram != null)
    {
      undo_redo.saveState(cur_diagram);
      state = state.mouseReleased(evt);
      diag_panel.repaint();
    }
  }
  
  public void rightMousePressed(MouseEvent evt) throws IOException
  {
    if(cur_diagram != null)
    {
      undo_redo.saveState(cur_diagram);
      state = state.mouseRightClicked(evt);
      diag_panel.repaint();
    }
  }
  
  public void deleteKeyPressed() throws IOException
  {
    if(cur_diagram != null)
    {
      undo_redo.saveState(cur_diagram);
      state = state.delete();
      diag_panel.repaint();
    }
  }
  
  public void prepAddClass() throws IOException
  {
    state = state.addClassBtnClicked();
    diag_panel.repaint();
  }
  
  public void prepAddInheritance() throws IOException
  {
    state = state.addInheritanceBtnClicked();
    diag_panel.repaint();
  }
  
  public void prepAddAggregation() throws IOException
  {
    state = state.addAggregationBtnClicked();
    diag_panel.repaint();
  }
  
  public void prepAddAssociation() throws IOException
  {
    state = state.addAssociationBtnClicked();
    diag_panel.repaint();
  }
  
  public void selectElements() throws IOException
  {
    state = state.selectBtnClicked();
    diag_panel.repaint();
  }
  
}
