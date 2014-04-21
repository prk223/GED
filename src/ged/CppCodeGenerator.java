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
public class CppCodeGenerator extends CodeGenerator
{
  
  public CppCodeGenerator() throws IOException
  {
    super();
  }
  
  @Override
  protected File createBaseClassFile(ClassElement c, String baseFileName) throws IOException
  {
    String tabLevel = "";
    File codeFile = new File(baseFileName + c.getName() + ".cpp");
    try (BufferedWriter codeWriter = new BufferedWriter(new FileWriter(codeFile)))
    {
      String classLine = tabLevel;
      classLine += "class " + c.getName() + "\n{\n";
      codeWriter.write(classLine);
      
      tabLevel = tab_size + tab_size;
      
      // Convert all atrributes to code and separate into private, protected, 
      // and public
      ArrayList<String> privateAttributes = new ArrayList<>();
      ArrayList<String> protectedAttributes = new ArrayList<>();
      ArrayList<String> publicAttributes = new ArrayList<>();
      ArrayList<Attribute> attributes = c.getAttributes();
      Iterator<Attribute> itAt = attributes.iterator();
      while(itAt.hasNext())
      {
        Attribute a = itAt.next();
        String attLine = tabLevel;
        if(a.getStatic())
          attLine += "static ";
        attLine += a.getType() + " " + a.getName() + ";\n";
        switch(a.getProtectionLevel())
        {
          case PRIVATE:
            privateAttributes.add(attLine);
            break;
          case PROTECTED:
            protectedAttributes.add(attLine);
            break;
          case PUBLIC:
            publicAttributes.add(attLine);
            break;
        }
      }
      
      // Generate code for member methods and separate into private, 
      // protected, and public
      ArrayList<String> privateOperations = new ArrayList<>();
      ArrayList<String> protectedOperations = new ArrayList<>();
      ArrayList<String> publicOperations = new ArrayList<>();
      ArrayList<Operation> operations = c.getOperations();
      Iterator<Operation> itOp = operations.iterator();
      String virt = "";
      if(c.getInterface()) virt = "virtual ";
      while(itOp.hasNext())
      {
        Operation o = itOp.next();
        String opLine = tabLevel;
        if(o.getStatic())
          opLine += "static ";
        opLine += virt + o.getReturnType();
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
        switch(o.getProtectionLevel())
        {
          case PRIVATE:
            privateOperations.add(opLine);
            break;
          case PROTECTED:
            protectedOperations.add(opLine);
            break;
          case PUBLIC:
            publicOperations.add(opLine);
            break;
        }
      }
      
      // Write everything out grouped together by protection level
      if(!privateAttributes.isEmpty() || !privateOperations.isEmpty())
      {
        codeWriter.write(tab_size + "private:\n");
        writeCode(codeWriter, privateAttributes, privateOperations);
      }
      if(!protectedAttributes.isEmpty() || !protectedOperations.isEmpty())
      {
        codeWriter.write(tab_size + "protected:\n");
        writeCode(codeWriter, protectedAttributes, protectedOperations);
      }
      
      tabLevel = tab_size;
      codeWriter.write(tabLevel + "public:\n");
      // Constructor
      tabLevel += tab_size;
      codeWriter.write(tabLevel + virt + c.getName() + "()\n");
      codeWriter.write(tabLevel + "{\n\n" + tabLevel + "}\n\n");
      // Destructor
      codeWriter.write(tabLevel + virt + "~" + c.getName() + "()\n");
      codeWriter.write(tabLevel + "{\n\n" + tabLevel + "}\n\n");
      writeCode(codeWriter, publicAttributes, publicOperations);
      
      codeWriter.write("\n};");
      codeWriter.close();
    }
    return codeFile;
  }
  
  private void writeCode(BufferedWriter writer, ArrayList<String> attributes,
          ArrayList<String> operations) throws IOException
  {
    Iterator<String> itCode = attributes.iterator();
    while(itCode.hasNext())
    {
      String codeStr = itCode.next();
      writer.write(codeStr);
    }
    if(!attributes.isEmpty())
      writer.write("\n");
    
    itCode = operations.iterator();
    while(itCode.hasNext())
    {
      String codeStr = itCode.next();
      writer.write(codeStr);
    }
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
          if(!line.contains(":"))
            line += " : ";
          else
            line += ", ";
          line += destination.getProtectionLevel().toString().toLowerCase();
          line += " " + destination.getName();
          
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
          String tabLevel = tab_size;
          
          // Move past open bracket line
          line += "\n" + tmpReader.readLine() + "\n";
          
          // Check if private line is next, if not then create one
          String nextLine = tmpReader.readLine();
          if(nextLine.contains("private"))
          {
            line += nextLine + "\n";
            nextLine = "";
          }
          else
            line += tabLevel + "private:\n";
          
          tabLevel += tab_size;
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
          if((multIsNumber && (mult <= 1) || srcMult.isEmpty()))
            line += tabLevel + source.getName() + " " + source.getName() + "Var;";
          else if(multIsNumber && (mult > 1))
          {
            line += tabLevel + "std::vector<" + source.getName() + "> ";
            line += source.getName() + "Vector("+mult+", 0);";
          }
          else if(srcMult.contains("..") || srcMult.contains("*"))
          {
            line += tabLevel + "std::vector<" + source.getName() + "> ";
            line += source.getName() + "Vector;";
          }
          else // name of variable was directly typed
            line += tabLevel + source.getName() + " " + srcMult + ";";
          
          firstLine = false;
          if(!nextLine.equals(""))
            line += "\n\n" + nextLine;
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
    boolean hasVector = false;
    File tmpFile = copyToTempFile(f);
    BufferedReader tmpReader = new BufferedReader(new FileReader(tmpFile));
    String line = tmpReader.readLine();
    String newFileStr = "";
    while(line != null)
    {
      newFileStr += line + "\n";
      if(line.contains("std::vector"))
        hasVector = true;
      line = tmpReader.readLine();
    }
    tmpReader.close();
    
    if(hasVector)
    {
      newFileStr = "#include <vector>\n\n" + newFileStr;
      BufferedWriter modWriter = new BufferedWriter(new FileWriter(modifiedFile));
      modWriter.write(newFileStr);
      modWriter.close();
    }
    
    return modifiedFile;
  }
}
