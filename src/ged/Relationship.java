/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import static ged.Util.getValueFromTag;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Comp
 */
public class Relationship implements DiagramElement
{
  protected final ConfigurationManager cfg_mgr;
  protected final DiagramController diag_controller;
  
  private TetherElementData source_tether;
  private TetherElementData destination_tether;
  
  private int source_class_uid;
  private int destination_class_uid;
  
  private String source_multiplicity;
  private String destination_multiplicity;
  private int source_mult_len;
  private double source_mult_angle;
  private int destination_mult_len;
  private double destination_mult_angle;
  
  protected final Point source_location;
  protected final Point destination_location;
  protected final ArrayList<Point> vertices;
  
  // Point selected for setting new location
  protected Point selected_point;
  // dist from a point to count it as being selected
  protected final double max_select_distance;
  protected final int vertex_diameter;
  protected final double vertex_remove_distance;
  protected final int symbol_size;
  
  private int unique_id;
  
  
  public Relationship(int x, int y) throws IOException
  {
    cfg_mgr = ConfigurationManager.getInstance();
    diag_controller = DiagramController.getInstance();
    int lineLength = Integer.parseInt(cfg_mgr.
            getConfigValue(ConfigurationManager.DFLT_RLTNSHP_LEN));
    max_select_distance = Integer.parseInt(cfg_mgr.
            getConfigValue(ConfigurationManager.SELECT_DISTANCE));
    vertex_diameter = Integer.parseInt(cfg_mgr.
            getConfigValue(ConfigurationManager.VERTEX_DIAMETER));
    symbol_size = Integer.parseInt(cfg_mgr.
            getConfigValue(ConfigurationManager.RLTN_SYM_SIZE));
    vertex_remove_distance = Integer.parseInt(cfg_mgr.
            getConfigValue(ConfigurationManager.VERTEX_RM_DIST));
    source_mult_len = Integer.parseInt(cfg_mgr.
            getConfigValue(ConfigurationManager.SRC_MULT_DFLT_LEN));
    source_mult_angle = (Math.PI / 180.0) * Integer.parseInt(cfg_mgr.
            getConfigValue(ConfigurationManager.SRC_MULT_DFLT_ANGL));
    destination_mult_len = Integer.parseInt(cfg_mgr.
            getConfigValue(ConfigurationManager.DEST_MULT_DFLT_LEN));
    destination_mult_angle = (Math.PI / 180.0) * Integer.parseInt(cfg_mgr.
            getConfigValue(ConfigurationManager.DEST_MULT_DFLT_ANGL));
    
    source_location = new Point(x, y);
    destination_location = new Point(x + lineLength, y);
    vertices = new ArrayList<>();
    
    selected_point = null;
    
    source_multiplicity = "";
    destination_multiplicity = "";
    
    source_tether = null;
    destination_tether = null;
    source_class_uid = 0;
    destination_class_uid = 0;
    
    unique_id = 0;
  }
  
