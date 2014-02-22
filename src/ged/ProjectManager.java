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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author Comp
 */
public class ProjectManager
{
  private static ProjectManager instance = null;
  private final String PROJ_EXTENSION = ".ged";
  private ConfigurationManager cfg_mgr = null;
  private DiagramController diag_controller = null;
  private final String varWsPath = ConfigurationManager.WORKSPACE_PATH;
  private Project cur_project = null;
  
  private ProjectManager() throws IOException
  {
    cfg_mgr = ConfigurationManager.getInstance();
    diag_controller = DiagramController.getInstance();
  }
  
  private ArrayList<String> getProjectFileNames()
  {
    String workspacePath = cfg_mgr.getConfigValue(varWsPath);
    File directory = new File(workspacePath);
    File[] matchingFiles;
    matchingFiles = directory.listFiles(new FilenameFilter(){
      @Override
      public boolean accept(File dir, String name){
        return name.endsWith(PROJ_EXTENSION);
      }
    });
    
    ArrayList<String> fileNames = new ArrayList<>();
    for(int i = 0; i < matchingFiles.length; i++)
    {
      String fileName = matchingFiles[i].getName();
      fileNames.add(fileName);
    }
    
    return fileNames;
  }
  
  private ArrayList<String> getDiagramNames(String projectName)
  {
    ArrayList<String> diagNames = new ArrayList<>();
    
    String workspacePath = cfg_mgr.getConfigValue(varWsPath);
    String path = workspacePath + "\\" + projectName + PROJ_EXTENSION;
    File projFile = new File(path);
    if(projFile.exists())
    {
      try (BufferedReader projRdr = 
              new BufferedReader(new FileReader(projFile)))
      {
        projRdr.readLine(); // First line is project name repeated
        String description = projRdr.readLine();
        
        // Add the project we found to the list
        String diagramName = projRdr.readLine();
        while(diagramName != null)
        {
          diagNames.add(diagramName);
          diagramName = projRdr.readLine();
        }
        projRdr.close();
      }
      catch(FileNotFoundException ex)
      {
        System.err.println("ProjMgr:OpenProj:ERROR:Unable to read file" 
                + projectName);
      }
      catch(IOException ex)
      {
        System.err.println("ProjMgr:OpenProj:ERROR:IO Error:" + projectName);
      }
    }
    
    return diagNames;
  }
  
  
  public static ProjectManager getInstance() throws IOException
  {
    if(instance == null)
      instance = new ProjectManager();
    
    return instance;
  }
  
  public void switchWorkspace(String directoryPath)
  {
    cfg_mgr.setConfigValue(varWsPath, directoryPath);
  }
  
  public void createProject(String projName, String projDesc)
  {
    String workspacePath = cfg_mgr.getConfigValue(varWsPath);
    String fileName = projName + PROJ_EXTENSION;
    String filePath = workspacePath + "\\" + fileName;
    try (PrintWriter outFile = new PrintWriter(filePath))
    {
      outFile.println(projName);
      outFile.println(projDesc);
      outFile.close();
    }
    catch(FileNotFoundException ex)
    {
      System.err.println("ERROR:File failed to open:" + filePath);
    }
  }
  
  public void closeProject()
  {
    cur_project = null;
  }
  
  public boolean openProject(String projName)
  {
    boolean openedSuccessfully = false;
    
    // Close project if one is already open
    closeProject();
    
    String workspacePath = cfg_mgr.getConfigValue(varWsPath);
    String path = workspacePath + "\\" + projName + PROJ_EXTENSION;
    cur_project = Project.loadProject(path);
    if(cur_project != null)
      openedSuccessfully = true;
    
    return openedSuccessfully;
  }
  
  public void saveProject()
  {
    if(cur_project != null)
    {
      String workspacePath = cfg_mgr.getConfigValue(varWsPath);
      String projName = cur_project.getName();
      String projDesc = cur_project.getDescription();
      String fileName = projName + PROJ_EXTENSION;
      String filePath = workspacePath + "\\" + fileName;
      cur_project.save(filePath);
    }
  }
  
  public String getOpenProjectName()
  {
    String projName = "";
    if(cur_project != null)
      projName = cur_project.getName();
    
    return projName;
  }
  
  public void addDiagram(String diagName) throws IOException
  {
    if(cur_project != null)
    {
      Diagram diag = diag_controller.createDiagram(diagName);
      cur_project.addDiagram(diag);
      saveProject();
    }
  }
  
  public void removeDiagram(String diagName)
  {
    if(cur_project != null)
    {
      cur_project.deleteDiagram(diagName);
      saveProject();
    }
  }
  
  public ArrayList<Project> getProjects()
  {
    String workspacePath = cfg_mgr.getConfigValue(varWsPath);
    ArrayList<Project> projectList = new ArrayList<>();
    ArrayList<String> projFileNames = getProjectFileNames();
    // Find all projects in workspace
    for(int i=0; i < projFileNames.size(); i++)
    {
      String fileName = workspacePath + "\\" + projFileNames.get(i);
      File projFile = new File(fileName);
      try (BufferedReader reader = 
              new BufferedReader(new FileReader(projFile)))
      {
        String projName = reader.readLine();
        String description = reader.readLine();
        if(projName == null)
          projName = "NULL_PROJ";
        if(description == null)
          description = "NULL_DESCRIPTION";
        
        // Add the project we found to the list
        Project foundProject = new Project(projName, description);
        projectList.add(foundProject);
        reader.close();
      }
      catch(FileNotFoundException ex)
      {
        System.err.println("ERROR:Unable to read file" + fileName);
      }
      catch(IOException ex)
      {
        System.err.println("ERROR:IO Error:" + fileName);
      }
    }
    
    return projectList;
  }
  
  public void deleteProject(String projectName) throws IOException
  {
    String workspacePath = cfg_mgr.getConfigValue(varWsPath);
    String path = workspacePath + "\\" + projectName + PROJ_EXTENSION;
    
    // First delete all diagrams in the project
    ArrayList<String> diagrams = getDiagramNames(projectName);
    for(int i = 0; i < diagrams.size(); i ++)
    {
      diag_controller.deleteDiagram(diagrams.get(i));
    }
    
    File projFile = new File(path);
    if(!projFile.delete())
      System.out.println("Unable to delete project:" + projectName);
  }
  
  public ArrayList<Diagram> getDiagrams()
  {
    ArrayList<Diagram> diagrams;
    if(cur_project != null)
      diagrams = cur_project.getDiagrams();
    else
      diagrams = new ArrayList<>();
    
    return diagrams;
  }
  
}
