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
public enum Protection
{
  PUBLIC(1), PROTECTED(2), PRIVATE(3);
  private final int value;
  private Protection(int v)
  {
    value = v;
  }
  public int getValue()
  {
    return value;
  }
}
