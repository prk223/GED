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
public enum RelationshipType
{
  INHERITANCE(1), AGGREGATION(2), ASSOCIATION(3);
  private final int value;
  private RelationshipType(int v)
  {
    value = v;
  }
  public int getValue()
  {
    return value;
  }
}
