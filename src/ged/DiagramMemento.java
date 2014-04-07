/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.io.IOException;

/**
 *
 * @author Comp
 */
public class DiagramMemento
{
  private int hashed_password;
  private Diagram saved_diagram;
  
  public DiagramMemento()
  {
    hashed_password = 0;
    saved_diagram   = null;
  }
  
  public Diagram getState(String password)
  {
    Diagram d = null;
    int hash = password.hashCode();
    if(hashed_password == hash)
      d = saved_diagram;
    else
      System.err.println("DiagramMemento:Illegal Access!");
    
    return d;
  }
  
  public void setState(Diagram d, String password) throws IOException
  {
    hashed_password = password.hashCode();
    saved_diagram = (Diagram)d.cloneElement();
  }
  
}
