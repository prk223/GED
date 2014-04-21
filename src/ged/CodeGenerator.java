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
public class CodeGenerator
{
  protected ConfigurationManager cfg_mgr;
  protected String tab_size;
  protected ProjectManager proj_mgr;
  
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
  
  protected String getFileNameBase(Diagram d) throws IOException
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
  
  // To be overwritten by subclasses
  public ArrayList<File> generateCode(Diagram d) throws IOException
  {
    ArrayList<File> baseFiles = new ArrayList<>();
    String fileNameBase = getFileNameBase(d);
    
    ArrayList<DiagramElement> elements = d.getElements();
    Iterator<DiagramElement> itEl = elements.iterator();
    ArrayList<String> classNames = new ArrayList<>();
    while(itEl.hasNext())
    {
      DiagramElement e = itEl.next();
      if(e.getElementType().equals("Class"))
      {
        ClassElement c = (ClassElement)e;
        if(c.getName().equals(""))
          System.err.println("ERR: Class has no name!");
        else
        {
          File codeFile = createBaseClassFile(c, fileNameBase);
          if(codeFile != null)
          {
            boolean duplicateClass = false;
            Iterator<String> nameIt = classNames.iterator();
            while(nameIt.hasNext())
            {
              String name = nameIt.next();
              if(name.equals(c.getName()))
                duplicateClass = true;
            }
            if(duplicateClass)
              System.err.println("ERR: Duplicate class:"+c.getName());
            else
            {
              classNames.add(c.getName());
              baseFiles.add(codeFile);
            }
          }
        }
      }
    }
    
    ArrayList<File> codeFiles = new ArrayList<>();
    Iterator<File> fileIt = baseFiles.iterator();
    while(fileIt.hasNext())
    {
      File baseFile = fileIt.next();
      
      elements = d.getElements();
      itEl = elements.iterator();
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
  
  // to be overwritten by subclasses
  protected File createBaseClassFile(ClassElement c, String baseFilename) throws IOException
  {
    return null;
  }
  
  // to be overwritten by subclasses
  protected File modifyFile(File f, InheritanceRelationship r) throws IOException
  {
    return f;
  }
  
  // to be overwritten by subclasses
  protected File modifyFile(File f, AggregationRelationship r) throws IOException
  {
    return f;
  }
  
  // to be overwritten by subclasses
  protected File modifyFile(File f, AssociationRelationship r) throws IOException
  {
    return f;
  }
  
  // to be overwritten by subclasses
  protected File addHeaders(File f) throws IOException
  {
    return f;
  }
}
