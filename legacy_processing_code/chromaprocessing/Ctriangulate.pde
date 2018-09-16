
import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*;
import java.io.*;





public static class Vertex {
  public PVector position;
  public float fieldValue;

  Vertex() {
    position = null;
  }

  Vertex(PVector pos) {
    position = pos;
  }

  Vertex(PVector pos, float value) {
    position = pos;
    fieldValue = value;
  }

  public void position(PVector pos) {
    position = pos;
  }

  public PVector position() {
    return position;
  }

  public void fieldValue(float value) {
    fieldValue = value;
  }
  public float fieldValue() {
    return fieldValue;
  }
}

public static class Triangle {

  public PVector p1, p2, p3;

  public Triangle() { 
    p1=null;
    p2=null;
    p3=null;
  }

  public Triangle(PVector p1, PVector p2, PVector p3) {
    this.p1 = p1;
    this.p2 = p2;
    this.p3 = p3;
  }

  public boolean sharesVertex(Triangle other) {
    return p1 == other.p1 || p1 == other.p2 || p1 == other.p3 ||
      p2 == other.p1 || p2 == other.p2 || p2 == other.p3 || 
      p3 == other.p1 || p3 == other.p2 || p3 == other.p3;
  }  

  // isIn returns true if "position" is in the triangle
  // nbesnard, 2013
  boolean isIn(PVector position) {
    boolean posIsIn = true;
    PVector[] segments = {
      p1, p2, p3
    };
    PVector[] vectors = new PVector[3];
    PVector[] normVectors = new PVector[3];
    float[] scalar = new float[3];
    for (int i=0; i<3; i++) {
      PVector vector = PVector.sub(segments[i], position);
      vectors[i] = vector;
      if (i>0) {
        normVectors[i-1] = vectors[i-1].get();
        normVectors[i-1] = normVectors[i-1].cross(vectors[i]);
        scalar[i-1] = PVector.dot(normVectors[i-1], normalVect());
      }
    }
    normVectors[segments.length-1] = vectors[2].get();
    normVectors[segments.length-1] = normVectors[2].cross(vectors[0]);
    scalar[segments.length-1] = PVector.dot(normVectors[2], normalVect());

    float prod;
    for (int i=1; i<4; i++) {
      if (i == 3) prod = scalar[i-1] * scalar[0];
      else prod = scalar[i-1] * scalar[i];
      if (prod < 0) {
        posIsIn = false;
        // break;
      }
      // else { posIsIn = true;}
    }
    return posIsIn;
  }

  PVector normalVect() {
    PVector segm1 = p1.get();
    PVector segm2 = p2.get();
    segm1.mult(-1);

    PVector normVect = segm1.cross(segm2);
    normVect.normalize();

    return normVect;
  }
}



private static class XComparator implements Comparator<PVector> {
  public int compare(PVector p1, PVector p2) {
    if (p1.x < p2.x) {
      return -1;
    } else if (p1.x > p2.x) {
      return 1;
    } else {
      return 0;
    }
  }
}

public static class CTriangulator {
  private ArrayList<Triangle> m_Triangles;

  ///////////////////////////////////////////////////////////
  // Constructor
  public void CTriangulator() {
    m_Triangles = new ArrayList<Triangle>();
  }

  CTriangulator(ArrayList<PVector> pxyz) {
    m_Triangles = triangulate(pxyz);
  }

  ///////////////////////////////////////////////////////////
  public ArrayList<Triangle> getTriangles() {
    return m_Triangles;
  }

  /*
    Return TRUE if a point (xp,yp) is inside the circumcircle made up
   of the points (x1,y1), (x2,y2), (x3,y3)
   The circumcircle centre is returned in (xc,yc) and the radius r
   NOTE: A point on the edge is inside the circumcircle
   		 */
  private boolean circumCircle(PVector p, Triangle t, PVector circle) {

    float m1, m2, mx1, mx2, my1, my2;
    float dx, dy, rsqr, drsqr;

    /* Check for coincident points */
    if ( PApplet.abs(t.p1.y-t.p2.y) < PApplet.EPSILON && PApplet.abs(t.p2.y-t.p3.y) < PApplet.EPSILON ) {
      System.err.println("CircumCircle: Points are coincident.");
      return false;
    }

    if ( PApplet.abs(t.p2.y-t.p1.y) < PApplet.EPSILON ) {
      m2 = - (t.p3.x-t.p2.x) / (t.p3.y-t.p2.y);
      mx2 = (t.p2.x + t.p3.x) / 2.0f;
      my2 = (t.p2.y + t.p3.y) / 2.0f;
      circle.x = (t.p2.x + t.p1.x) / 2.0f;
      circle.y = m2 * (circle.x - mx2) + my2;
    } else if ( PApplet.abs(t.p3.y-t.p2.y) < PApplet.EPSILON ) {
      m1 = - (t.p2.x-t.p1.x) / (t.p2.y-t.p1.y);
      mx1 = (t.p1.x + t.p2.x) / 2.0f;
      my1 = (t.p1.y + t.p2.y) / 2.0f;
      circle.x = (t.p3.x + t.p2.x) / 2.0f;
      circle.y = m1 * (circle.x - mx1) + my1;
    } else {
      m1 = - (t.p2.x-t.p1.x) / (t.p2.y-t.p1.y);
      m2 = - (t.p3.x-t.p2.x) / (t.p3.y-t.p2.y);
      mx1 = (t.p1.x + t.p2.x) / 2.0f;
      mx2 = (t.p2.x + t.p3.x) / 2.0f;
      my1 = (t.p1.y + t.p2.y) / 2.0f;
      my2 = (t.p2.y + t.p3.y) / 2.0f;
      circle.x = (m1 * mx1 - m2 * mx2 + my2 - my1) / (m1 - m2);
      circle.y = m1 * (circle.x - mx1) + my1;
    }

    dx = t.p2.x - circle.x;
    dy = t.p2.y - circle.y;
    rsqr = dx*dx + dy*dy;
    circle.z = PApplet.sqrt(rsqr);

    dx = p.x - circle.x;
    dy = p.y - circle.y;
    drsqr = dx*dx + dy*dy;

    return drsqr <= rsqr;
  }


