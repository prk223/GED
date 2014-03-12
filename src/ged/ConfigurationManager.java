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
public class ConfigurationManager
{
  private static final String CFG_FILENAME = ".gedcfg";
  private static ConfigurationManager instance = null;
  private static boolean config_open = false;
  
  
  public static final String WORKSPACE_PATH   = "WORKSPACE_PATH";
  public static final String NUM_UNDO         = "NUM_UNDO";
  public static final String MSG_TIMEOUT      = "MSG_TIMEOUT";
  public static final String MIN_CLASS_WIDTH  = "MIN_CLASS_WIDTH";
  public static final String MIN_CLASS_HEIGHT = "MIN_CLASS_HEIGHT";
  public static final String SELECT_DISTANCE  = "SELECT_DISTANCE";
  public static final String LINE_BFR_SIZE    = "LINE_BUFFER_SIZE";
  public static final String DIAGRAM_BFR_SIZE = "DIAGRAM_BUFFER_SIZE";
  
  private final ArrayList<ConfigurationItem> config_items = new ArrayList<>();
  
  private class ConfigurationItem
  {
    private final String name;
    private String value;
    
    public ConfigurationItem(String varName, String varValue)
    {
      name = varName;
      value = varValue;
    }
    
    public String getName()
    {
      return name;
    }
    
    public String getValue()
    {
      return value;
    }
    
    public void setValue(String varValue)
    {
      value = varValue;
    }
  }
  
  private ConfigurationManager() throws IOException
  {
    // Initialize defaults for configuration items
    config_items.add(new ConfigurationItem(WORKSPACE_PATH, "."));
    config_items.add(new ConfigurationItem(NUM_UNDO, "10"));
    config_items.add(new ConfigurationItem(MSG_TIMEOUT, "5000"));
    config_items.add(new ConfigurationItem(MIN_CLASS_WIDTH, "150"));
    config_items.add(new ConfigurationItem(MIN_CLASS_HEIGHT, "200"));
    config_items.add(new ConfigurationItem(SELECT_DISTANCE, "10"));
    config_items.add(new ConfigurationItem(LINE_BFR_SIZE, "4"));
    config_items.add(new ConfigurationItem(DIAGRAM_BFR_SIZE, "200"));
    
    File configFile = new File(".\\" + CFG_FILENAME);
    if(configFile.exists())
    {
      parseConfigFile(configFile);
    }
    
    config_open = true;
  }
  
  private void parseConfigFile(File cfgFile) throws IOException
  {
    try (BufferedReader cfgRdr = 
            new BufferedReader(new FileReader(cfgFile)))
    {
      String cfgLine = cfgRdr.readLine();
      while(cfgLine != null)
      {
        for(int i = 0; i < config_items.size(); i++)
        {
          String varName = config_items.get(i).getName();
          if(cfgLine.startsWith(varName + "="))
          {
            String varValue = "";
            String[] cfgLineSplit = cfgLine.split(varName + "=");
            if(cfgLineSplit.length > 1)
              varValue = cfgLineSplit[1].trim();
            config_items.get(i).setValue(varValue);
          }
        }
        cfgLine = cfgRdr.readLine();
      }
      cfgRdr.close();
    }
    catch(FileNotFoundException ex)
    {
      System.err.println("CfgMgr:FNFError:Unable to read file" +
              cfgFile.getAbsolutePath());
    }
    catch(IOException ex)
    {
      System.err.println("CfgMgr:IO Error:" + cfgFile.getAbsolutePath());
    }
    
  }
  
  private ConfigurationItem getCfgItem(String varName)
  {
    ConfigurationItem item = null;
    for(int i = 0; i < config_items.size(); i++)
    {
      ConfigurationItem curItem = config_items.get(i);
      if(curItem.getName().equals(varName))
      {
        item = curItem;
        break;
      }
    }
    
    return item;
  }
  
  private int getCfgIndex(String varName)
  {
    ConfigurationItem cfgItem = getCfgItem(varName);
    return config_items.indexOf(cfgItem);
  }
  
  /**
   *
   * @return
   * @throws IOException
   */
  public static ConfigurationManager getInstance() throws IOException
  {
    if(instance == null)
      instance = new ConfigurationManager();
    
    return instance;
  }
  
  public void saveConfiguration()
  {
    String configFilePath = ".\\" + CFG_FILENAME;
    try (PrintWriter outFile = new PrintWriter(configFilePath))
    {
      for(int i = 0; i < config_items.size(); i++)
      {
        ConfigurationItem cfgItem = config_items.get(i);
        String name = cfgItem.getName();
        String value = cfgItem.getValue();
        outFile.println(name + "=" + value);
      }
      outFile.close();
    }
    catch(FileNotFoundException ex)
    {
      System.err.println("CfgMgr:ERROR:File failed to open:" + configFilePath);
    }
  }
  
  public void setConfigValue(String varName, String varValue)
  {
    ConfigurationItem cfgItem = getCfgItem(varName);
    if(cfgItem != null)
    {
      cfgItem.setValue(varValue);
      int cfgItemIndex = config_items.indexOf(cfgItem);
      config_items.set(cfgItemIndex, cfgItem);
    }
    else
      System.err.println("CfgMgr:ERROR:set() config item not found:" + varName);
  }
  
  public String getConfigValue(String varName)
  {
    String cfgValue = "";
    ConfigurationItem cfgItem = getCfgItem(varName);
    if(cfgItem != null)
      cfgValue = cfgItem.getValue();
    else
      System.err.println("CfgMgr:ERROR:get() config item not found:" + varName);
    
    return cfgValue;
  }
}
