/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Comp
 */
public class DiagramDialog extends javax.swing.JDialog
{
  private final ConfigurationManager cfg_mgr;
  private final DiagramController diag_controller;
  private Timer diagram_msg_timer;
  private final CodeDialog code_dialog;
  private final Point mouse_loc;
  private final FileFilter dgm_filter;
  
  /**
   * Creates new form DiagramDialog
   * @param parent
   * @param modal
   * @throws java.io.IOException
   */
  public DiagramDialog(java.awt.Frame parent, boolean modal) throws IOException
  {
    super(parent, modal);
    DiagramPanel diagPanel = new DiagramPanel();
    initComponents();
    initDrawspaceComponents(diagPanel);
    diag_controller = DiagramController.getInstance();
    diag_controller.setupDiagramPanel(diagPanel, DiagramScrollPane.getViewport());
    
    // Force save message to be on top
    this.getContentPane().setComponentZOrder(DiagramMessage, 0);
    
    cfg_mgr = ConfigurationManager.getInstance();
    int msgTimeoutMs = Integer.parseInt(
            cfg_mgr.getConfigValue(ConfigurationManager.MSG_TIMEOUT));
    
    setVisible(false);  
    setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().
            getMaximumWindowBounds());
    DiagramMessage.setVisible(false);
    
