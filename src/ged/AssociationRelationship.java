/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ged;

import static ged.Util.getValueFromTag;
import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Comp
 */
public class AssociationRelationship extends Relationship
{
  // Cache some variables to avoid excess calculations
  private final Point prev_last_vertex;
  private final Point prev_destination;
  private TetherElementData association_tether;
  private int association_class_uid;
  private final Point association_class_location;
  private final Point association_relationship_location;
  private final ArrayList<Point> ass_vertices;
  private double percent_length;
  
  public AssociationRelationship(int x, int y) throws IOException
  {
    super(x, y);
    int lineLength = Integer.parseInt(cfg_mgr.
            getConfigValue(ConfigurationManager.DFLT_RLTNSHP_LEN));
    
    prev_last_vertex = new Point(0,0);
    prev_destination = new Point(0,0);
    association_relationship_location = new Point(x + (lineLength / 2), y);
    association_class_location = new Point(x + (lineLength / 2), 
                                           y + (lineLength / 2));
    association_tether = null;
    association_class_uid = 0;
    ass_vertices = new ArrayList<>();
    
    percent_length = -1.0;
  }
  
  @Override
  public String getElementType()
  {
    return "Association";
  }
  
  @Override
  public void draw(Graphics g)
  {
    Point curPoint = association_relationship_location;
    
    // Go through each vertex drawing a line to it
    Iterator<Point> vertIt = ass_vertices.iterator();
    while(vertIt.hasNext())
    {
      Point vertex = vertIt.next();
      drawDashedLine(g, new Point(curPoint.x, curPoint.y),
              new Point(vertex.x, vertex.y));
      int vertexX = vertex.x - (vertex_diameter/2);
      int vertexY = vertex.y - (vertex_diameter/2);
      g.fillOval(vertexX, vertexY, vertex_diameter, vertex_diameter);
      curPoint = vertex;
    }
    
    // Draw final line to class destination
    drawDashedLine(g, curPoint, association_class_location);
    
    super.draw(g);
  }
  
  @Override
  protected void drawLine(Graphics g, Point A, Point B)
  {
    g.drawLine(A.x, A.y, B.x, B.y);
  }
  
  @Override
  protected void drawEndpoints(Graphics g)
  {
    int destX = destination_location.x;
    int destY = destination_location.y;
    
    Point last_vertex = getLastVertex();
    // Check if vertex or destination changed
    if((prev_last_vertex.x != last_vertex.x)          ||
       (prev_last_vertex.y != last_vertex.y)          ||
       (prev_destination.x != destination_location.x) ||
       (prev_destination.y != destination_location.y))
    { // If there is a change, re-calculate diamond
      prev_last_vertex.x = last_vertex.x;
      prev_last_vertex.y = last_vertex.y;
      prev_destination.x = destination_location.x;
      prev_destination.y = destination_location.y;

      // TODO: calculate
    }

    // TODO: Draw something
  }
  
  @Override
  public double getDistanceFrom(int x, int y)
  {
    double minDistance = super.getDistanceFrom(x, y);
    double distance;
    
    Point p = new Point(x, y);
    Point closestPoint = association_relationship_location;
    if(selected_point != null)
      closestPoint = selected_point;
    
    Point curPoint = association_relationship_location;
    Iterator<Point> vertIt = ass_vertices.iterator();
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
    
    distance = pointDistFromLineSegment(p, curPoint, association_class_location);
    if(distance < minDistance)
    {
      minDistance = distance;
      closestPoint = getClosestEndpoint(p, curPoint, association_class_location);
    }
    
    double distToClosestPoint = getDistanceBetweenPoints(p, closestPoint);
    if(distToClosestPoint < max_select_distance)
      selected_point = closestPoint;
    else
      selected_point = null;
    
    return minDistance;
  }
  
  @Override
  public boolean addVertex(int x, int y)
  {
    double distance;
    boolean foundVertexSpot = super.addVertex(x, y);
    
    if(!foundVertexSpot)
    {
      Point p = new Point(x, y);
      Point curPoint = association_relationship_location;

      Iterator<Point> vertIt = ass_vertices.iterator();
      int index = 0;
      while(vertIt.hasNext())
      {
        Point vertex = vertIt.next();
        Point closestPoint = getClosestPoint(p, curPoint, vertex);
        distance = getDistanceBetweenPoints(p, closestPoint);
        if(distance < max_select_distance)
        {
          ass_vertices.add(index, closestPoint);
          foundVertexSpot = true;
          break;
        }
        curPoint = (Point)vertex.clone();
        index++;
      }

      if(!foundVertexSpot)
      {
        Point closestPoint = getClosestPoint(p, curPoint, 
                association_class_location);
        distance = getDistanceBetweenPoints(p, closestPoint);
        if(distance < max_select_distance)
        {
          ass_vertices.add(index, closestPoint);
          foundVertexSpot = true;
        }
      }
    }
    
    return foundVertexSpot;
  }
  
