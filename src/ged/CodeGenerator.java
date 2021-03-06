/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Comp
 */
public abstract class CodeGenerator
{
  protected ConfigurationManager cfg_mgr;
  protected String tab_size;
  protected ProjectManager proj_mgr;
  private ArrayList<DiagramElement> good_elements;
  private String errors;
  
  protected CodeGenerator() throws IOException
  {
    cfg_mgr  = ConfigurationManager.getInstance();
    proj_mgr = ProjectManager.getInstance();
    cfg_mgr = ConfigurationManager.getInstance();
    
    int numSpacesInTab = Integer.parseInt(cfg_mgr.getConfigValue(
            ConfigurationManager.TAB_SIZE));
    tab_size = "";
    for(int i = 0; i < numSpacesInTab; i++)
      tab_size += " ";
  }
  
  protected String getFileNameBase(ClassDiagram d) throws IOException
  {
    
    String path = cfg_mgr.getConfigValue(ConfigurationManager.WORKSPACE_PATH);
    path += "\\" + proj_mgr.getOpenProjectName();
    path += "_" + d.getName() + "_";
    
    return path;
  }
  
  protected File copyToTempFile(File f) throws FileNotFoundException, IOException
  {
    File tmpFile = new File(".tmpcodegeneratedfile");
    try (BufferedWriter fileWrite = new BufferedWriter(new FileWriter(tmpFile));
          BufferedReader fileRead = new BufferedReader(new FileReader(f)))
    {
      String line = fileRead.readLine();
      while(line != null)
      {
        fileWrite.write(line + "\n");
        line = fileRead.readLine();
      }
      fileWrite.close();
      fileRead.close();
    }
    
    return tmpFile;
  }
  
  public ArrayList<File> generateCode(ClassDiagram d) throws IOException
  {
    ArrayList<File> codeFiles = new ArrayList<>();
    ArrayList<File> baseFiles = new ArrayList<>();
    String fileNameBase = getFileNameBase(d);
    
    checkElements(d);
    if(!errors.isEmpty())
    {
      File errorFile = createErrorFile(d);
      codeFiles.add(errorFile);
    }
    
    Iterator<DiagramElement> itEl = good_elements.iterator();
    while(itEl.hasNext())
    {
      DiagramElement e = itEl.next();
      if(e.getElementType().equals("Class"))
      {
        ClassElement c = (ClassElement)e;
        File codeFile = createBaseClassFile(c, fileNameBase);
        if(codeFile != null)
          baseFiles.add(codeFile);
      }
    }
    
    Iterator<File> fileIt = baseFiles.iterator();
    while(fileIt.hasNext())
    {
      File baseFile = fileIt.next();
      
      itEl = good_elements.iterator();
      while(itEl.hasNext())
      {
        DiagramElement e = itEl.next();
        File modifiedFile = null;
        switch (e.getElementType())
        {
          case "Inheritance":
            modifiedFile = modifyFile(baseFile, (InheritanceRelationship)e);
            break;
          case "Aggregation":
            modifiedFile = modifyFile(baseFile, (AggregationRelationship)e);
            break;
          case "Association":
            modifiedFile = modifyFile(baseFile, (AssociationRelationship)e);
            break;
        }
        
        if(modifiedFile != null)
          baseFile = modifiedFile;
      }
      baseFile = addHeaders(baseFile);
      codeFiles.add(baseFile);
    }
    
    return codeFiles;
  }
  
  protected abstract File createBaseClassFile(ClassElement c, String baseFilename) throws IOException;
  
  protected abstract File modifyFile(File f, InheritanceRelationship r) throws IOException;
  
  protected abstract File modifyFile(File f, AggregationRelationship r) throws IOException;
  
  protected abstract File modifyFile(File f, AssociationRelationship r) throws IOException;
  
  protected abstract File addHeaders(File f) throws IOException;
  
  protected abstract ElementCheckerVisitor getChecker();
  
  private void checkElements(ClassDiagram diagram)
  {
    errors = "";
    good_elements = new ArrayList<>();
    
    ElementCheckerVisitor checker = getChecker();
    String error = diagram.accept(checker);
    if(error.isEmpty())
    {
      ArrayList<DiagramElement> elements = diagram.getElements();
      Iterator<DiagramElement> itEl = elements.iterator();
      while(itEl.hasNext())
      {
        DiagramElement e = itEl.next();
        error = e.accept(checker);
        if(error.isEmpty())
          good_elements.add(e);
        else
          errors += "ERROR:" + error + "\n";
      }
    }
    else
      errors += "ERROR:" + error + "\n";
  }
  
  private File createErrorFile(ClassDiagram d) throws IOException
  {
    String[] individualErrors = errors.split("\n");
    int errorCount = individualErrors.length;
    
    String filePath = getFileNameBase(d) + "_errors";
    File errorFile = new File(filePath);
    BufferedWriter errorWriter = new BufferedWriter(new FileWriter(errorFile));
    errorWriter.write("Errors detected while generating code.\n");
    errorWriter.write("Detected " + errorCount + " errors:\n");
    errorWriter.write(errors);
    errorWriter.close();
    
    return errorFile;
  }
}