    // Set up a timer to make save messages disappear
    diagram_msg_timer = new Timer(msgTimeoutMs, new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e)
      {
        DiagramMessage.setVisible(false);
        diagram_msg_timer.stop();
      }
    });
    
    code_dialog = new CodeDialog(parent, true);
    mouse_loc = new Point(0,0);
    
    dgm_filter = new FileFilter(){
      @Override
      public String getDescription() { return "Diagram files (*.dgm)"; }
      @Override
      public boolean accept(File f)
      {
        if (f.isDirectory()) return true;
        else return f.getName().toLowerCase().endsWith(".dgm");
      }
    };
    DiagramFileChooser.addChoosableFileFilter(dgm_filter);
  }
  
  private void initDrawspaceComponents(DiagramPanel diagPanel)
  {
    DiagramScrollPane.setViewportView(diagPanel);
    diagPanel.setFocusable(true);
    diagPanel.requestFocusInWindow();
    
    diagPanel.addMouseListener(new java.awt.event.MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent evt)
      {
        try
        {
          if((evt.getClickCount() > 1) && (evt.getButton() == MouseEvent.BUTTON1))
            diag_controller.mouseDoubleClicked(evt);
        }
        catch (IOException ex)
        {
          Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      @Override
      public void mouseEntered(java.awt.event.MouseEvent evt)
      {
        diag_controller.mouseEntered(evt);
      }
      @Override
      public void mouseExited(java.awt.event.MouseEvent evt)
      {
        diag_controller.mouseExited(evt);
      }
      @Override
      public void mousePressed(MouseEvent evt)
      {
        try
        {
          if(evt.getButton() == MouseEvent.BUTTON1)
            diag_controller.mousePressed(evt);
          else if(evt.getButton() == MouseEvent.BUTTON3)
            diag_controller.rightMousePressed(evt);
        }
        catch (IOException ex)
        {
          Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      @Override
      public void mouseReleased(MouseEvent evt)
      {
        try
        {
          if(evt.getButton() == MouseEvent.BUTTON1)
            diag_controller.mouseReleased(evt);
        }
        catch (IOException ex)
        {
          Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    });
    diagPanel.addMouseMotionListener(new java.awt.event.MouseAdapter()
    {
      @Override
      public void mouseMoved(MouseEvent evt)
      {
        mouse_loc.x = evt.getX();
        mouse_loc.y = evt.getY();
        diag_controller.mouseMoved(evt);
      }
      @Override
      public void mouseDragged(java.awt.event.MouseEvent evt)
      {
        mouse_loc.x = evt.getX();
        mouse_loc.y = evt.getY();
        diag_controller.mouseDragged(evt);
      }
    });
    
    diagPanel.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.
            getKeyStroke(KeyEvent.VK_DELETE, 0), "DeleteKey");
    diagPanel.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.
            getKeyStroke(KeyEvent.VK_DECIMAL, 0), "DecimalKey");
    diagPanel.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.
            getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "BackSpaceKey");
    diagPanel.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.
            getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), "KeyZ");
    diagPanel.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.
            getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK), "KeyY");
    diagPanel.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.
            getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "KeyS");
    diagPanel.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.
            getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK), "KeyA");
    diagPanel.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.
            getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "KeyC");
    diagPanel.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.
            getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK), "KeyX");
    diagPanel.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.
            getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "KeyV");
    diagPanel.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.
            getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), "KeyO");

    Action deleteAction = new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        try
        {
          diag_controller.deleteKeyPressed();
        }
        catch (IOException ex)
        {
          Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    };
    
    Action undoAction = new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        undo();
      }
    };
    Action redoAction = new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        redo();
      }
    };
    
    Action saveAction = new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        save();
      }
    };
    
    Action selectAllAction = new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        try
        {
          selectAll();
        }
        catch (IOException ex)
        {
          Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    };
    
    Action copyAction = new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        copy();
      }
    };
    
    Action cutAction = new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        cut();
      }
    };
    
    Action pasteAction = new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        paste();
      }
    };
    
    Action openDiagramAction = new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        openDiagram();
      }
    };
    
    diagPanel.getActionMap().put("DeleteKey", deleteAction);
    diagPanel.getActionMap().put("DecimalKey", deleteAction);
    diagPanel.getActionMap().put("BackSpaceKey", deleteAction);
    diagPanel.getActionMap().put("KeyZ", undoAction);
    diagPanel.getActionMap().put("KeyY", redoAction);
    diagPanel.getActionMap().put("KeyS", saveAction);
    diagPanel.getActionMap().put("KeyA", selectAllAction);
    diagPanel.getActionMap().put("KeyC", copyAction);
    diagPanel.getActionMap().put("KeyX", cutAction);
    diagPanel.getActionMap().put("KeyV", pasteAction);
    diagPanel.getActionMap().put("KeyO", openDiagramAction);
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

    DiagramFileChooser = new javax.swing.JFileChooser();
    DiagramMessage = new javax.swing.JLabel();
    AddClassBtn = new javax.swing.JButton();
    AddInheritanceBtn = new javax.swing.JButton();
    SelectBtn = new javax.swing.JButton();
    DiagramScrollPane = new javax.swing.JScrollPane();
    AddAggregationBtn = new javax.swing.JButton();
    AddAssociationBtn = new javax.swing.JButton();
    JavaGenerateCodeBtn = new javax.swing.JButton();
    CppGenerateCodeBtn = new javax.swing.JButton();
    DiagramMenuBar = new javax.swing.JMenuBar();
    DiagramFileMenu = new javax.swing.JMenu();
    OpenDiagramItem = new javax.swing.JMenuItem();
    DiagramSaveItem = new javax.swing.JMenuItem();
    SaveAsItem = new javax.swing.JMenuItem();
    DiagramCloseItem = new javax.swing.JMenuItem();
    DiagramEditMenu = new javax.swing.JMenu();
    SelectAllItem = new javax.swing.JMenuItem();
    DiagramUndoItem = new javax.swing.JMenuItem();
    DiagramRedoItem = new javax.swing.JMenuItem();
    CopyItem = new javax.swing.JMenuItem();
    CutItem = new javax.swing.JMenuItem();
    PasteItem = new javax.swing.JMenuItem();

    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    setModal(true);
    setName("Diagram"); // NOI18N
    setUndecorated(true);

    DiagramMessage.setBackground(new java.awt.Color(204, 51, 0));
    DiagramMessage.setLabelFor(DiagramScrollPane);
    DiagramMessage.setText("StatusMsg");
    DiagramMessage.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
    DiagramMessage.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    DiagramMessage.setMinimumSize(new java.awt.Dimension(2000000, 14));
    DiagramMessage.setOpaque(true);
    DiagramMessage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

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

    AddAssociationBtn.setText("Association");
    AddAssociationBtn.setMaximumSize(new java.awt.Dimension(57, 23));
    AddAssociationBtn.setMinimumSize(new java.awt.Dimension(57, 23));
    AddAssociationBtn.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        AddAssociationBtnMouseClicked(evt);
      }
    });

    JavaGenerateCodeBtn.setText("Generate Java Code");
    JavaGenerateCodeBtn.setMaximumSize(new java.awt.Dimension(57, 23));
    JavaGenerateCodeBtn.setMinimumSize(new java.awt.Dimension(57, 23));
    JavaGenerateCodeBtn.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseReleased(java.awt.event.MouseEvent evt)
      {
        JavaGenerateCodeBtnMouseReleased(evt);
      }
    });

    CppGenerateCodeBtn.setText("Generate C++ Code");
    CppGenerateCodeBtn.setMaximumSize(new java.awt.Dimension(57, 23));
    CppGenerateCodeBtn.setMinimumSize(new java.awt.Dimension(57, 23));
    CppGenerateCodeBtn.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseReleased(java.awt.event.MouseEvent evt)
      {
        CppGenerateCodeBtnMouseReleased(evt);
      }
    });

    DiagramFileMenu.setText("File");

    OpenDiagramItem.setText("Open Diagram (CTRL+O)");
    OpenDiagramItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        OpenDiagramItemActionPerformed(evt);
      }
    });
    DiagramFileMenu.add(OpenDiagramItem);

    DiagramSaveItem.setText("Save (CTRL+S)");
    DiagramSaveItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        DiagramSaveItemActionPerformed(evt);
      }
    });
    DiagramFileMenu.add(DiagramSaveItem);

    SaveAsItem.setText("Save as");
    SaveAsItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        SaveAsItemActionPerformed(evt);
      }
    });
    DiagramFileMenu.add(SaveAsItem);

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

    DiagramEditMenu.setText("Edit");

    SelectAllItem.setText("Select All (CTRL+A)");
    SelectAllItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        SelectAllItemActionPerformed(evt);
      }
    });
    DiagramEditMenu.add(SelectAllItem);

    DiagramUndoItem.setText("Undo (CTRL+Z)");
    DiagramUndoItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        DiagramUndoItemActionPerformed(evt);
      }
    });
    DiagramEditMenu.add(DiagramUndoItem);

    DiagramRedoItem.setText("Redo (CTRL+Y)");
    DiagramRedoItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        DiagramRedoItemActionPerformed(evt);
      }
    });
    DiagramEditMenu.add(DiagramRedoItem);

    CopyItem.setText("Copy (CTRL+C)");
    CopyItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        CopyItemActionPerformed(evt);
      }
    });
    DiagramEditMenu.add(CopyItem);

    CutItem.setText("Cut (CTRL+X)");
    CutItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        CutItemActionPerformed(evt);
      }
    });
    DiagramEditMenu.add(CutItem);

    PasteItem.setText("Paste (CTRL+V)");
    PasteItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        PasteItemActionPerformed(evt);
      }
    });
    DiagramEditMenu.add(PasteItem);

    DiagramMenuBar.add(DiagramEditMenu);

    setJMenuBar(DiagramMenuBar);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(DiagramMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 658, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(SelectBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(AddInheritanceBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(AddClassBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(AddAggregationBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(AddAssociationBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(CppGenerateCodeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(JavaGenerateCodeBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(DiagramScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE))))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGap(32, 32, 32)
            .addComponent(AddClassBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(AddInheritanceBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(AddAggregationBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(AddAssociationBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(SelectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
            .addComponent(JavaGenerateCodeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(CppGenerateCodeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(DiagramScrollPane)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(DiagramMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void DiagramSaveItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_DiagramSaveItemActionPerformed
  {//GEN-HEADEREND:event_DiagramSaveItemActionPerformed
    save();
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

  private void AddClassBtnMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_AddClassBtnMouseClicked
  {//GEN-HEADEREND:event_AddClassBtnMouseClicked
    try
    {
      diag_controller.prepAddClass();
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_AddClassBtnMouseClicked

  private void SelectBtnMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_SelectBtnMouseClicked
  {//GEN-HEADEREND:event_SelectBtnMouseClicked
    try
    {
      diag_controller.selectElements();
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_SelectBtnMouseClicked

  private void AddInheritanceBtnMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_AddInheritanceBtnMouseClicked
  {//GEN-HEADEREND:event_AddInheritanceBtnMouseClicked
    try
    {
      diag_controller.prepAddInheritance();
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_AddInheritanceBtnMouseClicked

  private void AddAggregationBtnMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_AddAggregationBtnMouseClicked
  {//GEN-HEADEREND:event_AddAggregationBtnMouseClicked
    try
    {
      diag_controller.prepAddAggregation();
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_AddAggregationBtnMouseClicked

  private void AddAssociationBtnMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_AddAssociationBtnMouseClicked
  {//GEN-HEADEREND:event_AddAssociationBtnMouseClicked
    try
    {
      diag_controller.prepAddAssociation();
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_AddAssociationBtnMouseClicked

  private void DiagramUndoItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_DiagramUndoItemActionPerformed
  {//GEN-HEADEREND:event_DiagramUndoItemActionPerformed
    undo();
  }//GEN-LAST:event_DiagramUndoItemActionPerformed

  private void DiagramRedoItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_DiagramRedoItemActionPerformed
  {//GEN-HEADEREND:event_DiagramRedoItemActionPerformed
    redo();
  }//GEN-LAST:event_DiagramRedoItemActionPerformed

  private void JavaGenerateCodeBtnMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_JavaGenerateCodeBtnMouseReleased
  {//GEN-HEADEREND:event_JavaGenerateCodeBtnMouseReleased
    setVisible(false);
    try
    {
      code_dialog.open(true);
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
    setVisible(true);
  }//GEN-LAST:event_JavaGenerateCodeBtnMouseReleased

  private void CppGenerateCodeBtnMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_CppGenerateCodeBtnMouseReleased
  {//GEN-HEADEREND:event_CppGenerateCodeBtnMouseReleased
    setVisible(false);
    try
    {
      code_dialog.open(false);
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
    setVisible(true);
  }//GEN-LAST:event_CppGenerateCodeBtnMouseReleased

  private void SelectAllItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_SelectAllItemActionPerformed
  {//GEN-HEADEREND:event_SelectAllItemActionPerformed
    try
    {
      selectAll();
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_SelectAllItemActionPerformed

  private void CopyItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CopyItemActionPerformed
  {//GEN-HEADEREND:event_CopyItemActionPerformed
    copy();
  }//GEN-LAST:event_CopyItemActionPerformed

  private void CutItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CutItemActionPerformed
  {//GEN-HEADEREND:event_CutItemActionPerformed
    cut();
  }//GEN-LAST:event_CutItemActionPerformed

  private void PasteItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_PasteItemActionPerformed
  {//GEN-HEADEREND:event_PasteItemActionPerformed
    paste();
  }//GEN-LAST:event_PasteItemActionPerformed

  private void SaveAsItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_SaveAsItemActionPerformed
  {//GEN-HEADEREND:event_SaveAsItemActionPerformed
    String path = cfg_mgr.getConfigValue(
            ConfigurationManager.WORKSPACE_PATH);
    File workspace = new File(path);
    DiagramFileChooser.setCurrentDirectory(workspace);
    DiagramFileChooser.setFileFilter(dgm_filter);
    int btnClicked = DiagramFileChooser.showSaveDialog(this);
    if(btnClicked == JFileChooser.APPROVE_OPTION)
    {
      String fileName = DiagramFileChooser.getSelectedFile().getName();
      path = DiagramFileChooser.getSelectedFile().getAbsolutePath();
      if(!fileName.contains(".")) // no extension
      {
        fileName += ".dgm";
        path += ".dgm";
      }
      boolean saved = diag_controller.saveDiagram(path);
      String saveMsg;
      Color color;
      if(saved)
      {
        saveMsg = "Successfully saved diagram '" + fileName + "'";
        color = Color.GREEN;
      }
      else
      {
        saveMsg = "FAILED to save diagram '" + fileName + "'";
        color = Color.RED;
      }

      TitledBorder paneBorder = (TitledBorder)DiagramScrollPane.getBorder();
      paneBorder.setTitle(diag_controller.getOpenDiagramName());
      DiagramScrollPane.repaint();
      DiagramMessage.setText(saveMsg);
      DiagramMessage.setBackground(color);
      DiagramMessage.setVisible(true);
      diagram_msg_timer.stop();
      diagram_msg_timer.start();
    }
  }//GEN-LAST:event_SaveAsItemActionPerformed

  private void OpenDiagramItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_OpenDiagramItemActionPerformed
  {//GEN-HEADEREND:event_OpenDiagramItemActionPerformed
    openDiagram();
  }//GEN-LAST:event_OpenDiagramItemActionPerformed

  public void open(String diagram) throws IOException
  {
    boolean opened = diag_controller.openDiagram(diagram);
    if(opened)
    {
      TitledBorder paneBorder = (TitledBorder)DiagramScrollPane.getBorder();
      paneBorder.setTitle(diagram);
      DiagramScrollPane.repaint();
      setVisible(true);
    }
  }
  
  public void openDiagram()
  {   
    String path = cfg_mgr.getConfigValue(
            ConfigurationManager.WORKSPACE_PATH);
    File workspace = new File(path);
    DiagramFileChooser.setCurrentDirectory(workspace);
    DiagramFileChooser.setFileFilter(dgm_filter);
    int btnClicked = DiagramFileChooser.showOpenDialog(this);
    if(btnClicked == JFileChooser.APPROVE_OPTION)
    {
      String fileName = DiagramFileChooser.getSelectedFile().getName();
      path = DiagramFileChooser.getSelectedFile().getAbsolutePath();
      boolean opened = false;
      try
      {
        opened = diag_controller.openDiagramFile(path);
      }
      catch (IOException ex) 
      {
        Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
      }
      String openMsg;
      Color color;
      if(opened)
      {
        openMsg = "Successfully opened diagram '" + fileName + "'";
        color = Color.GREEN;
      }
      else
      {
        openMsg = "FAILED to open diagram '" + fileName + "'";
        color = Color.RED;
      }

      TitledBorder paneBorder = (TitledBorder)DiagramScrollPane.getBorder();
      paneBorder.setTitle(diag_controller.getOpenDiagramName());
      DiagramScrollPane.repaint();
      DiagramMessage.setText(openMsg);
      DiagramMessage.setBackground(color);
      DiagramMessage.setVisible(true);
      diagram_msg_timer.stop();
      diagram_msg_timer.start();
    }
  }
  
  public void close() throws IOException
  {
    diag_controller.closeDiagram();
    setVisible(false);
  }
  
  private void undo()
  {
    try
    {
      boolean ableToUndo = diag_controller.undoLastChange();

      // If unable to undo, display a message so user knows
      if(!ableToUndo)
      {
        String undoMsg = "Unable to undo any further.";
        DiagramMessage.setText(undoMsg);
        DiagramMessage.setBackground(Color.MAGENTA);
        DiagramMessage.setVisible(true);
        diagram_msg_timer.stop();
        diagram_msg_timer.start();
      }
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  private void redo()
  {
    try
    {
      boolean ableToRedo = diag_controller.redoLastChange();
    
      // If unable to undo, display a message so user knows
      if(!ableToRedo)
      {
        String redoMsg = "Unable to redo any further, already at latest.";
        DiagramMessage.setText(redoMsg);
        DiagramMessage.setBackground(Color.CYAN);
        DiagramMessage.setVisible(true);
        diagram_msg_timer.stop();
        diagram_msg_timer.start();
      }
    }
    catch (IOException ex)
    {
      Logger.getLogger(DiagramDialog.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  private void save()
  {
    boolean saved = diag_controller.saveDiagram();
    String saveMsg;
    Color color;
    if(saved)
    {
      saveMsg = "Successfully saved diagram: ";
      color = Color.GREEN;
    }
    else
    {
      saveMsg = "FAILED TO SAVE DIAGRAM: ";
      color = Color.RED;
    }
    saveMsg += diag_controller.getOpenDiagramName();
    DiagramMessage.setText(saveMsg);
    DiagramMessage.setBackground(color);
    DiagramMessage.setVisible(true);
    
    diagram_msg_timer.stop();
    diagram_msg_timer.start();
  }
  
  private void selectAll() throws IOException
  {
    diag_controller.selectAll();
  }
  
  private void copy()
  {
    diag_controller.copy();
  }
  
  private void cut()
  {
    diag_controller.cut();
  }
  
  private void paste()
  {
    diag_controller.paste(mouse_loc);
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
  private javax.swing.JButton AddInheritanceBtn;
  private javax.swing.JMenuItem CopyItem;
  private javax.swing.JButton CppGenerateCodeBtn;
  private javax.swing.JMenuItem CutItem;
  private javax.swing.JMenuItem DiagramCloseItem;
  private javax.swing.JMenu DiagramEditMenu;
  private javax.swing.JFileChooser DiagramFileChooser;
  private javax.swing.JMenu DiagramFileMenu;
  private javax.swing.JMenuBar DiagramMenuBar;
  private javax.swing.JLabel DiagramMessage;
  private javax.swing.JMenuItem DiagramRedoItem;
  private javax.swing.JMenuItem DiagramSaveItem;
  private javax.swing.JScrollPane DiagramScrollPane;
  private javax.swing.JMenuItem DiagramUndoItem;
  private javax.swing.JButton JavaGenerateCodeBtn;
  private javax.swing.JMenuItem OpenDiagramItem;
  private javax.swing.JMenuItem PasteItem;
  private javax.swing.JMenuItem SaveAsItem;
  private javax.swing.JMenuItem SelectAllItem;
  private javax.swing.JButton SelectBtn;
  // End of variables declaration//GEN-END:variables
}
