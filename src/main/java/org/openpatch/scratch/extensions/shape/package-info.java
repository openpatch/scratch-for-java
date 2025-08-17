package org.openpatch.scratch.extensions.shape;

/**
 * Provides classes for creating and manipulating geometric shapes.
 * <p>
 * This package includes classes for various shapes such as rectangles, circles,
 * and ellipses,
 * along with a Bounds class for defining rectangular bounds.
 * </p>
 *
 * These shapes are currently only used for Hitboxes and collision detection.
 *
 * @see Rectangle
 * @see Circle
 * @see Ellipse
 * @see Bounds
 *
 *      Example usage:
 * 
 *      <pre>
 *      import org.openpatch.scratch.extensions.shape.Rectangle;
 *      import org.openpatch.scratch.extensions.shape.Circle;
 *      import org.openpatch.scratch.extensions.shape.Ellipse;
 *
 *      Shape rect = new Rectangle(10, 20, 100, 50);
 *      Shape circle = new Circle(50, 50, 25);
 *      Shape ellipse = new Ellipse(30, 40, 80, 40);
 *      // Check if a point is inside the rectangle
 *      boolean isInside = rect.contains(15, 25);
 *      // Check if the rectangle intersects with the circle
 *      boolean intersects = rect.intersects(circle);
 *      // Scale the ellipse
 *      Shape scaledEllipse = ellipse.scale(1.5, 1.5);
 *      </pre>
 */
