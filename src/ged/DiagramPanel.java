/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Comp
 */
// Class to handle drawing the diagram
public class DiagramPanel extends JPanel
{
  private Dimension diagram_size;
  private final DiagramController diag_controller;

  public DiagramPanel() throws IOException
  {
    setBorder(BorderFactory.createLineBorder(Color.black));
    diagram_size = new Dimension(25000, 25000);
    diag_controller = DiagramController.getInstance();
  }

  @Override
  public Dimension getPreferredSize()
  {
    return diagram_size;
  }

  @Override
  public void paintComponent(Graphics g)
  {
    // Clear panel
    super.paintComponent(g);

    // Get diagram size
    Dimension d = diag_controller.getDiagramDimension();
    diagram_size = d;
    setSize(diagram_size);

    // draw everything in diagram
    diag_controller.draw(g);
  }
}