  /*
    Triangulation subroutine
   Takes as input vertices (PVectors) in ArrayList pxyz
   Returned is a list of triangular faces in the ArrayList triangles 
   These triangles are arranged in a consistent clockwise order.
   		 */
  public ArrayList<Triangle> triangulate( ArrayList<PVector> pxyz ) {
    // sort vertex array in increasing x values
    Collections.sort(pxyz, new XComparator());    

    /*
      Find the maximum and minimum vertex bounds.
     This is to allow calculation of the bounding triangle
     			 */
    float xmin = ((PVector)pxyz.get(0)).x;
    float ymin = ((PVector)pxyz.get(0)).y;
    float xmax = xmin;
    float ymax = ymin;

    Iterator<PVector> pIter = pxyz.iterator();
    while (pIter.hasNext ()) {
      PVector p = (PVector)pIter.next();
      if (p.x < xmin) xmin = p.x;
      if (p.x > xmax) xmax = p.x;
      if (p.y < ymin) ymin = p.y;
      if (p.y > ymax) ymax = p.y;
    }

    float dx = xmax - xmin;
    float dy = ymax - ymin;
    float dmax = (dx > dy) ? dx : dy;
    float xmid = (xmax + xmin) / 2.0f;
    float ymid = (ymax + ymin) / 2.0f;

    ArrayList<Triangle> triangles = new ArrayList<Triangle>(); // for the Triangles
    HashSet<Triangle> complete = new HashSet<Triangle>(); // for complete Triangles

    /*
      Set up the supertriangle
     This is a triangle which encompasses all the sample points.
     The supertriangle coordinates are added to the end of the
     vertex list. The supertriangle is the first triangle in
     the triangle list.
     			 */
    Triangle superTriangle = new Triangle();
    superTriangle.p1 = new PVector( xmid - 2.0f * dmax, ymid - dmax, 0.0f );
    superTriangle.p2 = new PVector( xmid, ymid + 2.0f * dmax, 0.0f );
    superTriangle.p3 = new PVector( xmid + 2.0f * dmax, ymid - dmax, 0.0f );
    triangles.add(superTriangle);

    /*
      Include each point one at a time into the existing mesh
     			 */
    ArrayList<Edge> edges = new ArrayList<Edge>();
    pIter = pxyz.iterator();
    while (pIter.hasNext ()) {

      PVector p = (PVector)pIter.next();

      edges.clear();

      /*
        Set up the edge buffer.
       If the point (xp,yp) lies inside the circumcircle then the
       three edges of that triangle are added to the edge buffer
       and that triangle is removed.
       				 */
      PVector circle = new PVector();

      for (int j = triangles.size ()-1; j >= 0; j--) {

        Triangle t = (Triangle)triangles.get(j);
        if (complete.contains(t)) {
          continue;
        }

        boolean inside = circumCircle( p, t, circle );

        if (circle.x + circle.z < p.x) {
          complete.add(t);
        }
        if (inside) {
          edges.add(new Edge(t.p1, t.p2));
          edges.add(new Edge(t.p2, t.p3));
          edges.add(new Edge(t.p3, t.p1));
          triangles.remove(j);
        }
      }

      /*
        Tag multiple edges
       Note: if all triangles are specified anticlockwise then all
       interior edges are opposite pointing in direction.
       				 */
      for (int j=0; j<edges.size ()-1; j++) {
        Edge e1 = (Edge)edges.get(j);
        for (int k=j+1; k<edges.size (); k++) {
          Edge e2 = (Edge)edges.get(k);
          if (e1.p1 == e2.p2 && e1.p2 == e2.p1) {
            e1.p1 = null;
            e1.p2 = null;
            e2.p1 = null;
            e2.p2 = null;
          }
          /* Shouldn't need the following, see note above */
          if (e1.p1 == e2.p1 && e1.p2 == e2.p2) {
            e1.p1 = null;
            e1.p2 = null;
            e2.p1 = null;
            e2.p2 = null;
          }
        }
      }

      /*
        Form new triangles for the current point
       Skipping over any tagged edges.
       All edges are arranged in clockwise order.
       				 */
      for (int j=0; j < edges.size (); j++) {
        Edge e = (Edge)edges.get(j);
        if (e.p1 == null || e.p2 == null) {
          continue;
        }
        triangles.add(new Triangle(e.p1, e.p2, p));
      }
    }

    /*
      Remove triangles with supertriangle vertices
     			 */
    for (int i = triangles.size ()-1; i >= 0; i--) {
      Triangle t = (Triangle)triangles.get(i);
      if (t.sharesVertex(superTriangle)) {
        triangles.remove(i);
      }
    }

    return triangles;
  }

  ///////////////////////////////////////////////////////////
  // private class Edge
  private static class Edge {
    public PVector p1, p2;

    public Edge() {
      p1=null;
      p2=null;
    }

    public Edge(PVector p1, PVector p2) {
      this.p1 = p1;
      this.p2 = p2;
    }
  }
}
