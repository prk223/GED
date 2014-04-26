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
public interface ElementCheckerVisitor
{
  public String visit(ClassDiagram diagram);
  public String visit(ClassElement c);
  public String visit(Relationship r);
  public String visit(InheritanceRelationship r);
  public String visit(AggregationRelationship r);
  public String visit(AssociationRelationship r);
}