  @Override
  public void draw(Graphics g)
  {
    try
    {
      updateTethers();
    }
    catch (IOException ex)
    {
      Logger.getLogger(Relationship.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    Point curPoint = source_location;
    
    // Go through each vertex drawing a line to it
    Iterator<Point> vertIt = vertices.iterator();
    while(vertIt.hasNext())
    {
      Point vertex = vertIt.next();
      drawLine(g, new Point(curPoint.x, curPoint.y),
              new Point(vertex.x, vertex.y));
      int vertexX = vertex.x - (vertex_diameter/2);
      int vertexY = vertex.y - (vertex_diameter/2);
      g.fillOval(vertexX, vertexY, vertex_diameter, vertex_diameter);
      curPoint = vertex;
    }
    
    // Draw final line to destination
    drawLine(g, new Point(curPoint.x, curPoint.y), 
           new Point(destination_location.x, destination_location.y));
    
    drawEndpoints(g);
    
    drawMultiplicities(g);
  }
  
  protected void drawLine(Graphics g, Point A, Point B)
  {
    g.drawLine(A.x, A.y, B.x, B.y);
  }
  
  protected void drawDashedLine(Graphics g, Point A, Point B)
  {
    Graphics2D g2 = (Graphics2D)g;
    Stroke oldStroke = g2.getStroke();
    BasicStroke dashed = new BasicStroke((float) 1.0,
            BasicStroke.CAP_SQUARE, 
            BasicStroke.JOIN_MITER,
            (float)10.0, 
            new float[]{9}, 
            0);
    g2.setStroke(dashed);
    g2.drawLine(A.x, A.y, B.x, B.y);
    g2.setStroke(oldStroke);
  }
  
  protected void drawEndpoints(Graphics g)
  {
    // subclasses overwite
  }
  
    boolean right = false;
  protected void drawMultiplicities(Graphics g)
  {
    Graphics2D g2 = (Graphics2D)g;
    FontMetrics metrics = g2.getFontMetrics();
    
    double toSrcAngle = getAngleToSource();
    double textSrcAngle = toSrcAngle + (Math.PI / 2);
    int srcX;
    int srcY;
    if(textSrcAngle < (3*Math.PI / 2)) // toSource is right half of circle
    {
      int srcTextWidth = metrics.stringWidth(source_multiplicity);
      textSrcAngle += Math.PI;
      srcX = source_location.x +
              (int)(-Math.cos(textSrcAngle)*srcTextWidth)+
              (int)(-Math.sin(Math.PI - source_mult_angle - toSrcAngle)*
                     source_mult_len);
      srcY = source_location.y +
              (int)(-Math.sin(textSrcAngle)*srcTextWidth)+
              (int)(-Math.cos(Math.PI - source_mult_angle - toSrcAngle)*
                     source_mult_len);
    }
    else // toSource is left half of circle
    {
      srcX = source_location.x +
              (int)(Math.cos(toSrcAngle - source_mult_angle - (3*Math.PI/2))*
                    source_mult_len);
      srcY = source_location.y +
              (int)(Math.sin(toSrcAngle - source_mult_angle - (3*Math.PI/2))*
                    source_mult_len);
    }
    
    
    AffineTransform origTransform = g2.getTransform();
    g2.rotate(textSrcAngle, srcX, srcY);
    g2.drawString(source_multiplicity, srcX, srcY);
    g2.setTransform(origTransform);
    
    double toDestAngle = getAngleToDestination();
    int destX;
    int destY;
    double textDestAngle = toDestAngle - (Math.PI / 2);
    if(textDestAngle > (Math.PI / 2)) // toDest is left half of circle
    {
      textDestAngle -= Math.PI;
      destX = destination_location.x +
              (int)(Math.cos(toDestAngle - destination_mult_angle - 
                             (3*Math.PI/2))*destination_mult_len);
      destY = destination_location.y +
              (int)(Math.sin(toDestAngle - destination_mult_angle - 
                             (3*Math.PI/2))*destination_mult_len);
    }
    else // toDest is right half of circle
    {
      int destTextWidth = metrics.stringWidth(destination_multiplicity);
      destX = destination_location.x +
              (int)(-Math.cos(textDestAngle)*destTextWidth) +
              (int)(-Math.sin(Math.PI - destination_mult_angle - toDestAngle)*
                              destination_mult_len);
      destY = destination_location.y +
              (int)(-Math.sin(textDestAngle)*destTextWidth) +
              (int)(-Math.cos(Math.PI - destination_mult_angle - toDestAngle)*
                              destination_mult_len);
    }
    g2.rotate(textDestAngle, destX, destY);
    g2.drawString(destination_multiplicity, destX, destY);
    g2.setTransform(origTransform);
  }
  
  protected void updateTethers() throws IOException
  {
    if((source_tether == null) && source_class_uid != 0)
    {
      DiagramElement e = diag_controller.getUniqueElement(source_class_uid);
      if(e != null && "Class".equals(e.getElementType()))
        source_tether = new TetherElementData((ClassElement)e, source_location);
      else
        System.out.println("WARN:Relationship:Attached UID was not a class!");
      source_class_uid = 0;
    }
    if((destination_tether == null) && destination_class_uid != 0)
    {
      DiagramElement e = diag_controller.getUniqueElement(destination_class_uid);
      if(e != null && "Class".equals(e.getElementType()))
        destination_tether = 
                new TetherElementData((ClassElement)e, destination_location);
      else
        System.out.println("WARN:Relationship:Attached UID was not a class!");
      destination_class_uid = 0;
    }
    
    if(source_tether != null)
    {
      Point newLoc = source_tether.getPoint();
      source_location.x = newLoc.x;
      source_location.y = newLoc.y;
    }
    if(destination_tether != null)
    {
      Point newLoc = destination_tether.getPoint();
      destination_location.x = newLoc.x;
      destination_location.y = newLoc.y;
    }
  }
  
  @Override
  public Point getLocation()
  {
    Point loc;
    if(selected_point != null)
      loc = new Point(selected_point.x, selected_point.y);
    else
      loc = new Point(source_location.x, source_location.y);
    
    return loc;
    
  }
  
  @Override
  public void setLocation(Point loc)
  {
    if(selected_point != null)
    {
      selected_point.x = loc.x;
      selected_point.y = loc.y;
      
      if(selected_point == source_location)
        source_tether = null;
      else if(selected_point == destination_location)
        destination_tether = null;
      
      Iterator<Point> vertIt = vertices.iterator();
      while(vertIt.hasNext())
      {
        Point vertex = vertIt.next();
        if(selected_point != vertex)
        {
          double distance = getDistanceBetweenPoints(selected_point, vertex);
          if(distance < vertex_remove_distance)
          {
            vertIt.remove();
            break;
          }
        }
      }
    }
    else
    {
      source_tether = null;
      destination_tether = null;
      
      int distX = loc.x - source_location.x;
      int distY = loc.y - source_location.y;
      
      source_location.x += distX;
      source_location.y += distY;
      
      Iterator<Point> vertIt = vertices.iterator();
      while(vertIt.hasNext())
      {
        Point vertex = vertIt.next();
        vertex.x += distX;
        vertex.y += distY;
      }
      
      destination_location.x += distX;
      destination_location.y += distY;
    }
  }
  
  @Override
  public String getElementType()
  {
    return "Relationship";
  }

  @Override
  public String getPersistentRepresentation()
  {
    int source_uid = 0;
    int dest_uid = 0;
    
    if(source_tether != null)
      source_uid = source_tether.getElement().getUniqueId();
    if(destination_tether != null)
      dest_uid = destination_tether.getElement().getUniqueId();
    
    String rep = "<uniqueID>" + unique_id + "</uniqueID>";
    rep += "<sourceUID>" + source_uid + "</sourceUID>";
    rep += "<destinationUID>" + dest_uid + "</destinationUID>";
    
    rep += "<sourceMult>" + source_multiplicity + "</sourceMult>";
    rep += "<destinationMult>" + destination_multiplicity + 
            "</destinationMult>";
    
    rep += "<sourceMultLength>" + source_mult_len + "</sourceMultLength>";
    rep += "<sourceMultAngle>" + source_mult_angle + "</sourceMultAngle>";
    rep += "<destinationMultLength>" + destination_mult_len + 
            "</destinationMultLength>";
    rep += "<destinationMultAngle>" + destination_mult_angle + 
            "<destinationMultAngle>";

    rep += "<sourceLoc>" + source_location.x + "," + source_location.y +
            "</sourceLoc>";
    rep += "<destinationLoc>" + destination_location.x + "," + 
            destination_location.y + "</destinationLoc>";
    
    Iterator<Point> vertIt = vertices.iterator();
    while(vertIt.hasNext())
    {
      Point p = vertIt.next();
      rep += "<vertex>" + p.x + "," + p.y + "</vertex>";
    }
    
    return rep;
  }
  
  public static DiagramElement fromPersistentRepresentation(String s) throws IOException
  {
    Relationship r = new Relationship(0, 0);
    return fromPersistentRepresentation(s, r);
  }
  
  protected static Relationship fromPersistentRepresentation(String s, Relationship r) throws IOException
  {
    String sourceLocStr = getValueFromTag(s, "sourceLoc");
    String[] sourceLocArr = sourceLocStr.split(",");
    if(sourceLocArr.length > 1)
    {
      r.source_location.x = Integer.parseInt(sourceLocArr[0]);
      r.source_location.y = Integer.parseInt(sourceLocArr[1]);
    }
    
    int uid = Integer.parseInt(getValueFromTag(s, "uniqueID"));
    r.unique_id = uid;
    
    int sourceUID = Integer.parseInt(getValueFromTag(s, "sourceUID"));
    r.source_class_uid = sourceUID;
    int destinationUID = Integer.parseInt(getValueFromTag(s, "destinationUID"));
    r.destination_class_uid = destinationUID;
    
    
    String sourceMult = getValueFromTag(s, "sourceMult");
    r.source_multiplicity = sourceMult;
    String destMult = getValueFromTag(s, "destinationMult");
    r.destination_multiplicity = destMult;
    
    String srcMultLen    = getValueFromTag(s, "sourceMultLength");
    String srcMultAngle  = getValueFromTag(s, "sourceMultAngle");
    String destMultLen   = getValueFromTag(s, "destinationMultLength");
    String destMultAngle = getValueFromTag(s, "destinationMultAngle");
    if(!srcMultLen.equals(""))
      r.source_mult_len = Integer.parseInt(srcMultLen);
    if(!srcMultAngle.equals(""))
      r.source_mult_angle = Double.parseDouble(srcMultAngle);
    if(!destMultLen.equals(""))
      r.destination_mult_len = Integer.parseInt(destMultLen);
    if(!destMultAngle.equals(""))
      r.destination_mult_angle = Double.parseDouble(destMultAngle);
    
    Point destPoint = new Point(0, 0);
    String destLocStr = getValueFromTag(s, "destinationLoc");
    String[] destLocArr = destLocStr.split(",");
    if(destLocArr.length > 1)
    {
      destPoint.x = Integer.parseInt(destLocArr[0]);
      destPoint.y = Integer.parseInt(destLocArr[1]);
    }
    r.destination_location.x = destPoint.x;
    r.destination_location.y = destPoint.y;
    
    // Get all vertices
    String[] vert_pieces = s.split("<vertex>");
    for(int i = 0; i < vert_pieces.length; i++)
    {
      if(vert_pieces[i].contains("</vertex>"))
      {
        vert_pieces[i] = "<vertex>" + vert_pieces[i];
        vert_pieces[i] = getValueFromTag(vert_pieces[i], "vertex");
        Point vertex = new Point(0, 0);
        String[] vertArr = vert_pieces[i].split(",");
        if(vertArr.length > 1)
        {
          vertex.x = Integer.parseInt(vertArr[0]);
          vertex.y = Integer.parseInt(vertArr[1]);
          r.vertices.add(vertex);
        }
      }
    }
    
    return r;
  }
  
  @Override
  public double getDistanceFrom(int x, int y)
  {
    double minDistance = 100000000;
    double distance;
    Point p = new Point(x, y);
    Point closestPoint = source_location;
    
    Point curPoint = source_location;
    Iterator<Point> vertIt = vertices.iterator();
    while(vertIt.hasNext())
    {
      Point vertex = vertIt.next();
      distance = pointDistFromLineSegment(p, curPoint, vertex);
      if(distance < minDistance)
      {
        minDistance = distance;
        closestPoint = getClosestEndpoint(p, curPoint, vertex);
      }
      curPoint = vertex;
    }
    
    distance = pointDistFromLineSegment(p, curPoint, destination_location);
    if(distance < minDistance)
    {
      minDistance = distance;
      closestPoint = getClosestEndpoint(p, curPoint, destination_location);
    }
    
    double distToClosestPoint = getDistanceBetweenPoints(p, closestPoint);
    if(distToClosestPoint < max_select_distance)
      selected_point = closestPoint;
    else
      selected_point = null;
    
    return minDistance;
  }
  
  // Get distance from point C to line segment AB
  protected static double pointDistFromLineSegment(Point C, Point segA, Point segB)
  {
    double distance;
    
    double r_numerator = (C.x - segA.x)*(segB.x - segA.x) + 
                         (C.y - segA.y)*(segB.y - segA.y);
    double r_denominator = (segB.x - segA.x)*(segB.x - segA.x) +
                           (segB.y - segA.y)*(segB.y - segA.y);
    
    if(r_denominator == 0)
    { // segA == segB meaning a point was given instead of a line
      double aSquared = (C.x - segA.x)*(C.x - segA.x);
      double bSquared = (C.y - segA.y)*(C.y - segA.y);
      distance = Math.sqrt(aSquared + bSquared);
    }
    else
    {
      double r = r_numerator / r_denominator;
      
      double px = segA.x + r*(segB.x - segA.x);
      double py = segA.y + r*(segB.y - segA.y);
      
      double s = ((segA.y - C.y)*(segB.x - segA.x) - 
              (segA.x - C.x)*(segB.y - segA.y)) / r_denominator;
      
      if(r >= 0 && r <= 1)
      { 
        // Point C is closer to a point on the line segment than it is
        // to the line segment's endpoints
        distance = Math.abs(s)*Math.sqrt(r_denominator);
      }
      else
      {
        // Point C is behind or ahead of the line segment
        // Calculate distances to endpoints of line segment
        // Find the closer of the 2 distances
        double aSquared = (C.x - segA.x)*(C.x - segA.x);
        double bSquared = (C.y - segA.y)*(C.y - segA.y);
        double distSquaredToSegA = aSquared + bSquared;
        
        aSquared = (C.x - segB.x)*(C.x - segB.x);
        bSquared = (C.y - segB.y)*(C.y - segB.y);
        double distSquaredToSegB = aSquared + bSquared;
        
        if(distSquaredToSegA > distSquaredToSegB)
        {
          distance = Math.sqrt(distSquaredToSegB);
        }
        else
        {
          distance = Math.sqrt(distSquaredToSegA);
        }
      }
    }
    
    return distance;
  }
  
  // Get closest point on line segment AB to point C
  protected static Point getClosestPoint(Point C, Point segA, Point segB)
  {
    double r_numerator = (C.x - segA.x)*(segB.x - segA.x) + 
                         (C.y - segA.y)*(segB.y - segA.y);
    double r_denominator = (segB.x - segA.x)*(segB.x - segA.x) +
                           (segB.y - segA.y)*(segB.y - segA.y);
    
    // Closest x,y values for C to the line segment
    double xClosest;
    double yClosest;
    
    if(r_denominator == 0)
    { // segA == segB meaning a point was given instead of a line
      xClosest = segA.x;
      yClosest = segA.y;
    }
    else
    {
      double r = r_numerator / r_denominator;
      
      double px = segA.x + r*(segB.x - segA.x);
      double py = segA.y + r*(segB.y - segA.y);
      
      double s = ((segA.y - C.y)*(segB.x - segA.x) - 
              (segA.x - C.x)*(segB.y - segA.y)) / r_denominator;
      
      if(r >= 0 && r <= 1)
      { 
        // Point C is closer to a point on the line segment than it is
        // to the line segment's endpoints
        xClosest = px;
        yClosest = py;
      }
      else
      {
        // Point C is behind or ahead of the line segment
        // Calculate distances to endpoints of line segment
        // Find the closer of the 2 distances
        double aSquared = (C.x - segA.x)*(C.x - segA.x);
        double bSquared = (C.y - segA.y)*(C.y - segA.y);
        double distSquaredToSegA = aSquared + bSquared;
        
        aSquared = (C.x - segB.x)*(C.x - segB.x);
        bSquared = (C.y - segB.y)*(C.y - segB.y);
        double distSquaredToSegB = aSquared + bSquared;
        
        if(distSquaredToSegA > distSquaredToSegB)
        {
          xClosest = segB.x;
          yClosest = segB.y;
        }
        else
        {
          xClosest = segA.x;
          yClosest = segA.y;
        }
      }
    }
    
    int x = (int)xClosest;
    int y = (int)yClosest;
    
    return new Point(x, y);
  }
  
  protected static Point getClosestEndpoint(Point C, Point segA, Point segB)
  {
    double distToA = getDistanceBetweenPoints(C, segA);
    double distToB = getDistanceBetweenPoints(C, segB);
    
    Point closestEndpoint = segA;
    if(distToB < distToA)
      closestEndpoint = segB;
    
    return closestEndpoint;
  }
  
  protected static double getDistanceBetweenPoints(Point A, Point B)
  {
    double A_aSquared = (B.x - A.x) * (B.x - A.x);
    double A_bSquared = (B.y - A.y) * (B.y - A.y);
    double dist = Math.sqrt(A_aSquared + A_bSquared);
    return dist;
  }
  
  @Override
  public void displayEditGui(MouseEvent evt)
  {
    EditRelationshipDialog dlg = new EditRelationshipDialog(null, true);
    dlg.open(this, evt);
  }
  
  @Override
  public int getMaxX()
  {
    int maxX = source_location.x;
    
    if(destination_location.x > maxX)
      maxX = destination_location.x;
    
    Iterator<Point> vertIt = vertices.iterator();
    while(vertIt.hasNext())
    {
      Point v = vertIt.next();
      if(v.x > maxX)
        maxX = v.x;
    }
    
    return maxX;
  }
  
  @Override
  public int getMaxY()
  {
    int maxY = source_location.y;
    
    if(destination_location.y > maxY)
      maxY = destination_location.y;
    
    Iterator<Point> vertIt = vertices.iterator();
    while(vertIt.hasNext())
    {
      Point v = vertIt.next();
      if(v.y > maxY)
        maxY = v.y;
    }
    
    return maxY;
  }
  
  @Override
  public void setUniqueId(int id)
  {
    unique_id = id;
  }
  
  @Override
  public int getUniqueId()
  {
    return unique_id;
  }
  
  public boolean addVertex(int x, int y)
  {
    double distance;
    boolean foundVertexSpot = false;
    
    Point p = new Point(x, y);
    Point curPoint = source_location;
    
    Iterator<Point> vertIt = vertices.iterator();
    int index = 0;
    while(vertIt.hasNext())
    {
      Point vertex = vertIt.next();
      Point closestPoint = getClosestPoint(p, curPoint, vertex);
      distance = getDistanceBetweenPoints(p, closestPoint);
      if(distance < max_select_distance)
      {
        vertices.add(index, closestPoint);
        foundVertexSpot = true;
        break;
      }
      curPoint = (Point)vertex.clone();
      index++;
    }
    
    if(!foundVertexSpot)
    {
      Point closestPoint = getClosestPoint(p, curPoint, destination_location);
      distance = getDistanceBetweenPoints(p, closestPoint);
      if(distance < max_select_distance)
      {
        vertices.add(index, closestPoint);
        foundVertexSpot = true;
      }
    }
    
    return foundVertexSpot;
  }
  
  @Override
  public void setNearElement(DiagramElement e)
  {
    try 
    {
      if("Class".equals(e.getElementType()))
        tetherToClass((ClassElement)e);
    }
    catch (IOException ex) {
      Logger.getLogger(Relationship.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  protected void tetherToClass(ClassElement e) throws IOException
  {
    if(selected_point == source_location)
      source_tether = new TetherElementData(e, source_location);
    else if(selected_point == destination_location)
      destination_tether = new TetherElementData(e, destination_location);
  }
  
  protected Point getLastVertex()
  {
    Point lastPoint = source_location;
    
    Iterator<Point> vertIt = vertices.iterator();
    while(vertIt.hasNext())
      lastPoint = vertIt.next();
    
    return lastPoint;
  }
  
  protected Point getDeltaToSource()
  {
    Point secondPoint;
    Iterator<Point> vertIt = vertices.iterator();
    if(vertIt.hasNext())
      secondPoint = vertIt.next();
    else
      secondPoint = destination_location;
    
    int x = source_location.x - secondPoint.x;
    int y = source_location.y - secondPoint.y;
    
    return new Point(x, y);
  }
  
  protected Point getDeltaToDestination()
  {
    Point firstPoint = getLastVertex();
    
    int x = destination_location.x - firstPoint.x;
    int y = destination_location.y - firstPoint.y;
    
    return new Point(x, y);
  }
  
  // Return angle between 0 and 2pi
  protected double getAngle(Point delta)
  {
    double hptns = Math.sqrt((delta.x*delta.x) + 
            (delta.y*delta.y));
    
    double angle;
    if((delta.y == 0) && (delta.x == 0))            // Double point (bad)
      angle = 0;
    else if((delta.y <= 0) && (delta.x >= 0))       // Top right quadrant
      angle = Math.asin(Math.abs(delta.x) / hptns);
    else if((delta.y <= 0) && (delta.x <= 0))       // Top left quadrant
      angle = (2*Math.PI) - Math.asin(Math.abs(delta.x) / hptns);
    else if((delta.y >= 0) && (delta.x >= 0))       // Bottom right quadrant
      angle = Math.asin(Math.abs(delta.y) / hptns) + (Math.PI / 2);
    else                                            // Bottom left quadrant
      angle = (3*Math.PI / 2) - Math.asin(Math.abs(delta.y) / hptns);
    
    return angle;
  }
  
  protected double getAngleToSource()
  {
    Point delta = getDeltaToSource();
    return getAngle(delta);
  }
  
  protected double getAngleToDestination()
  {
    Point delta = getDeltaToDestination();
    return getAngle(delta);
  }
  
  @Override
  public boolean isRelationship()
  {
    return true;
  }
  
  @Override
  public void alertDestroyedElement(DiagramElement e)
  {
    if((source_tether != null) && (source_tether.getElement() == e))
      source_tether = null;
    if((destination_tether != null) && (destination_tether.getElement() == e))
      destination_tether = null;
  }
  
  public String getSourceMultiplicity()
  {
    return source_multiplicity;
  }
  
  public void setSourceMultiplicity(String mult)
  {
    source_multiplicity = mult;
  }
  
  public String getDestinationMultiplicity()
  {
    return destination_multiplicity;
  }
  
  public void setDestinationMultiplicity(String mult)
  {
    destination_multiplicity = mult;
  }
  
  public Relationship cloneRelationship(Relationship cloneIntoThis) throws IOException
  {
    if(source_tether != null)
      cloneIntoThis.source_class_uid = 
              source_tether.getElement().getUniqueId();
    else
      cloneIntoThis.source_class_uid = source_class_uid;
      
    if(destination_tether != null)
      cloneIntoThis.destination_class_uid =
              destination_tether.getElement().getUniqueId();
    else
      cloneIntoThis.destination_class_uid = destination_class_uid;
      
    cloneIntoThis.source_multiplicity = source_multiplicity;
    cloneIntoThis.destination_multiplicity = destination_multiplicity;
    cloneIntoThis.source_mult_angle = source_mult_angle;
    cloneIntoThis.source_mult_len   = source_mult_len;
    cloneIntoThis.destination_mult_angle = destination_mult_angle;
    cloneIntoThis.destination_mult_len   = destination_mult_len;
    
    cloneIntoThis.source_location.x = source_location.x;
    cloneIntoThis.source_location.y = source_location.y;
    cloneIntoThis.destination_location.x = destination_location.x;
    cloneIntoThis.destination_location.y = destination_location.y;
    
    Iterator<Point> vertIt = vertices.iterator();
    while(vertIt.hasNext())
    {
      Point vertex = vertIt.next();
      cloneIntoThis.vertices.add(new Point(vertex.x, vertex.y));
    }
    
    cloneIntoThis.unique_id = unique_id;
    
    return cloneIntoThis;
  }
  
  @Override
  public DiagramElement cloneElement() throws IOException
  {
    Relationship clonedRelationship = 
            new Relationship(0, 0);
    clonedRelationship = cloneRelationship(clonedRelationship);
  
    return clonedRelationship;
  }
  
  @Override
  public boolean equivalentTo(DiagramElement e)
  {
    if(e.getElementType().equals(getElementType()))
    {
      Relationship r = (Relationship)e;

      if(!r.source_multiplicity.equals(source_multiplicity)) return false;
      if(!r.destination_multiplicity.equals(destination_multiplicity)) return false;
      if(r.source_mult_angle != source_mult_angle) return false;
      if(r.destination_mult_angle != destination_mult_angle) return false;
      if(r.source_mult_len != source_mult_len) return false;
      if(r.destination_mult_len != destination_mult_len) return false;

      if(r.source_location.x != source_location.x) return false;
      if(r.source_location.y != source_location.y) return false;
      if(r.destination_location.x != destination_location.x) return false;
      if(r.destination_location.y != destination_location.y) return false;
      if(r.unique_id != unique_id) return false;

      if(r.vertices.size() != vertices.size()) return false;
      Iterator<Point> myVertIt = vertices.iterator();
      Iterator<Point> rVertIt  = vertices.iterator();
      while(myVertIt.hasNext())
      {
        Point myVertex = myVertIt.next();
        Point rVertex  = rVertIt.next();
        if(rVertex.x != myVertex.x) return false;
        if(rVertex.y != myVertex.y) return false;
      }

      int mySourceUid = source_class_uid;
      if(source_tether != null)
        mySourceUid = source_tether.getElement().getUniqueId();
      int rSourceUid = r.source_class_uid;
      if(r.source_tether != null)
        rSourceUid = r.source_tether.getElement().getUniqueId();
      if(rSourceUid != mySourceUid) return false;

      int myDestUid = destination_class_uid;
      if(destination_tether != null)
        myDestUid = destination_tether.getElement().getUniqueId();
      int rDestUid = r.destination_class_uid;
      if(r.destination_tether != null)
        rDestUid = r.destination_tether.getElement().getUniqueId();
      if(rDestUid != myDestUid) return false;
    }
    else return false;
    
    // Could not find any differences, must be equivalent
    return true;
  }
  
  public ClassElement getSourceClass()
  {
    ClassElement e = null;
    if(source_tether != null)
      e = (ClassElement)source_tether.getElement();
    return e;
  }
  
  public ClassElement getDestinationClass()
  {
    ClassElement e = null;
    if(destination_tether != null)
      e = (ClassElement)destination_tether.getElement();
    return e;
  }
  
  @Override
  public String accept(ElementCheckerVisitor visitor)
  {
    return visitor.visit(this);
  }
  
}
