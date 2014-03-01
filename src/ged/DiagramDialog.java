/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.TitledBorder;
import javax.swing.Timer;

/**
 *
 * @author Comp
 */
public class DiagramDialog extends javax.swing.JDialog
{
  private final DiagramController diag_controller;
  private Timer save_timer;

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
    setVisible(false);  
    setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
    SaveLabel.setVisible(false);
    
    diag_controller = DiagramController.getInstance();
    
    // Set up a timer to make save messages disappear
    save_timer = new Timer(5000, new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e)
      {
        SaveLabel.setVisible(false);
        save_timer.stop();
      }
    });
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

    AddClassBtn = new javax.swing.JButton();
    DiagramScrollPane = new javax.swing.JScrollPane();
    AddAssociationBtn = new javax.swing.JButton();
    AddAggregationBtn = new javax.swing.JButton();
    AddInterfaceBtn = new javax.swing.JButton();
    AddSelectBtn = new javax.swing.JButton();
    SaveLabel = new javax.swing.JLabel();
    DiagramMenuBar = new javax.swing.JMenuBar();
    DiagramFileMenu = new javax.swing.JMenu();
    DiagramSaveItem = new javax.swing.JMenuItem();
    DiagramCloseItem = new javax.swing.JMenuItem();

    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    setModal(true);
    setName("Diagram"); // NOI18N
    setUndecorated(true);

    AddClassBtn.setText("Class");

    DiagramScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DiagramTitle", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Verdana", 0, 24), java.awt.Color.blue)); // NOI18N

    AddAssociationBtn.setText("Association");
    AddAssociationBtn.setMaximumSize(new java.awt.Dimension(57, 23));
    AddAssociationBtn.setMinimumSize(new java.awt.Dimension(57, 23));

    AddAggregationBtn.setText("Aggregation");
    AddAggregationBtn.setMaximumSize(new java.awt.Dimension(57, 23));
    AddAggregationBtn.setMinimumSize(new java.awt.Dimension(57, 23));

    AddInterfaceBtn.setText("Interface");
    AddInterfaceBtn.setMaximumSize(new java.awt.Dimension(57, 23));
    AddInterfaceBtn.setMinimumSize(new java.awt.Dimension(57, 23));

    AddSelectBtn.setText("Select");
    AddSelectBtn.setMaximumSize(new java.awt.Dimension(57, 23));
    AddSelectBtn.setMinimumSize(new java.awt.Dimension(57, 23));

    SaveLabel.setLabelFor(DiagramScrollPane);
    SaveLabel.setText("SaveMsg");
    SaveLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
    SaveLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    SaveLabel.setMinimumSize(new java.awt.Dimension(2000000, 14));
    SaveLabel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

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
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(AddAssociationBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(AddClassBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(AddAggregationBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(AddInterfaceBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(AddSelectBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(DiagramScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE))
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
        .addComponent(AddClassBtn)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(AddAssociationBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(AddAggregationBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(AddInterfaceBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(AddSelectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(32, 220, Short.MAX_VALUE))
      .addComponent(DiagramScrollPane)
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
          .addContainerGap(386, Short.MAX_VALUE)
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
      saveMsg = "Successfully saved ";
    else
      saveMsg = "Failed to save ";
    saveMsg += diag_controller.getOpenDiagramName();
    SaveLabel.setText(saveMsg);
    SaveLabel.setVisible(true);
    
    if(save_timer != null) save_timer.stop();
    save_timer.start();
  }//GEN-LAST:event_DiagramSaveItemActionPerformed

  private void DiagramCloseItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_DiagramCloseItemActionPerformed
  {//GEN-HEADEREND:event_DiagramCloseItemActionPerformed
    close();
  }//GEN-LAST:event_DiagramCloseItemActionPerformed

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
  private javax.swing.JButton AddAssociationBtn;
  private javax.swing.JButton AddClassBtn;
  private javax.swing.JButton AddInterfaceBtn;
  private javax.swing.JButton AddSelectBtn;
  private javax.swing.JMenuItem DiagramCloseItem;
  private javax.swing.JMenu DiagramFileMenu;
  private javax.swing.JMenuBar DiagramMenuBar;
  private javax.swing.JMenuItem DiagramSaveItem;
  private javax.swing.JScrollPane DiagramScrollPane;
  private javax.swing.JLabel SaveLabel;
  // End of variables declaration//GEN-END:variables
}