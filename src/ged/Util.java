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
public class Util
{
  public static String getValueFromTag(String str, String tag)
  {
    String val = "";
    String startTag = "<" + tag + ">";
    String endTag = "</" + tag + ">";
    if(str.contains(startTag) && str.contains(endTag))
    {
      int startIndex = str.indexOf(startTag) + startTag.length();
      int endIndex = str.indexOf(endTag);
      if(endIndex > startIndex)
        val = str.substring(startIndex, endIndex);
    }
    
    return val;
  }
  
}
