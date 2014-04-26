/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Comp
 */
public class CppElementCheckerVisitor implements ElementCheckerVisitor
{
  private final ArrayList<ClassElement> classes;
  private final ArrayList<AggregationRelationship> aggregations;
  
  public CppElementCheckerVisitor()
  {
    classes = new ArrayList<>();
    aggregations = new ArrayList<>();
  }
  
  @Override
  public String visit(ClassDiagram diagram)
  {
    String error = "";
    if(diagram.getElements().isEmpty())
      error = "Diagram does not contain any elements:" + diagram.getName();
    
    return error;
  }
  
  @Override
  public String visit(ClassElement c)
  {
    String error = "";
    
    if(c.getName().isEmpty())
      error = "Class has no name!";
    else
    {
      Iterator<ClassElement> classIt = classes.iterator();
      while(classIt.hasNext())
      {
        ClassElement classInList = classIt.next();
        if(classInList.getName().equals(c.getName()))
        {
          error = "Duplicate class name:" + c.getName();
          break;
        }
      }
    }
    classes.add(c);
    
    return error;
  }
  
  @Override
  public String visit(Relationship r)
  {
    String error = "";
    if(r.getSourceClass() == null)
      error += "Relationship does not have a source class! ";
    if(r.getDestinationClass() == null)
      error += "Relationship does not have a destination class! ";
    
    return error;
  }
  
  @Override
  public String visit(InheritanceRelationship r)
  {
    return visit((Relationship)r);
  }
  
  @Override
  public String visit(AggregationRelationship r)
  {
    String error = visit((Relationship)r);
    
    if(error.isEmpty())
    {
      Iterator<AggregationRelationship> aggIt = aggregations.iterator();
      while(aggIt.hasNext())
      {
        AggregationRelationship agg = aggIt.next();
        if((agg.getSourceClass() == r.getSourceClass()) &&
           (agg.getDestinationClass() == r.getDestinationClass()))
        {
          error += "Multiple aggregation relationships between same two classes:";
          error += r.getSourceClass().getName() + " and ";
          error += r.getDestinationClass().getName() + "! ";
          break;
        }
      }
      aggregations.add(r);
    }
    
    return error;
  }
  
  @Override
  public String visit(AssociationRelationship r)
  {
    return visit((Relationship)r);
  }
}
