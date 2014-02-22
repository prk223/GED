/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author Comp
 */
public class Project
{
  private String name;
  private String description;
  private final ArrayList<Diagram> diagrams;
  
  public Project(String pName, String pDescription)
  {
    name = pName;
    description = pDescription;
    diagrams = new ArrayList<>();
  }
  
  public void addDiagram(Diagram diag)
  {
    diagrams.add(diag);
  }
  
  public void deleteDiagram(String diagName)
  {
    for(int i = 0; i < diagrams.size(); i++)
    {
      Diagram d = diagrams.get(i);
      String n = d.getName();
      if(n.equals(diagName))
      {
        diagrams.remove(i);
        break;
      }
    }
  }
  
  public void setName(String s)
  {
    name = s;
  }
  
  public void setDescription(String s)
  {
    description = s;
  }
  
  public ArrayList<Diagram> getDiagrams()
  {
    return diagrams;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public void save(String filePath)
  {
    try (PrintWriter outFile = new PrintWriter(filePath))
      {
        outFile.println(name);
        outFile.println(description);
        for(int i = 0; i < diagrams.size(); i++)
        { // add name of all diagrams in project
          outFile.println(diagrams.get(i).getName());
        }
        outFile.close();
      }
      catch(FileNotFoundException ex)
      {
        System.err.println("ERROR:File failed to open:" + filePath);
      }
  }

  public static Project loadProject(String filePath)
  {
    Project loadedProject = null;
    File projFile = new File(filePath);
    if(projFile.exists())
    {
      try (BufferedReader projRdr = 
              new BufferedReader(new FileReader(projFile)))
      {
        String name = projRdr.readLine();
        String description = projRdr.readLine();
        
        // Add the project we found to the list
        loadedProject = new Project(name, description);
        String diagramName = projRdr.readLine();
        while(diagramName != null)
        {
          Diagram diag = new Diagram(diagramName);
          loadedProject.addDiagram(diag);
          diagramName = projRdr.readLine();
        }
        projRdr.close();
      }
      catch(FileNotFoundException ex)
      {
        System.err.println("ProjMgr:OpenProj:ERROR:Unable to read file" 
                + filePath);
      }
      catch(IOException ex)
      {
        System.err.println("ProjMgr:OpenProj:ERROR:IO Error:" + filePath);
      }
    }
    
    return loadedProject;
  }
  
}


