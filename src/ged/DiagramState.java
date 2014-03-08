/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

/**
 *
 * @author Comp
 */
public interface DiagramState
{
  public void mouseClicked(MouseEvent evt);
  
  public void mouseDoubleClicked(MouseEvent evt);
  
  public void mouseDragged(MouseEvent evt);
  
  public void mouseMoved(MouseEvent evt);
  
  public void mouseEntered(MouseEvent evt);
  
  public void mouseExited(MouseEvent evt);
  
  public void mousePressed(MouseEvent evt);
  
  public void mouseReleased(MouseEvent evt);
  
  public void draw(Graphics g);
  
  public void reset();
}