  public static DiagramElement fromPersistentRepresentation(String s) throws IOException
  {
    AssociationRelationship r = new AssociationRelationship(0, 0);
    int assUID = Integer.parseInt(getValueFromTag(s, "assUID"));
    r.association_class_uid = assUID;
    
    Point assClassPoint = new Point(0, 0);
    String assClassLocStr = getValueFromTag(s, "associationClassLoc");
    if(!"".equals(assClassLocStr))
    {
      String[] assLocArr = assClassLocStr.split(",");
      if(assLocArr.length > 1)
      {
        assClassPoint.x = Integer.parseInt(assLocArr[0]);
        assClassPoint.y = Integer.parseInt(assLocArr[1]);
      }
      r.association_class_location.x = assClassPoint.x;
      r.association_class_location.y = assClassPoint.y;
    }
    
    Point assRelPoint = new Point(0, 0);
    String assRelLocStr = getValueFromTag(s, "associationRelationshipLoc");
    if(!"".equals(assRelLocStr))
    {
      String[] assLocArr = assRelLocStr.split(",");
      if(assLocArr.length > 1)
      {
        assRelPoint.x = Integer.parseInt(assLocArr[0]);
        assRelPoint.y = Integer.parseInt(assLocArr[1]);
      }
      r.association_relationship_location.x = assRelPoint.x;
      r.association_relationship_location.y = assRelPoint.y;
    }
    
    // Get all vertices
    String[] vert_pieces = s.split("<assVertex>");
    for(int i = 0; i < vert_pieces.length; i++)
    {
      if(vert_pieces[i].contains("</assVertex>"))
      {
        vert_pieces[i] = "<assVertex>" + vert_pieces[i];
        vert_pieces[i] = getValueFromTag(vert_pieces[i], "assVertex");
        Point vertex = new Point(0, 0);
        String[] vertArr = vert_pieces[i].split(",");
        if(vertArr.length > 1)
        {
          vertex.x = Integer.parseInt(vertArr[0]);
          vertex.y = Integer.parseInt(vertArr[1]);
          r.ass_vertices.add(vertex);
        }
      }
    }
    
    return fromPersistentRepresentation(s, r);
  }
  
  @Override
  protected void updateTethers() throws IOException
  {
    boolean changedLocation = false;
    Point source = new Point(source_location.x, source_location.y);
    Point dest = new Point(destination_location.x, destination_location.y);
    super.updateTethers();
    if((source.x != source_location.x)     || 
        (source.y != source_location.y)    ||
        (dest.x != destination_location.x) || 
        (dest.y != destination_location.y))
    {
      changedLocation = true;
    }
    
    if((association_tether == null) && association_class_uid != 0)
    {
      DiagramElement e = diag_controller.getUniqueElement(association_class_uid);
      if(e != null && "Class".equals(e.getElementType()))
        association_tether = 
                new TetherElementData((ClassElement)e, association_class_location);
      else
        System.out.println("WARN:Relationship:Attached UID was not a class!");
      association_class_uid = 0;
    }
    if(association_tether != null)
    {
      Point newLoc = association_tether.getPoint();
      association_class_location.x = newLoc.x;
      association_class_location.y = newLoc.y;
      changedLocation = true;
    }
    
    if(changedLocation)
      snapToMainLine();
  }
  
  @Override
  public void setLocation(Point loc)
  {
    Point ref = new Point(source_location.x, source_location.y);
    super.setLocation(loc);
    if(selected_point != null)
    {
      if(selected_point == association_class_location)
        association_tether = null;
      
      Iterator<Point> vertIt = ass_vertices.iterator();
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
      association_tether = null;
      
      int deltaX = loc.x - ref.x;
      int deltaY = loc.y - ref.y;
      
      association_relationship_location.x += deltaX;
      association_relationship_location.y += deltaY;
      
      Iterator<Point> vertIt = ass_vertices.iterator();
      while(vertIt.hasNext())
      {
        Point vertex = vertIt.next();
        vertex.x += deltaX;
        vertex.y += deltaY;
      }
      
      association_class_location.x += deltaX;
      association_class_location.y += deltaY;
    }
    
    snapToMainLine();
  }
  
  private void snapToMainLine()
  {
    Point curPoint = source_location;
    double distance;
    double leastDistance = 10000000;
    Point closestSeg1 = source_location;
    Point closestSeg2 = destination_location;
    Iterator<Point> vertIt = vertices.iterator();
    while(vertIt.hasNext())
    {
      Point vertex = vertIt.next();
      distance = pointDistFromLineSegment(association_relationship_location,
              curPoint, vertex);
      if(distance < leastDistance)
      {
        leastDistance = distance;
        closestSeg1 = curPoint;
        closestSeg2 = vertex;
      }
      curPoint = vertex;
    }
    
    distance = pointDistFromLineSegment(association_relationship_location,
            curPoint, destination_location);
    if(distance < leastDistance)
    {
      closestSeg1 = curPoint;
      closestSeg2 = destination_location;
    }
    
    Point closestPoint = getClosestPoint(association_relationship_location,
            closestSeg1, closestSeg2);
    association_relationship_location.x = closestPoint.x;
    association_relationship_location.y = closestPoint.y;
  }
  
  @Override
  public String getPersistentRepresentation()
  {
    String rep = super.getPersistentRepresentation();
    
    int ass_uid = 0;
    if(association_tether != null)
      ass_uid = association_tether.getElement().getUniqueId();
    rep += "<assUID>" + ass_uid + "</assUID>";
    rep += "<associationClassLoc>" + association_class_location.x + "," + 
            association_class_location.y + "</associationClassLoc>";
    rep += "<associationRelationshipLoc>" + 
            association_relationship_location.x + "," + 
            association_relationship_location.y + 
            "</associationRelationshipLoc>";
    
    Iterator<Point> vertIt = ass_vertices.iterator();
    while(vertIt.hasNext())
    {
      Point p = vertIt.next();
      rep += "<assVertex>" + p.x + "," + p.y + "</assVertex>";
    }
    
    return rep;
  }
  
  @Override
  protected void tetherToClass(ClassElement e) throws IOException
  {
    super.tetherToClass(e);
    if(selected_point == association_class_location)
      association_tether = new TetherElementData(e, association_class_location);
  }
  
  @Override
  public void alertDestroyedElement(DiagramElement e)
  {
    super.alertDestroyedElement(e);
    if((association_tether != null) && (association_tether.getElement() == e))
      association_tether = null;
  }
}
