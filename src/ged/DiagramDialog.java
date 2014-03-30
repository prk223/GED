/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.Timer;

/**
 *
 * @author Comp
 */
public class DiagramDialog extends javax.swing.JDialog
{
  private final ConfigurationManager cfg_mgr;
  private final DiagramController diag_controller;
  private Timer save_timer;
  private DiagramPanel diag_panel;
  private DiagramState state;
  
  
  // Class to handle drawing the diagram
  public class DiagramPanel extends JPanel
  {
    Dimension diagram_size;
    
    public DiagramPanel()
    {
      setBorder(BorderFactory.createLineBorder(Color.black));
      diagram_size = new Dimension(25000, 25000);
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
      
      // Draw anything state specific
      state.draw(g);
    }
  }

  /**
   * Creates new form DiagramDialog
   * @param parent
   * @param modal
   * @throws java.io.IOException
   */
  public DiagramDialog(java.awt.Frame parent, boolean modal) throws IOException
  {
    super(parent, modal);
    initComponents();
    initDrawspaceComponents();
    
    // Force save message to be on top
    this.getContentPane().setComponentZOrder(SaveLabel, 0);
    
    cfg_mgr = ConfigurationManager.getInstance();
    int saveTimeoutMs = Integer.parseInt(
            cfg_mgr.getConfigValue(ConfigurationManager.MSG_TIMEOUT));
    
    setVisible(false);  
    setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().
            getMaximumWindowBounds());
    SaveLabel.setVisible(false);
    
    diag_controller = DiagramController.getInstance();
    
