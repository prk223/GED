/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

/**
 *
 * @author Comp
 */
public class EditRelationshipDialog extends javax.swing.JDialog
{
  private Relationship editing_relationship;

  /**
   * Creates new form EditRelationshipDialog
   * @param parent
   * @param modal
   */
  public EditRelationshipDialog(java.awt.Frame parent, boolean modal)
  {
    super(parent, modal);
    initComponents();
    editing_relationship = null;
  }
  
  public void open(Relationship e)
  {
    if(e != null)
    {
      editing_relationship = e;
      SourceMultTextField.setText(e.getSourceMultiplicity());
      DestMultTextField.setText(e.getDestinationMultiplicity());
      setVisible(true);
    }
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

    EditRelationshipLabel = new javax.swing.JLabel();
    SourceMultTextField = new javax.swing.JTextField();
    SourceMultLabel = new javax.swing.JLabel();
    DestMultLabel = new javax.swing.JLabel();
    DestMultTextField = new javax.swing.JTextField();
    EditRelationshipOkBtn = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    EditRelationshipLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
    EditRelationshipLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    EditRelationshipLabel.setText("Edit Relationship");
    EditRelationshipLabel.setToolTipText("");

    SourceMultTextField.addKeyListener(new java.awt.event.KeyAdapter()
    {
      public void keyReleased(java.awt.event.KeyEvent evt)
      {
        SourceMultTextFieldKeyReleased(evt);
      }
    });

    SourceMultLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
    SourceMultLabel.setText("Source Multiplicity:      ");

    DestMultLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
    DestMultLabel.setText("Destination Multiplicity:");

    DestMultTextField.addKeyListener(new java.awt.event.KeyAdapter()
    {
      public void keyReleased(java.awt.event.KeyEvent evt)
      {
        DestMultTextFieldKeyReleased(evt);
      }
    });

    EditRelationshipOkBtn.setText("OK");
    EditRelationshipOkBtn.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        EditRelationshipOkBtnMouseClicked(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(EditRelationshipLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
            .addGap(5, 5, 5))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(SourceMultLabel)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(SourceMultTextField)
            .addContainerGap())
          .addGroup(layout.createSequentialGroup()
            .addComponent(DestMultLabel)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(DestMultTextField)
            .addContainerGap())))
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(EditRelationshipOkBtn)
        .addGap(19, 19, 19))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(EditRelationshipLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(SourceMultLabel)
          .addComponent(SourceMultTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(DestMultLabel)
          .addComponent(DestMultTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(EditRelationshipOkBtn)
        .addContainerGap(297, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void SourceMultTextFieldKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_SourceMultTextFieldKeyReleased
  {//GEN-HEADEREND:event_SourceMultTextFieldKeyReleased
    if(editing_relationship != null)
      editing_relationship.setSourceMultiplicity(SourceMultTextField.getText());
  }//GEN-LAST:event_SourceMultTextFieldKeyReleased

  private void DestMultTextFieldKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_DestMultTextFieldKeyReleased
  {//GEN-HEADEREND:event_DestMultTextFieldKeyReleased
    if(editing_relationship != null)
      editing_relationship.setDestinationMultiplicity(DestMultTextField.
              getText());
  }//GEN-LAST:event_DestMultTextFieldKeyReleased

  private void EditRelationshipOkBtnMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_EditRelationshipOkBtnMouseClicked
  {//GEN-HEADEREND:event_EditRelationshipOkBtnMouseClicked
    setVisible(false);
  }//GEN-LAST:event_EditRelationshipOkBtnMouseClicked

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
      java.util.logging.Logger.getLogger(EditRelationshipDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    catch (InstantiationException ex)
    {
      java.util.logging.Logger.getLogger(EditRelationshipDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    catch (IllegalAccessException ex)
    {
      java.util.logging.Logger.getLogger(EditRelationshipDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    catch (javax.swing.UnsupportedLookAndFeelException ex)
    {
      java.util.logging.Logger.getLogger(EditRelationshipDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
        //</editor-fold>

    /* Create and display the dialog */
    java.awt.EventQueue.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        EditRelationshipDialog dialog = new EditRelationshipDialog(new javax.swing.JFrame(), true);
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
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel DestMultLabel;
  private javax.swing.JTextField DestMultTextField;
  private javax.swing.JLabel EditRelationshipLabel;
  private javax.swing.JButton EditRelationshipOkBtn;
  private javax.swing.JLabel SourceMultLabel;
  private javax.swing.JTextField SourceMultTextField;
  // End of variables declaration//GEN-END:variables
}