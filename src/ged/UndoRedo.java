/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.io.IOException;
import java.util.ArrayDeque;

/**
 *
 * @author Comp
 */
public class UndoRedo
{
  private final ConfigurationManager cfg_mgr;
  private final int max_undo;
  private final ArrayDeque<DiagramMemento> undo_queue;
  private final ArrayDeque<DiagramMemento> redo_queue;
  private final String password;
  private ClassDiagram latest_diagram;
  
  public UndoRedo(ClassDiagram initialDiagram) throws IOException
  {
    cfg_mgr = ConfigurationManager.getInstance();
    max_undo = Integer.parseInt(
            cfg_mgr.getConfigValue(ConfigurationManager.NUM_UNDO));
    
    undo_queue = new ArrayDeque<>(max_undo);
    redo_queue = new ArrayDeque<>(max_undo);
    password = "undo_redo_password";
    latest_diagram = (ClassDiagram)initialDiagram.cloneElement();
  }
  
  public void saveState(ClassDiagram d) throws IOException
  {
    // Only queue it up if it's different than last time
    if(!d.equivalentTo(latest_diagram))
    {
      ClassDiagram pushDiag = latest_diagram;
      latest_diagram = (ClassDiagram)d.cloneElement();
      
      DiagramMemento memento = new DiagramMemento();
      memento.setState(pushDiag, password);

      // If queue is full, remove last element to make space
      while(undo_queue.size() >= max_undo)
        undo_queue.removeLast();

      // Clear redo queue since a new action has been taken
      redo_queue.clear();

      undo_queue.push(memento);
    }
  }
  
  public ClassDiagram undo(ClassDiagram currentDiag) throws IOException
  {
    saveState(currentDiag);
    if(undo_queue.size() > 0)
    {
      DiagramMemento mem = new DiagramMemento();
      mem.setState(latest_diagram, password);
      redo_queue.push(mem);
      
      mem = undo_queue.pop();
      latest_diagram = mem.getState(password);
    }
    
    return (ClassDiagram)latest_diagram.cloneElement();
  }
  
  public ClassDiagram redo(ClassDiagram currentDiag) throws IOException
  {
    saveState(currentDiag);
    if(redo_queue.size() > 0)
    {
      DiagramMemento mem = new DiagramMemento();
      mem.setState(latest_diagram, password);
      while(undo_queue.size() >= max_undo)
        undo_queue.removeLast();
      undo_queue.push(mem);
      
      mem = redo_queue.pop();
      latest_diagram = mem.getState(password);
    }
    
    return (ClassDiagram)latest_diagram.cloneElement();
  }
  
}