    // Set up a timer to make save messages disappear
    save_timer = new Timer(saveTimeoutMs, new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e)
      {
        SaveLabel.setVisible(false);
        save_timer.stop();
      }
    });
    
    state = new SelectDiagramState(DiagramScrollPane.getViewport());
  }
  
  private void initDrawspaceComponents()
  {
    diag_panel = new DiagramPanel();
    DiagramScrollPane.setViewportView(diag_panel);
    diag_panel.setFocusable(true);
    diag_panel.requestFocusInWindow();
    
    diag_panel.addMouseListener(new java.awt.event.MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent evt)
      {
        if((evt.getClickCount() > 1) && (evt.getButton() == MouseEvent.BUTTON1))
          DiagPanelMouseDoubleClicked(evt);
      }
      @Override
      public void mouseEntered(java.awt.event.MouseEvent evt)
      {
        DiagPanelMouseEntered(evt);
      }
      @Override
      public void mouseExited(java.awt.event.MouseEvent evt)
      {
        DiagPanelMouseExited(evt);
      }
      @Override
      public void mousePressed(MouseEvent evt)
      {
        try
        {
          if(evt.getButton() == MouseEvent.BUTTON1)
            DiagPanelMousePressed(evt);
          else if(evt.getButton() == MouseEvent.BUTTON3)
            DiagPanelRightMousePressed(evt);
        }
        catch (IOException ex)
        {
          Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      @Override
      public void mouseReleased(MouseEvent evt)
      {
        if(evt.getButton() == MouseEvent.BUTTON1)
          DiagPanelMouseReleased(evt);
      }
    });
    diag_panel.addMouseMotionListener(new java.awt.event.MouseAdapter()
    {
      @Override
      public void mouseMoved(MouseEvent evt)
      {
        DiagPanelMouseMoved(evt);
      }
      @Override
      public void mouseDragged(java.awt.event.MouseEvent evt)
      {
        DiagPanelMouseDragged(evt);
      }
    });
    
    diag_panel.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.
            getKeyStroke(KeyEvent.VK_DELETE, 0), "DeleteKey");
    diag_panel.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.
            getKeyStroke(KeyEvent.VK_DECIMAL, 0), "DecimalKey");
    diag_panel.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.
            getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "BackSpaceKey");

    Action deleteAction = new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        try
        {
          DiagPanelDeleteKeyPressed();
        }
        catch (IOException ex)
        {
          Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    };
    diag_panel.getActionMap().put("DeleteKey", deleteAction);
    diag_panel.getActionMap().put("DecimalKey", deleteAction);
    diag_panel.getActionMap().put("BackSpaceKey", deleteAction);
  }
  
  private void DiagPanelMouseDoubleClicked(MouseEvent evt)
  {
    state = state.mouseDoubleClicked(evt);
    diag_panel.repaint();
  }
  
  private void DiagPanelMouseMoved(MouseEvent evt)
  {
    state = state.mouseMoved(evt);
    diag_panel.repaint();
  }
  
  private void DiagPanelMouseEntered(MouseEvent evt)
  {
    state = state.mouseEntered(evt);
    diag_panel.repaint();
  }
  
  private void DiagPanelMouseExited(MouseEvent evt)
  {
    state = state.mouseExited(evt);
    diag_panel.repaint();
  }
  
  private void DiagPanelMouseDragged(MouseEvent evt)
  {
    state = state.mouseDragged(evt);
    diag_panel.repaint();
  }
  
  private void DiagPanelMousePressed(MouseEvent evt)
  {
    state = state.mousePressed(evt);
    diag_panel.repaint();
  }
  
  private void DiagPanelMouseReleased(MouseEvent evt)
  {
    state = state.mouseReleased(evt);
    diag_panel.repaint();
  }
  
  private void DiagPanelRightMousePressed(MouseEvent evt) throws IOException
  {
    state = state.mouseRightClicked(evt);
    diag_panel.repaint();
  }
  
  private void DiagPanelDeleteKeyPressed() throws IOException
  {
    state = state.delete();
    diag_panel.repaint();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents()
  {

    SaveLabel = new javax.swing.JLabel();
    AddClassBtn = new javax.swing.JButton();
    AddInheritanceBtn = new javax.swing.JButton();
    SelectBtn = new javax.swing.JButton();
    DiagramScrollPane = new javax.swing.JScrollPane();
    AddAggregationBtn = new javax.swing.JButton();
    DiagramMenuBar = new javax.swing.JMenuBar();
    DiagramFileMenu = new javax.swing.JMenu();
    DiagramSaveItem = new javax.swing.JMenuItem();
    DiagramCloseItem = new javax.swing.JMenuItem();

    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    setModal(true);
    setName("Diagram"); // NOI18N
    setUndecorated(true);

    SaveLabel.setLabelFor(DiagramScrollPane);
    SaveLabel.setText("SaveMsg");
    SaveLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
    SaveLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    SaveLabel.setMinimumSize(new java.awt.Dimension(2000000, 14));
    SaveLabel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

    AddClassBtn.setText("Class");
    AddClassBtn.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        AddClassBtnMouseClicked(evt);
      }
    });

    AddInheritanceBtn.setText("Inheritance");
    AddInheritanceBtn.setMaximumSize(new java.awt.Dimension(57, 23));
    AddInheritanceBtn.setMinimumSize(new java.awt.Dimension(57, 23));
    AddInheritanceBtn.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        AddInheritanceBtnMouseClicked(evt);
      }
    });

    SelectBtn.setText("Select");
    SelectBtn.setMaximumSize(new java.awt.Dimension(57, 23));
    SelectBtn.setMinimumSize(new java.awt.Dimension(57, 23));
    SelectBtn.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        SelectBtnMouseClicked(evt);
      }
    });

    DiagramScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DiagramTitle", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Verdana", 0, 24), java.awt.Color.blue)); // NOI18N

    AddAggregationBtn.setText("Aggregation");
    AddAggregationBtn.setMaximumSize(new java.awt.Dimension(57, 23));
    AddAggregationBtn.setMinimumSize(new java.awt.Dimension(57, 23));
    AddAggregationBtn.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        AddAggregationBtnMouseClicked(evt);
      }
    });

    DiagramFileMenu.setText("File");

    DiagramSaveItem.setText("Save");
    DiagramSaveItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        DiagramSaveItemActionPerformed(evt);
      }
    });
    DiagramFileMenu.add(DiagramSaveItem);

    DiagramCloseItem.setText("Close");
    DiagramCloseItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        DiagramCloseItemActionPerformed(evt);
      }
    });
    DiagramFileMenu.add(DiagramCloseItem);

    DiagramMenuBar.add(DiagramFileMenu);

    setJMenuBar(DiagramMenuBar);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
          .addComponent(SelectBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(AddInheritanceBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
          .addComponent(AddClassBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(AddAggregationBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(DiagramScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE))
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(SaveLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 667, Short.MAX_VALUE)
          .addContainerGap()))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(32, 32, 32)
        .addComponent(AddClassBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(AddInheritanceBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(AddAggregationBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(SelectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(175, Short.MAX_VALUE))
      .addComponent(DiagramScrollPane)
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
          .addContainerGap(459, Short.MAX_VALUE)
          .addComponent(SaveLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addContainerGap()))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void DiagramSaveItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_DiagramSaveItemActionPerformed
  {//GEN-HEADEREND:event_DiagramSaveItemActionPerformed
    boolean saved = diag_controller.saveDiagram();
    String saveMsg;
    if(saved)
      saveMsg = "Successfully saved diagram: ";
    else
      saveMsg = "FAILED TO SAVE DIAGRAM: ";
    saveMsg += diag_controller.getOpenDiagramName();
    SaveLabel.setText(saveMsg);
    SaveLabel.setVisible(true);
    
    save_timer.stop();
    save_timer.start();
  }//GEN-LAST:event_DiagramSaveItemActionPerformed

  private void DiagramCloseItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_DiagramCloseItemActionPerformed
  {//GEN-HEADEREND:event_DiagramCloseItemActionPerformed
    try
    {
      close();
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_DiagramCloseItemActionPerformed

  int x = 0;
  int y = 0;
  private void AddClassBtnMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_AddClassBtnMouseClicked
  {//GEN-HEADEREND:event_AddClassBtnMouseClicked
    try
    {
      state = state.addClassBtnClicked(evt);
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
    diag_panel.repaint();
  }//GEN-LAST:event_AddClassBtnMouseClicked

  private void SelectBtnMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_SelectBtnMouseClicked
  {//GEN-HEADEREND:event_SelectBtnMouseClicked
    try
    {
      state = state.selectBtnClicked(evt);
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
    diag_panel.repaint();
  }//GEN-LAST:event_SelectBtnMouseClicked

  private void AddInheritanceBtnMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_AddInheritanceBtnMouseClicked
  {//GEN-HEADEREND:event_AddInheritanceBtnMouseClicked
    try
    {
      state = state.addInheritanceBtnClicked(evt);
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
    diag_panel.repaint();
  }//GEN-LAST:event_AddInheritanceBtnMouseClicked

  private void AddAggregationBtnMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_AddAggregationBtnMouseClicked
  {//GEN-HEADEREND:event_AddAggregationBtnMouseClicked
    try
    {
      state = state.addAggregationBtnClicked(evt);
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
    diag_panel.repaint();
  }//GEN-LAST:event_AddAggregationBtnMouseClicked

  public void open(String diagram)
  {
    boolean opened = diag_controller.openDiagram(diagram);
    if(opened)
    {
      TitledBorder paneBorder = (TitledBorder)DiagramScrollPane.getBorder();
      paneBorder.setTitle(diagram);
      setVisible(true);
    }
  }
  
  public void close() throws IOException
  {
    diag_controller.closeDiagram();
    state = new SelectDiagramState(DiagramScrollPane.getViewport());
    setVisible(false);
  }
  /**
   * @param args the command line arguments
   */
  public static void main(String args[])
  {
    /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try
    {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
      {
        if ("Nimbus".equals(info.getName()))
        {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    }
    catch (ClassNotFoundException ex)
    {
      java.util.logging.Logger.getLogger(DiagramDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    catch (InstantiationException ex)
    {
      java.util.logging.Logger.getLogger(DiagramDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    catch (IllegalAccessException ex)
    {
      java.util.logging.Logger.getLogger(DiagramDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    catch (javax.swing.UnsupportedLookAndFeelException ex)
    {
      java.util.logging.Logger.getLogger(DiagramDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
        //</editor-fold>

    /* Create and display the dialog */
    java.awt.EventQueue.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          DiagramDialog dialog = new DiagramDialog(new javax.swing.JFrame(), true);
          dialog.addWindowListener(new java.awt.event.WindowAdapter()
          {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e)
            {
              System.exit(0);
            }
          });
          dialog.setVisible(true);
        }
        catch (IOException ex)
        {
          Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton AddAggregationBtn;
  private javax.swing.JButton AddClassBtn;
  private javax.swing.JButton AddInheritanceBtn;
  private javax.swing.JMenuItem DiagramCloseItem;
  private javax.swing.JMenu DiagramFileMenu;
  private javax.swing.JMenuBar DiagramMenuBar;
  private javax.swing.JMenuItem DiagramSaveItem;
  private javax.swing.JScrollPane DiagramScrollPane;
  private javax.swing.JLabel SaveLabel;
  private javax.swing.JButton SelectBtn;
  // End of variables declaration//GEN-END:variables
}
