/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Comp
 */
public class JavaCodeGenerator extends CodeGenerator
{
  
  public JavaCodeGenerator() throws IOException
  {
    super();
  }
  
  @Override
  protected File createBaseClassFile(ClassElement c, String baseFileName) throws IOException
  {
    String tabLevel = "";
    File codeFile = new File(baseFileName + c.getName() + ".java");
    try (BufferedWriter codeWriter = new BufferedWriter(new FileWriter(codeFile)))
    {
      String classLine = tabLevel;
      classLine += c.getProtectionLevel().toString().toLowerCase();
      if(c.getInterface())
        classLine += " interface ";
      else
        classLine += " class ";
      classLine += c.getName() + "\n{\n";
      codeWriter.write(classLine);
      
      tabLevel += tab_size;
      
      // Member attributes
      // If an interface, do not put them here. Java interfaces cannot
      // have attributes, so put them in inherited classes in that case
      if(!c.getInterface())
      {
        ArrayList<Attribute> attributes = c.getAttributes();
        Iterator<Attribute> itAt = attributes.iterator();
        while(itAt.hasNext())
        {
          Attribute a = itAt.next();
          String attLine = tabLevel;
          if(a.getStatic())
            attLine += "static ";
          Protection p = a.getProtectionLevel();
          attLine += p.toString().toLowerCase() + " ";
          attLine += a.getType() + " " + a.getName() + ";\n";
          codeWriter.write(attLine);
        }
      }
      codeWriter.write("\n");
      
      // Member methods
      ArrayList<Operation> operations = c.getOperations();
      Iterator<Operation> itOp = operations.iterator();
      while(itOp.hasNext())
      {
        Operation o = itOp.next();
        String opLine = tabLevel;
        if(o.getStatic())
          opLine += "static ";
        opLine += o.getProtectionLevel().toString().toLowerCase();
        opLine += " " + o.getReturnType();
        opLine += " " + o.getName() + "(";
        ArrayList<Parameter> parameters = o.getParameters();
        Iterator<Parameter> paramIt = parameters.iterator();
        while(paramIt.hasNext())
        {
          Parameter p = paramIt.next();
          opLine += p.getString();
          if(paramIt.hasNext()) opLine += ", ";
        }
        opLine += ")\n" + tabLevel + "{\n\n" + tabLevel + "}\n\n";
        codeWriter.write(opLine);
      }
      
      codeWriter.write("\n}");
      codeWriter.close();
    }
    return codeFile;
  }
  
  @Override
  protected File modifyFile(File f, InheritanceRelationship r) throws IOException
  {
    File modifiedFile = f;
    
    ClassElement source = r.getSourceClass();
    ClassElement destination = r.getDestinationClass();
    if((source != null) && (destination != null) &&
       (!source.getName().equals("")) &&
       (f.getName().contains(source.getName())))
    {
      File tmpFile = copyToTempFile(f);
      BufferedReader tmpReader = new BufferedReader(new FileReader(tmpFile));
      BufferedWriter modWriter = new BufferedWriter(new FileWriter(modifiedFile));
      boolean firstLine = true;
      String line = tmpReader.readLine();
      while(line != null)
      {
        if(firstLine)
        {
          if(!line.contains("extends") && !line.contains("implements"))
          {
            if(destination.getInterface())
              line += " implements ";
            else
              line += " extends ";
          }
          else
            line += ", ";
          line += destination.getName();
          
          // If implementing an interface, put all member attributes in 
          // concrete class
          if(destination.getInterface())
          {
            tmpReader.readLine(); // Remove open bracket line
            line += "\n{";
            ArrayList<Attribute> attributes = destination.getAttributes();
            Iterator<Attribute> itAt = attributes.iterator();
            while(itAt.hasNext())
            {
              Attribute a = itAt.next();
              line += "\n  " + a.getProtectionLevel().toString().toLowerCase();
              line += " " + a.getType() + " " + a.getName() + ";";
            }
          }
          
          firstLine = false;
        }
        
        modWriter.write(line + "\n");
        line = tmpReader.readLine();
      }
      modWriter.close();
      
      tmpFile.delete();
    }
    
    return modifiedFile;
  }
  
  @Override
  protected File modifyFile(File f, AggregationRelationship r) throws IOException
  {
    File modifiedFile = f;
    
    ClassElement source = r.getSourceClass();
    ClassElement destination = r.getDestinationClass();
    if((source != null) && (destination != null) && 
       (!destination.getName().equals("")) &&
       (f.getName().contains(destination.getName())))
    {
      File tmpFile = copyToTempFile(f);
      BufferedReader tmpReader = new BufferedReader(new FileReader(tmpFile));
      BufferedWriter modWriter = new BufferedWriter(new FileWriter(modifiedFile));
      boolean firstLine = true;
      String line = tmpReader.readLine();
      while(line != null)
      {
        if(firstLine)
        {
          // Move past open bracket line
          line += "\n" + tmpReader.readLine() + "\n";
          String srcMult = r.getSourceMultiplicity();
          int mult = 0;
          boolean multIsNumber = true;
          try
          {
            mult = Integer.parseInt(srcMult);
          }
          catch(NumberFormatException e)
          {
            multIsNumber = false;
          }
          
          String tabLevel = tab_size;
          
          if((multIsNumber && (mult <= 1) || srcMult.isEmpty()))
            line += tab_size + source.getName() + " " + source.getName() + "Var;";
          else if(multIsNumber && (mult > 1))
          {
            line += tabLevel + "ArrayList<" + source.getName() + "> ";
            line += source.getName() + "Array = new ArrayList<>("+mult+");";
          }
          else if(srcMult.contains("..") || srcMult.contains("*"))
          {
            line += tabLevel + "ArrayList<" + source.getName() + "> ";
            line += source.getName() + "Array;";
          }
          else // name of variable was directly typed
            line += tabLevel + source.getName() + " " + srcMult + ";";
          
          firstLine = false;
        }
        
        modWriter.write(line + "\n");
        line = tmpReader.readLine();
      }
      modWriter.close();
      
      tmpFile.delete();
    }
    
    return modifiedFile;
  }
  
  @Override
  protected File modifyFile(File f, AssociationRelationship r)
  {
    return f;
  }
  
  @Override
  protected File addHeaders(File f) throws IOException
  {
    File modifiedFile = f;
    boolean hasArrayList = false;
    File tmpFile = copyToTempFile(f);
    BufferedReader tmpReader = new BufferedReader(new FileReader(tmpFile));
    String line = tmpReader.readLine();
    String newFileStr = "";
    while(line != null)
    {
      newFileStr += line + "\n";
      if(line.contains("ArrayList<"))
        hasArrayList = true;
      line = tmpReader.readLine();
    }
    tmpReader.close();
    
    if(hasArrayList)
    {
      newFileStr = "import java.util.ArrayList;\n\n" + newFileStr;
      BufferedWriter modWriter = new BufferedWriter(new FileWriter(modifiedFile));
      modWriter.write(newFileStr);
      modWriter.close();
    }
    
    return modifiedFile;
  }
}
