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
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
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
  private final SelectDiagramState select_state;
  private final ClassDiagramState class_state;
  private ArrayList<DiagramState> states;
  
  // Class to handle drawing the diagram
  public class DiagramPanel extends JPanel
  {
    public DiagramPanel()
    {
      setBorder(BorderFactory.createLineBorder(Color.black));
    }
    
    @Override
    public Dimension getPreferredSize()
    {
      return new Dimension(25000, 25000);
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
      // Clear panel
      super.paintComponent(g);
      
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
    
    select_state = new SelectDiagramState();
    class_state  = new ClassDiagramState();
    state = select_state;
    states = new ArrayList<>();
    states.add(select_state);
    states.add(class_state);
  }
  
  private void initDrawspaceComponents()
  {
    diag_panel = new DiagramPanel();
    DiagramScrollPane.setViewportView(diag_panel);
    
    diag_panel.addMouseListener(new java.awt.event.MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent evt)
      {
        DiagPanelMouseClicked(evt);
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
        DiagPanelMousePressed(evt);
      }
      @Override
      public void mouseReleased(MouseEvent evt)
      {
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
  }
  
  private void DiagPanelMouseClicked(MouseEvent evt)
  {
    state.mouseClicked(evt);
    if(state != select_state)
    {
      state = select_state;
      state.reset();
    }
    state.mouseEntered(evt);
    diag_panel.repaint();
  }
  
  private void DiagPanelMouseMoved(MouseEvent evt)
  {
    state.mouseMoved(evt);
    diag_panel.repaint();
  }
  
  private void DiagPanelMouseEntered(MouseEvent evt)
  {
    state.mouseEntered(evt);
    diag_panel.repaint();
  }
  
  private void DiagPanelMouseExited(MouseEvent evt)
  {
    state.mouseExited(evt);
    diag_panel.repaint();
  }
  
  private void DiagPanelMouseDragged(MouseEvent evt)
  {
    state.mouseDragged(evt);
    diag_panel.repaint();
  }
  
  private void DiagPanelMousePressed(MouseEvent evt)
  {
    state.mousePressed(evt);
    diag_panel.repaint();
  }
  
  private void DiagPanelMouseReleased(MouseEvent evt)
  {
    state.mouseReleased(evt);
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
    AddRelationshipBtn = new javax.swing.JButton();
    AddSelectBtn = new javax.swing.JButton();
    DiagramScrollPane = new javax.swing.JScrollPane();
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

    AddRelationshipBtn.setText("Relationship");
    AddRelationshipBtn.setMaximumSize(new java.awt.Dimension(57, 23));
    AddRelationshipBtn.setMinimumSize(new java.awt.Dimension(57, 23));

    AddSelectBtn.setText("Select");
    AddSelectBtn.setMaximumSize(new java.awt.Dimension(57, 23));
    AddSelectBtn.setMinimumSize(new java.awt.Dimension(57, 23));
    AddSelectBtn.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        AddSelectBtnMouseClicked(evt);
      }
    });

    DiagramScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DiagramTitle", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Verdana", 0, 24), java.awt.Color.blue)); // NOI18N

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
          .addComponent(AddSelectBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(AddRelationshipBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
          .addComponent(AddClassBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        .addComponent(AddRelationshipBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(AddSelectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(247, Short.MAX_VALUE))
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
    close();
  }//GEN-LAST:event_DiagramCloseItemActionPerformed

  int x = 0;
  int y = 0;
  private void AddClassBtnMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_AddClassBtnMouseClicked
  {//GEN-HEADEREND:event_AddClassBtnMouseClicked
    state = class_state;
    state.reset();
    state.mouseExited(evt);
    diag_panel.repaint();
  }//GEN-LAST:event_AddClassBtnMouseClicked

  private void AddSelectBtnMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_AddSelectBtnMouseClicked
  {//GEN-HEADEREND:event_AddSelectBtnMouseClicked
    state = select_state;
    state.reset();
    state.mouseExited(evt);
    diag_panel.repaint();
  }//GEN-LAST:event_AddSelectBtnMouseClicked

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
  
  public void close()
  {
    diag_controller.closeDiagram();
    Iterator<DiagramState> it = states.iterator();
    while(it.hasNext())
    {
      DiagramState ds = it.next();
      ds.reset();
    }
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
  private javax.swing.JButton AddClassBtn;
  private javax.swing.JButton AddRelationshipBtn;
  private javax.swing.JButton AddSelectBtn;
  private javax.swing.JMenuItem DiagramCloseItem;
  private javax.swing.JMenu DiagramFileMenu;
  private javax.swing.JMenuBar DiagramMenuBar;
  private javax.swing.JMenuItem DiagramSaveItem;
  private javax.swing.JScrollPane DiagramScrollPane;
  private javax.swing.JLabel SaveLabel;
  // End of variables declaration//GEN-END:variables
}
