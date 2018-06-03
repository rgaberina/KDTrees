import java.util.ArrayList;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

/**
 * 
 */

/**
 * @author berina
 *
 */
public class KdTree {

	private Node root;
	private int size;

	private static class Node {
		private Point2D p;      // the point
		private RectHV rect;    // the axis-aligned rectangle corresponding to this node
		private Node lb;        // the left/bottom subtree
		private Node rt;        // the right/top subtree
		private boolean vert;

		public Node(Point2D p, RectHV r, boolean vert) {
			this.p = p;
			this.rect = r;
			this.vert = vert;
		}

		public Point2D getPoint() {
			return p;
		}

		public Node getLeft() {
			return lb;
		}

		public Node getRight() {
			return rt;
		}

		public RectHV getRect() {
			return rect;
		}

		public boolean isVertical() {
			return vert;
		}
	}

	private void checkNullPoint(Point2D p) {
		if (p == null)
			throw new IllegalArgumentException("Point argument cannot be null");
	}


	/** construct an empty set of points */
	public KdTree() { 
		size = 0;
	}

	/** is the set empty? */
	public boolean isEmpty() {
		return size == 0;
	}

	/** number of points in the set */
	public int size() {
		return size;
	}

	/** add the point to the set (if it is not already in the set) */
	public void insert(Point2D p) {
		checkNullPoint(p);
		//		Node n = new Node(p);
		if (root == null) {
			this.root = new Node(p, new RectHV(0, 0, 1, 1), true);
			size++;
		} else
			insertNode(p, root, new RectHV(0, 0, 1, 1), true);
	}

