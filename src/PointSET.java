import java.util.ArrayList;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

/**
 * 
 */

/**
 * @author berina
 *
 */
public class PointSET {
	
	private SET<Point2D> set;
	
	private void checkNullPoint(Point2D p) {
		if (p == null)
			throw new IllegalArgumentException("Point argument cannot be null");
	}


	/** construct an empty set of points */
	public PointSET() { 
		set = new SET<>();
	}

	/** is the set empty? */
	public boolean isEmpty() {
		if (set == null)
			return true;
		return set.isEmpty();
	}

	/** number of points in the set */
	public int size() {
		if (set == null)
			return 0;
		return set.size();
	}

	/** add the point to the set (if it is not already in the set) */
	public void insert(Point2D p) {
		checkNullPoint(p);
		if (set == null)
			return;
		set.add(p);
	}

	/** does the set contain point p? */
	public boolean contains(Point2D p) {
		if (set == null)
			return true;
		return set.contains(p);
	}

	/** draw all points to standard draw  */
	public void draw()  {
		for (Point2D p : set) {
			p.draw();
		}
	}

	/** all points that are inside the rectangle (or on the boundary)*/
	public Iterable<Point2D> range(RectHV rect) {
		ArrayList<Point2D> array = new ArrayList<>();
		for (Point2D p : set) {
			if (rect.contains(p))
				array.add(p);
		}
		return array;
	}

	/** a nearest neighbor in the set to point p; null if the set is empty  */
	public Point2D nearest(Point2D p) {
		if (set.isEmpty())
			return null;
		Point2D nearest = set.min();
		double leastDist = p.distanceSquaredTo(nearest);
		for (Point2D point : set) {
			double dist = p.distanceSquaredTo(point);
			if (dist < leastDist) {
				leastDist = dist;
				nearest = point;
			}
		}
		return nearest;
	}

	public static void main(String[] args) {

	}

}
