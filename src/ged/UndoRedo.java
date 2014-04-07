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
  
  public UndoRedo() throws IOException
  {
    cfg_mgr = ConfigurationManager.getInstance();
    max_undo = Integer.parseInt(
            cfg_mgr.getConfigValue(ConfigurationManager.NUM_UNDO));
    
    undo_queue = new ArrayDeque<>(max_undo);
    redo_queue = new ArrayDeque<>(max_undo);
    password = "undo_redo_password";
  }
  
  public void saveState(Diagram d) throws IOException
  {
    Diagram diagramCopy = (Diagram)d.cloneElement();
    DiagramMemento memento = new DiagramMemento();
    memento.setState(diagramCopy, password);
    
    // If queue is full, remove last element to make space
    if(undo_queue.size() == max_undo)
      undo_queue.removeLast();
    
    // Clear redo queue since a new action has been taken
    redo_queue.clear();
    
    undo_queue.push(memento);
  }
  
  public Diagram undo()
  {
    Diagram diag = null;
    if(undo_queue.size() > 0)
    {
      DiagramMemento mem = undo_queue.pop();
      diag = mem.getState(password);
      
      redo_queue.push(mem);
    }
    
    return diag;
  }
  
  public Diagram redo()
  {
    Diagram diag = null;
    if(redo_queue.size() > 0)
    {
      DiagramMemento mem = redo_queue.pop();
      diag = mem.getState(password);
      
      // If undo queue is fill, remove last to clear room for more
      if(undo_queue.size() == max_undo)
        undo_queue.removeLast();
      undo_queue.push(mem);
    }
    
    return diag;
  }
  
}