	private Node insertNode (Point2D p, Node root, RectHV rect, boolean vert) {
		if (root == null) {
			size++;
			root = new Node(p, rect, vert);
		}
		if (root.getPoint().compareTo(p) == 0)
			return root;
		if (!root.isVertical()) { // compare y coordinates
			if (p.y() < root.getPoint().y()) { // go left
				RectHV r = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), root.getPoint().y());
				root.lb = insertNode(p, root.getLeft(), r, !vert);
			} else { // go right
				RectHV r = new RectHV(rect.xmin(), root.getPoint().y(), rect.xmax(), rect.ymax());
				root.rt = insertNode(p, root.getRight(), r, !vert);
			}
		} else { // compare x coordinates
			if (p.x() < root.getPoint().x()) { // go left
				RectHV r = new RectHV(rect.xmin(), rect.ymin(), root.getPoint().x(), rect.ymax());
				root.lb = insertNode(p, root.getLeft(), r, !vert);
			} else { // go right
				RectHV r = new RectHV(root.getPoint().x(), rect.ymin(), rect.xmax(), rect.ymax());
				root.rt = insertNode(p, root.getRight(), r, !vert);
			}
		}
		return root;
	}

	/** does the set contain point p? */
	public boolean contains(Point2D p) {
		checkNullPoint(p);
		return containsNode(p, root);
	}

	private boolean containsNode(Point2D p, Node root) {
		if (root == null)
			return false;
		int compare = root.getPoint().compareTo(p);
		if (compare == 0)
			return true;
		if (!root.isVertical()) { // compare y coordinates
			if (p.y() < root.getPoint().y()) // go left
				return containsNode(p, root.getLeft());
			else // go right
				return containsNode(p, root.getRight());
		} else { // compare x coordinates
			if (p.x() < root.getPoint().x()) // go left
				return containsNode(p, root.getLeft());
			else // go right
				return containsNode(p, root.getRight());
		}
	}

	/** draw all points to standard draw  */
	public void draw() {
		StdDraw.setPenColor(StdDraw.BLACK);
		root.getRect().draw();
		draw(root);
	}

	private void draw(Node n) {
		if (n != null) {
			if (n.isVertical()) {
				StdDraw.setPenColor(StdDraw.RED);
				StdDraw.setPenRadius();
				Point2D p1 = new Point2D(n.getPoint().x(), n.getRect().ymin());
				Point2D p2 = new Point2D(n.getPoint().x(), n.getRect().ymax());
				p1.drawTo(p2);
			} else {
				StdDraw.setPenColor(StdDraw.BLUE);
				StdDraw.setPenRadius();
				Point2D p1 = new Point2D(n.getRect().xmin(), n.getPoint().y());
				Point2D p2 = new Point2D(n.getRect().xmax(), n.getPoint().y());
				p1.drawTo(p2);
			}
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.setPenRadius(0.01);
			n.getPoint().draw();
			draw(n.getLeft());
			draw(n.getRight());
		}
	}

	/** all points that are inside the rectangle (or on the boundary)*/
	public Iterable<Point2D> range(RectHV rect) {
		if (rect == null)
			throw new IllegalArgumentException();
		ArrayList<Point2D> points = new ArrayList<>();
		range(rect, root, points);
		return points;
	}

	private void range(RectHV rect, Node n, ArrayList<Point2D> points) {
		if (n == null)
			return;
		if (rect.contains(n.getPoint()))
			points.add(n.getPoint());
		if (n.getLeft() != null && rect.intersects(n.getLeft().getRect()))
			range(rect, n.getLeft(), points);
		if (n.getRight() != null && rect.intersects(n.getRight().getRect()))
			range(rect, n.getRight(), points);
	}

	/** a nearest neighbor in the set to point p; null if the set is empty  */
	public Point2D nearest(Point2D p) {
		checkNullPoint(p);
		if (root == null)
			return null;
		double leastDist = p.distanceSquaredTo(root.getPoint());
		return nearestPoint(p, root, leastDist);
	}

	private Point2D nearestPoint(Point2D p, Node n, double leastDist) {
		if (n == null)
			return null;
		Point2D neighbor = null;
		if (n.getPoint().distanceSquaredTo(p) <= leastDist) {
			leastDist = n.getPoint().distanceSquaredTo(p);
			neighbor = n.getPoint();
		}
		double leftDist = Double.NEGATIVE_INFINITY;
		double rightDist = Double.NEGATIVE_INFINITY;
		if (n.getLeft() != null) 
			leftDist = n.getLeft().getRect().distanceSquaredTo(p);
		if (n.getRight() != null) 
			rightDist = n.getRight().getRect().distanceSquaredTo(p);
		if (leftDist != Double.NEGATIVE_INFINITY && rightDist != Double.NEGATIVE_INFINITY 
				&& rightDist < leftDist) {
			if (n.getRight() != null) {
				rightDist = n.getRight().getRect().distanceSquaredTo(p);
				if (rightDist <= leastDist) {
					Point2D point = nearestPoint(p, n.getRight(), leastDist);
					if (point != null) {
						neighbor = point;
						leastDist = neighbor.distanceSquaredTo(p);
					}
				}
			}
			if (n.getLeft() != null) {
				leftDist = n.getLeft().getRect().distanceSquaredTo(p);	
				if (leftDist <= leastDist) {
					Point2D point = nearestPoint(p, n.getLeft(), leastDist);
					if (point != null) {
						neighbor = point;
						leastDist = neighbor.distanceSquaredTo(p);
					}
				}
			} 
		} else {
			if (n.getLeft() != null) {
				leftDist = n.getLeft().getRect().distanceSquaredTo(p);	
				if (leftDist <= leastDist) {
					Point2D point = nearestPoint(p, n.getLeft(), leastDist);
					if (point != null) {
						neighbor = point;
						leastDist = neighbor.distanceSquaredTo(p);
					}
				}
			} 
			if (n.getRight() != null) {
				rightDist = n.getRight().getRect().distanceSquaredTo(p);
				if (rightDist <= leastDist) {
					Point2D point = nearestPoint(p, n.getRight(), leastDist);
					if (point != null) {
						neighbor = point;
						leastDist = neighbor.distanceSquaredTo(p);
					}
				}
			}
		}
		return neighbor;
	}

	public static void main(String[] args) {
		KdTree kd = new KdTree();
		/*kd.insert(new Point2D(0.15625, 0.5625));
		kd.insert(new Point2D(1.0, 0.75));
		kd.insert(new Point2D(0.4375, 0.46875));
		kd.insert(new Point2D(0.40625, 0.28125));
		kd.insert(new Point2D(0.625, 0.9375));
		kd.insert(new Point2D(0.21875, 0.625));
		kd.insert(new Point2D(0.90625, 0.21875));
		kd.insert(new Point2D(0.1875, 0.71875));
		kd.insert(new Point2D(0.59375, 0.59375));
		kd.insert(new Point2D(0.96875, 0.65625));
		kd.insert(new Point2D(0.71875, 0.0625));
		kd.insert(new Point2D(0.375, 0.8125));
		kd.insert(new Point2D(0.84375, 0.25));
		kd.insert(new Point2D(0.125, 0.1875));
		kd.insert(new Point2D(0.46875, 0.125));
		kd.insert(new Point2D(0.28125, 0.09375));
		kd.insert(new Point2D(0.25, 0.96875));
		kd.insert(new Point2D(0.75, 0.0));
		kd.insert(new Point2D(0.9375, 0.03125));
		kd.insert(new Point2D(0.0, 0.5));*/
		/*kd.insert(new Point2D(0.7, 0.2));
		kd.insert(new Point2D(0.5, 0.4));
		kd.insert(new Point2D(0.2, 0.3));
		kd.insert(new Point2D(0.4, 0.7));
		kd.insert(new Point2D(0.9, 0.6));*/
		kd.insert(new Point2D(0.5, 0.0));
		kd.insert(new Point2D(0.0, 0.0));
		kd.insert(new Point2D(0.5, 0.25));
		kd.insert(new Point2D(1.0, 0.5));
		kd.insert(new Point2D(1.0, 1.0));
		kd.insert(new Point2D(0.25, 0.75));
		kd.insert(new Point2D(0.25, 0.0));
		kd.insert(new Point2D(0.75, 0.0));
		kd.insert(new Point2D(0.0, 0.5));
		kd.insert(new Point2D(0.5, 0.75));
		/*String filename = "circle10.txt";
        In in = new In(filename);
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kd.insert(p);
        }*/
		System.out.println(kd.size());
		System.out.println(kd.nearest(new Point2D(0.25, 1.0)));
		//		kd.draw();
		//		System.out.println(kd.range(new RectHV(0.03125, 0.5625, 0.53125, 0.90625)));
	}

}
