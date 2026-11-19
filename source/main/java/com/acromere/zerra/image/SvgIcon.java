package com.acromere.zerra.image;

import com.acromere.zerra.color.Paints;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A {@link VectorIcon} defined by a set of {@link IconAction icon actions}.
 *
 * <p>
 * An {@code SvgIcon} is an icon whose appearance is produced by interpreting a
 * sequence of vector commands rather than a single image resource.
 *
 * <p>
 * The drawing model is expressed through the nested action types
 * {@link IconAction}, {@link RenderAction}, {@link Fill}, {@link Draw},
 * {@link Clip}, {@link Xform} and {@link Restore}. A {@code RenderAction}
 * carries a {@link Fill} together with a {@link Clip} and one or more
 * {@link Draw} primitives; {@link Xform} supplies a per-action transform and
 * {@link Restore} resets the current transform.
 *
 * @see VectorIcon
 * @see RenderedIcon
 */

public class SvgIcon extends VectorIcon {

	/**
	 * The number of radians per degree, used to convert degree-based angles to
	 * the radian values expected by the trigonometric functions.
	 */
	public static final double RADIANS_PER_DEGREE = Math.PI / 180.0;

	/**
	 * A Paint object to denote the use of the primary color. The actual color is of no interest.
	 */
	protected static final Paint PRIMARY = Paint.valueOf( "#000000" );

	/**
	 * A Paint object to denote the use of the secondary color. The actual color is of no interest.
	 */
	protected static final Paint SECONDARY = Paint.valueOf( "#000000" );

	/**
	 * The sequence of {@link IconAction icon actions} that define this icon.
	 */
	private final List<IconAction> actions;

	/**
	 * Constructs an empty SVG icon using the default grid dimensions.
	 */
	public SvgIcon() {
		this( DEFAULT_GRID, DEFAULT_GRID );
	}

	/**
	 * Constructs an empty SVG icon using the given grid dimensions.
	 *
	 * @param gridX the X dimension of the icon grid
	 * @param gridY the Y dimension of the icon grid
	 */
	protected SvgIcon( double gridX, double gridY ) {
		this( gridX, gridY, null );
	}

	/**
	 * Constructs an SVG icon from the given grid dimensions and SVG path, which
	 * is added as the initial fill.
	 *
	 * @param gridX   the X dimension of the icon grid
	 * @param gridY   the Y dimension of the icon grid
	 * @param svgPath the SVG path to fill, or {@code null} for an empty icon
	 */
	protected SvgIcon( double gridX, double gridY, String svgPath ) {
		super( gridX, gridY );
		actions = new CopyOnWriteArrayList<>();
		reset();
		fill( svgPath );
	}

	/**
	 * Create an SVG string for an arc. Because SVG path arcs are a bit
	 * cumbersome, this method simplifies the creating of an arc based
	 * on the center start and extent.
	 */
	public static String arc( double cx, double cy, double r, double start, double extent ) {
		return arc( false, cx, cy, r, start, extent );
	}

	/**
	 * Create an SVG string for an arc. Because SVG path arcs are a bit
	 * cumbersome, this method simplifies the creating of an arc based
	 * on the center start and extent.
	 */
	public static String arc( boolean moveToStart, double cx, double cy, double r, double start, double extent ) {
		int ccw = extent < 0 ? 0 : 1;
		double end = start + extent;
		double sx = cx + r * Math.cos( start * RADIANS_PER_DEGREE );
		double sy = cy + r * Math.sin( start * RADIANS_PER_DEGREE );
		double ex = cx + r * Math.cos( end * RADIANS_PER_DEGREE );
		double ey = cy + r * Math.sin( end * RADIANS_PER_DEGREE );

		String icon = "";
		if( moveToStart ) {
			icon += "M" + sx + "," + sy + " ";
		}
		icon += "A" + r + "," + r + " 0 0 " + ccw + " " + ex + "," + ey;

		return icon;
	}

	/**
	 * Create an SVG string for a circle. Because SVG path arcs are a bit
	 * cumbersome, this method simplifies the creating of a full circle.
	 *
	 * @param cx The circle center X coordinate
	 * @param cy The circle center Y coordinate
	 * @param r The circle radius
	 * @return The SVG string for the circle
	 */
	public static String circle( double cx, double cy, double r ) {
		return "M" + (cx + r) + "," + cy + " A" + r + "," + r + " 0 0 0 " + (cx - r) + "," + cy + " A" + r + "," + r + " 0 0 0 " + (cx + r) + "," + cy;
	}

	/**
	 * Create an SVG string for an ellipse. Because SVG path arcs are a bit
	 * cumbersome, this method simplifies the creating of a full ellipse.
	 *
	 * @param cx The ellipse center X coordinate
	 * @param cy The ellipse center Y coordinate
	 * @param rx The ellipse X radius
	 * @param ry The ellipse Y radius
	 * @return The SVG string for the ellipse
	 */
	public static String ellipse( double cx, double cy, double rx, double ry ) {
		return ellipse( cx, cy, rx, ry, 0 );
	}

	/**
	 * Create an SVG string for an ellipse. Because SVG path arcs are a bit
	 * cumbersome, this method simplifies the creating of a full ellipse.
	 *
	 * @param cx The ellipse center X coordinate
	 * @param cy The ellipse center Y coordinate
	 * @param rx The ellipse X radius
	 * @param ry The ellipse Y radius
	 * @return The SVG string for the ellipse
	 */
	public static String ellipse( double cx, double cy, double rx, double ry, double rotate ) {
		return "M" + (cx + rx) + "," + (cy + ry) + " A" + rx + "," + ry + " " + rotate + " 0 0 " + (cx - rx) + "," + (cy - ry) + " A" + rx + "," + ry + " " + rotate + " 0 0 " + (cx + rx) + "," + (cy + ry);
	}

	/**
	 * Entry point for the package's interactive proof harness.
	 *
	 * @param commands the command-line arguments, ignored
	 * @see Proof
	 */
	public static void main( String[] commands ) {
		Proof.proof( new SvgIcon( 24, 24, "M20.5 6c-2.61.7-5.67 1-8.5 1s-5.89-.3-8.5-1L3 8c1.86.5 4 .83 6 1v13h2v-6h2v6h2V9c2-.17 4.14-.5 6-1l-.5-2zM12 6c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z" ) );
	}

	/**
	 * This method is used to define the rendering steps where it is problematic
	 * to define them in the constructor. This is particularly necessary when
	 * using values that come from the style class like colors and sizes.
	 */
	protected void define() {
	}

	/**
	 * Restores the icon to the transform state in effect before the most
	 * recent clip or transform.
	 */
	public void restore() {
		actions.add( new Restore() );
	}

	/**
	 * Clips subsequent drawing to the given SVG path.
	 *
	 * @param path the SVG path to clip to
	 * @return this {@link SvgIcon}
	 */
	public SvgIcon clip( String path ) {
		actions.add( new Clip( path, true ) );
		return this;
	}

	/**
	 * Applies the given transform to subsequent drawing.
	 *
	 * @param transform the transform to apply
	 * @return this {@link SvgIcon}
	 */
	public SvgIcon transform( Transform transform ) {
		actions.add( new Xform( transform ) );
		return this;
	}

	/**
	 * Add a filled SVG path entry to the icon.
	 *
	 * @param path The SVG path to fill
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon fill( String path ) {
		return fill( path, null, null );
	}

	/**
	 * Add a filled SVG path entry to the icon using the given paint.
	 *
	 * @param path  The SVG path to fill
	 * @param paint The paint to fill with
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon fill( String path, Paint paint ) {
		return fill( path, paint, null );
	}

	/**
	 * Add a filled SVG path entry to the icon using the given fill rule.
	 *
	 * @param path The SVG path to fill
	 * @param rule The fill rule to use
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon fill( String path, FillRule rule ) {
		return fill( path, null, rule );
	}

	/**
	 * Add a filled SVG path entry to the icon using the given paint and fill
	 * rule.
	 *
	 * @param path  The SVG path to fill
	 * @param paint The paint to fill with
	 * @param rule  The fill rule to use
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon fill( String path, Paint paint, FillRule rule ) {
		return fill( path, null, paint, rule );
	}

	/**
	 * Add a filled SVG path entry to the icon using the given font, paint and
	 * fill rule.
	 *
	 * @param path  The SVG path to fill
	 * @param font  The font to use when the path is text
	 * @param paint The paint to fill with
	 * @param rule  The fill rule to use
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon fill( String path, Font font, Paint paint, FillRule rule ) {
		if( path != null ) {
			actions.add( new Fill( path, true, font, paint, rule ) );
		}
		return this;
	}

	/**
	 * Add a filled text entry to the icon.
	 *
	 * @param path The text to fill
	 * @return This {@link SvgIcon}
	 */
	@SuppressWarnings( "unused" )
	public SvgIcon fillText( String path ) {
		return fillText( path, null, null );
	}

	/**
	 * Add a filled text entry to the icon using the given paint.
	 *
	 * @param path  The text to fill
	 * @param paint The paint to fill with
	 * @return This {@link SvgIcon}
	 */
	@SuppressWarnings( "unused" )
	public SvgIcon fillText( String path, Paint paint ) {
		return fillText( path, paint, null );
	}

	/**
	 * Add a filled text entry to the icon using the given fill rule.
	 *
	 * @param path The text to fill
	 * @param rule The fill rule to use
	 * @return This {@link SvgIcon}
	 */
	@SuppressWarnings( "unused" )
	public SvgIcon fillText( String path, FillRule rule ) {
		return fillText( path, null, rule );
	}

	/**
	 * Add a filled text entry to the icon using the given paint and fill rule.
	 *
	 * @param path  The text to fill
	 * @param paint The paint to fill with
	 * @param rule  The fill rule to use
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon fillText( String path, Paint paint, FillRule rule ) {
		return fillText( path, null, paint, rule );
	}

	/**
	 * Add a filled text entry to the icon using the given font, paint and fill
	 * rule.
	 *
	 * @param path  The text to fill
	 * @param font  The font to use
	 * @param paint The paint to fill with
	 * @param rule  The fill rule to use
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon fillText( String path, Font font, Paint paint, FillRule rule ) {
		if( path != null ) {
			actions.add( new Fill( path, false, font, paint, rule ) );
		}
		return this;
	}

	/**
	 * Add a filled text entry to the icon.
	 *
	 * @param path The text to fill
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon text( String path ) {
		return text( path, null, null );
	}

	/**
	 * Add a text entry to the icon using the given paint.
	 *
	 * @param path  The text to add
	 * @param paint The paint to use
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon text( String path, Paint paint ) {
		return text( path, paint, null );
	}

	/**
	 * Add a text entry to the icon using the given fill rule.
	 *
	 * @param path The text to add
	 * @param rule The fill rule to use
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon text( String path, FillRule rule ) {
		return text( path, null, rule );
	}

	/**
	 * Add a text entry to the icon using the given paint and fill rule.
	 *
	 * @param path  The text to add
	 * @param paint The paint to use
	 * @param rule  The fill rule to use
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon text( String path, Paint paint, FillRule rule ) {
		return fillText( path, paint, rule );
	}

	/**
	 * Add a stroked SVG path entry to the icon.
	 *
	 * @param path The SVG path to fill
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon draw( String path ) {
		return draw( path, null );
	}

	/**
	 * Add a stroked SVG path entry to the icon using the given paint.
	 *
	 * @param path  The SVG path to draw
	 * @param paint The paint to stroke with
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon draw( String path, Paint paint ) {
		return draw( path, paint, DEFAULT_STROKE_WIDTH );
	}

	/**
	 * Add a stroked SVG path entry to the icon using the given stroke width.
	 *
	 * @param path  The SVG path to draw
	 * @param width The stroke width
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon draw( String path, double width ) {
		return draw( path, null, width, DEFAULT_STROKE_CAP, DEFAULT_STROKE_JOIN, 0 );
	}

	/**
	 * Add a stroked SVG path entry to the icon using the given paint and stroke
	 * width.
	 *
	 * @param path  The SVG path to draw
	 * @param paint The paint to stroke with
	 * @param width The stroke width
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon draw( String path, Paint paint, double width ) {
		return draw( path, paint, width, DEFAULT_STROKE_CAP, DEFAULT_STROKE_JOIN, 0 );
	}

	/**
	 * Add a stroked SVG path entry to the icon using the given paint, stroke
	 * width, line cap and line join.
	 *
	 * @param path  The SVG path to draw
	 * @param paint The paint to stroke with
	 * @param width The stroke width
	 * @param cap   The stroke line cap
	 * @param join  The stroke line join
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon draw( String path, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join ) {
		return draw( path, paint, width, cap, join, 0 );
	}

	/**
	 * Add a stroked SVG path entry to the icon using the given paint, stroke
	 * width, line cap, line join, dash offset and dash array.
	 *
	 * @param path       The SVG path to draw
	 * @param paint      The paint to stroke with
	 * @param width      The stroke width
	 * @param cap        The stroke line cap
	 * @param join       The stroke line join
	 * @param dashOffset The stroke dash offset
	 * @param dashes     The stroke dash array
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon draw( String path, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join, double dashOffset, double... dashes ) {
		return draw( path, null, paint, width, cap, join, dashOffset, dashes );
	}

	/**
	 * Add a stroked SVG path entry to the icon using the given font, paint,
	 * stroke width, line cap, line join, dash offset and dash array.
	 *
	 * @param path       The SVG path to draw
	 * @param font       The font to use when the path is text
	 * @param paint      The paint to stroke with
	 * @param width      The stroke width
	 * @param cap        The stroke line cap
	 * @param join       The stroke line join
	 * @param dashOffset The stroke dash offset
	 * @param dashes     The stroke dash array
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon draw( String path, Font font, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join, double dashOffset, double... dashes ) {
		if( path != null ) {
			actions.add( new Draw( path, true, font, paint, width, cap, join, dashOffset, dashes ) );
		}
		return this;
	}

	/**
	 * Add a stroked SVG path entry to the icon.
	 *
	 * @param path The SVG path to fill
	 * @return This {@link SvgIcon}
	 */
	@SuppressWarnings( "unused" )
	public SvgIcon drawText( String path ) {
		return drawText( path, null );
	}

	/**
	 * Add a stroked text entry to the icon using the given paint.
	 *
	 * @param path  The text to draw
	 * @param paint The paint to stroke with
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon drawText( String path, Paint paint ) {
		return drawText( path, paint, DEFAULT_STROKE_WIDTH );
	}

	/**
	 * Add a stroked text entry to the icon using the given stroke width.
	 *
	 * @param path  The text to draw
	 * @param width The stroke width
	 * @return This {@link SvgIcon}
	 */
	@SuppressWarnings( "unused" )
	public SvgIcon drawText( String path, double width ) {
		return drawText( path, null, width, DEFAULT_STROKE_CAP, DEFAULT_STROKE_JOIN, 0 );
	}

	/**
	 * Add a stroked text entry to the icon using the given paint and stroke
	 * width.
	 *
	 * @param path  The text to draw
	 * @param paint The paint to stroke with
	 * @param width The stroke width
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon drawText( String path, Paint paint, double width ) {
		return drawText( path, paint, width, DEFAULT_STROKE_CAP, DEFAULT_STROKE_JOIN, 0 );
	}

	/**
	 * Add a stroked text entry to the icon using the given paint, stroke width,
	 * line cap and line join.
	 *
	 * @param path  The text to draw
	 * @param paint The paint to stroke with
	 * @param width The stroke width
	 * @param cap   The stroke line cap
	 * @param join  The stroke line join
	 * @return This {@link SvgIcon}
	 */
	@SuppressWarnings( "unused" )
	public SvgIcon drawText( String path, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join ) {
		return drawText( path, paint, width, cap, join, 0 );
	}

	/**
	 * Add a stroked text entry to the icon using the given paint, stroke width,
	 * line cap, line join, dash offset and dash array.
	 *
	 * @param path       The text to draw
	 * @param paint      The paint to stroke with
	 * @param width      The stroke width
	 * @param cap        The stroke line cap
	 * @param join       The stroke line join
	 * @param dashOffset The stroke dash offset
	 * @param dashes     The stroke dash array
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon drawText( String path, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join, double dashOffset, double... dashes ) {
		return drawText( path, null, paint, width, cap, join, dashOffset, dashes );
	}

	/**
	 * Add a stroked text entry to the icon using the given font, paint, stroke
	 * width, line cap, line join, dash offset and dash array.
	 *
	 * @param path       The text to draw
	 * @param font       The font to use
	 * @param paint      The paint to stroke with
	 * @param width      The stroke width
	 * @param cap        The stroke line cap
	 * @param join       The stroke line join
	 * @param dashOffset The stroke dash offset
	 * @param dashes     The stroke dash array
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon drawText( String path, Font font, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join, double dashOffset, double... dashes ) {
		if( path != null ) {
			actions.add( new Draw( path, false, font, paint, width, cap, join, dashOffset, dashes ) );
		}
		return this;
	}

	/**
	 * Appends the actions of the given icon to this icon, wrapping them in a
	 * saved/restored graphics context so the embedded icon's drawing is
	 * isolated.
	 *
	 * @param icon the icon whose actions are appended, or {@code null}
	 * @return this {@link SvgIcon}
	 */
	public SvgIcon draw( SvgIcon icon ) {
		if( icon != null ) {
			icon.doRender();
			actions.addAll( icon.actions );
			actions.add( new Restore() );
		}
		return this;
	}

	/**
	 * Create an SVG string that represents the given point rotated about cx,cy
	 * by the angle.
	 *
	 * @param x The X coordinate of the point
	 * @param y The Y coordinate of the point
	 * @param cx The X coordinate of the rotation center
	 * @param cy The Y coordinate of the rotation center
	 * @param angle The angle in degrees to rotate counter-clockwise
	 * @return The SVG string of the rotated point
	 */
	protected String rotate( double x, double y, double cx, double cy, double angle ) {
		return rx( cx, cy, x, y, angle ) + "," + ry( cx, cy, x, y, angle );
	}

	/**
	 * Computes the X coordinate of the point {@code (x, y)} rotated about
	 * {@code (cx, cy)} by the given angle, in degrees, counter-clockwise.
	 *
	 * @param cx the X coordinate of the rotation center
	 * @param cy the Y coordinate of the rotation center
	 * @param x  the X coordinate of the point
	 * @param y  the Y coordinate of the point
	 * @param angle the angle in degrees to rotate counter-clockwise
	 * @return the rotated X coordinate
	 */
	private double rx( double cx, double cy, double x, double y, double angle ) {
		return cx + ((x - cx) * Math.cos( Math.toRadians( angle ) )) + ((y - cy) * Math.sin( Math.toRadians( angle ) ));
	}

	/**
	 * Computes the Y coordinate of the point {@code (x, y)} rotated about
	 * {@code (cx, cy)} by the given angle, in degrees, counter-clockwise.
	 *
	 * @param cx the X coordinate of the rotation center
	 * @param cy the Y coordinate of the rotation center
	 * @param x  the X coordinate of the point
	 * @param y  the Y coordinate of the point
	 * @param angle the angle in degrees to rotate counter-clockwise
	 * @return the rotated Y coordinate
	 */
	private double ry( double cx, double cy, double x, double y, double angle ) {
		return cy + ((y - cy) * Math.cos( Math.toRadians( angle ) )) - ((x - cx) * Math.sin( Math.toRadians( angle ) ));
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * In addition to the base copy, the icon's sequence of
	 * {@link IconAction actions} is duplicated so the copy is independent of
	 * this icon.
	 */
	@Override
	public <T extends VectorImage> T copy() {
		T copy = super.copy();
		((SvgIcon)copy).actions.addAll( this.actions );
		return copy;
	}

	/**
	 * This method should not be overridden. Drawing definition should be done
	 * in the constructor.
	 */
	@Override
	protected final void doRender() {
		define();
		super.doRender();
		getGraphicsContext2D().save();
		actions.forEach( a -> a.render( this ) );
	}

	/**
	 * Serializes this icon to an SVG document string, emitting a {@code <path>}
	 * element for each {@link Fill} and {@link Draw} action.
	 *
	 * @return the SVG document string
	 */
	public String toSvg() {
		StringBuilder svg = new StringBuilder();
		svg.append( "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" ).append( getGridX() ).append( "\" height=\"" ).append( getGridY() ).append( "\">" );
		for( IconAction action : actions ) {
			// TODO Handle transforms
			//getTransforms();

			if( action instanceof Fill fill ) {
				svg.append( "<path fill=\"" ).append( Paints.toString( fill.calcPaint( this ) ) ).append( "\" d=\"" ).append( fill.getPath() ).append( "\"/>" );
			} else if( action instanceof Draw draw ) {
				svg
					.append( "<path stroke=\"" )
					.append( Paints.toString( draw.calcPaint( this ) ) )
					.append( "\" stroke-width=\"" )
					.append( draw.width )
					.append( "\" d=\"" )
					.append( draw.getPath() )
					.append( "\"/>" );
			}
		}
		svg.append( "</svg>" );

		return svg.toString();
	}

	/**
	 * Restores the graphics context to the state saved by a preceding
	 * {@link #restore()} call.
	 */
	private void doRestore() {
		getGraphicsContext2D().restore();
	}

	/**
	 * An action that is applied to a {@link SvgIcon} during rendering.
	 */
	private interface IconAction {

		/**
		 * Renders the action against the supplied icon.
		 *
		 * @param icon the icon being rendered
		 */
		void render( SvgIcon icon );

	}

	/**
	 * The abstract base for render actions that operate on a path, providing
	 * shared path storage, paint resolution, and graphics-context setup and
	 * teardown.
	 */
	private static abstract class RenderAction implements IconAction {

		/**
		 * The path data for this action, which is either an SVG path string or
		 * a literal text string depending on {@link #isSvg()}.
		 */
		private final String path;

		/**
		 * Whether {@link #path} is an SVG path ({@code true}) or a literal
		 * text string ({@code false}).
		 */
		private final boolean isSvg;

		/**
		 * The paint to use, or the {@link #PRIMARY} or {@link #SECONDARY}
		 * sentinel that is expanded to the icon's primary or secondary paint.
		 */
		private final Paint paint;

		/**
		 * Constructs a render action.
		 *
		 * @param path the SVG path data or literal text
		 * @param isSvg whether {@code path} is an SVG path ({@code true}) or
		 *              literal text ({@code false})
		 * @param paint the paint to use, or a {@link #PRIMARY} or
		 *              {@link #SECONDARY} sentinel, or {@code null} to use the
		 *              icon's stroke paint
		 */
		protected RenderAction( String path, boolean isSvg, Paint paint ) {
			this.path = path;
			this.isSvg = isSvg;
			this.paint = paint;
		}

		/**
		 * Returns the path data for this action, which is either an SVG path
		 * string or a literal text string depending on {@link #isSvg()}.
		 *
		 * @return the path data
		 */
		protected String getPath() {
			return path;
		}

		/**
		 * Indicates whether {@link #getPath()} holds an SVG path string
		 * ({@code true}) or a literal text string ({@code false}).
		 *
		 * @return {@code true} if the path is an SVG path, {@code false} if it
		 * is literal text
		 */
		protected boolean isSvg() {
			return isSvg;
		}

		/**
		 * Saves the current graphics context state before a render action is
		 * applied.
		 *
		 * @param icon the icon being rendered
		 * @return the graphics context whose state was saved
		 */
		protected GraphicsContext setup( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();
			context.save();
			return context;
		}

		/**
		 * Restores the graphics context state after a render action is
		 * complete, undoing the save performed by {@link #setup(SvgIcon)}.
		 *
		 * @param icon the icon being rendered
		 * @return the graphics context whose state was restored
		 */
		@SuppressWarnings( "UnusedReturnValue" )
		protected GraphicsContext teardown( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();
			context.restore();
			return context;
		}

		/**
		 * Resolves the effective paint for this action, expanding the
		 * {@link #PRIMARY} and {@link #SECONDARY} sentinels to the icon's
		 * primary and secondary paints.
		 *
		 * @param icon the icon whose primary and secondary paints are used when
		 *            {@link #paint} is a sentinel
		 * @return the resolved paint, or the icon's stroke paint when
		 *         {@link #paint} is {@code null}
		 */
		protected Paint calcPaint( SvgIcon icon ) {
			if( paint == null ) {
				return icon.getStrokePaint();
			} else if( paint == PRIMARY ) {
				return icon.getPrimaryPaint();
			} else if( paint == SECONDARY ) {
				return icon.getSecondaryPaint();
			} else {
				return paint;
			}
		}

	}

	/**
	 * A render action that fills an SVG path (or fills text when not SVG)
	 * using a font, paint, and fill rule.
	 */
	private static class Fill extends RenderAction {

		/**
		 * The font to use when rendering text.
		 */
		private final Font font;

		/**
		 * The fill rule that determines whether the path interior is filled
		 * using the nonzero or even-odd rule.
		 */
		private final FillRule rule;

		/**
		 * Constructs a fill action.
		 *
		 * @param path the SVG path data or literal text to fill
		 * @param isSvg whether {@code path} is an SVG path ({@code true}) or
		 *              literal text ({@code false})
		 * @param font the font to use when rendering text
		 * @param paint the paint to fill with, or a {@link #PRIMARY} or
		 *              {@link #SECONDARY} sentinel
		 * @param rule the fill rule to apply
		 */
		public Fill( String path, boolean isSvg, Font font, Paint paint, FillRule rule ) {
			super( path, isSvg, paint );
			this.font = font;
			this.rule = rule;
		}

		public void render( SvgIcon icon ) {
			GraphicsContext context = setup( icon );
			context.setFont( font );
			context.setFill( calcPaint( icon ) );
			context.setFillRule( rule );

			if( isSvg() ) {
				context.beginPath();
				context.appendSVGPath( getPath() );
				context.fill();
			} else {
				context.fillText( getPath(), 0, 0 );
				context.restore();
			}

			teardown( icon );
		}

	}

	/**
	 * A render action that strokes an SVG path (or strokes text when not SVG)
	 * using a font, paint, and stroke style.
	 */
	private static class Draw extends RenderAction {

		/**
		 * The font to use when rendering text.
		 */
		private final Font font;

		/**
		 * The stroke line width.
		 */
		private final double width;

		/**
		 * The stroke line cap style.
		 */
		private final StrokeLineCap cap;

		/**
		 * The stroke line join style.
		 */
		private final StrokeLineJoin join;

		/**
		 * The offset into the line dash pattern.
		 */
		private final double dashOffset;

		/**
		 * The line dash pattern lengths.
		 */
		private final double[] dashes;

		/**
		 * Constructs a draw action.
		 *
		 * @param path the SVG path data or literal text to stroke
		 * @param isSvg whether {@code path} is an SVG path ({@code true}) or
		 *              literal text ({@code false})
		 * @param font the font to use when rendering text
		 * @param paint the paint to stroke with, or a {@link #PRIMARY} or
		 *              {@link #SECONDARY} sentinel
		 * @param width the stroke line width
		 * @param cap the stroke line cap style
		 * @param join the stroke line join style
		 * @param dashOffset the offset into the line dash pattern
		 * @param dashes the line dash pattern lengths
		 */
		public Draw( String path, boolean isSvg, Font font, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join, double dashOffset, double... dashes ) {
			super( path, isSvg, paint );
			this.font = font;
			this.width = width;
			this.cap = cap;
			this.join = join;
			this.dashOffset = dashOffset;
			this.dashes = dashes;
		}

		public void render( SvgIcon icon ) {
			GraphicsContext context = setup( icon );
			context.setFont( font );
			context.setStroke( calcPaint( icon ) );
			context.setLineWidth( width );
			context.setLineCap( cap );
			context.setLineJoin( join );
			context.setLineDashOffset( dashOffset );
			context.setLineDashes( dashes );

			if( isSvg() ) {
				context.beginPath();
				context.appendSVGPath( getPath() );
				context.stroke();
			} else {
				context.strokeText( getPath(), 0, 0 );
				context.restore();
			}

			teardown( icon );
		}

	}

	/**
	 * A render action that clips the graphics context to an SVG path.
	 */
	private static class Clip extends RenderAction {

		/**
		 * Constructs a clip action.
		 *
		 * @param path the SVG path data to clip to
		 * @param isSvg whether {@code path} is an SVG path ({@code true}) or
		 *              literal text ({@code false})
		 */
		public Clip( String path, boolean isSvg ) {
			super( path, isSvg, null );
		}

		@Override
		public void render( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();

			// It is important to store the context state but not restore it
			context.save();

			context.beginPath();
			context.appendSVGPath( getPath() );
			context.clip();
		}

	}

	/**
	 * A render action that applies a transform to the graphics context.
	 */
	private static class Xform implements IconAction {

		/**
		 * The transform to apply to the graphics context.
		 */
		private final Transform transform;

		/**
		 * Constructs an xform action.
		 *
		 * @param transform the transform to apply
		 */
		public Xform( Transform transform ) {
			this.transform = transform;
		}

		@Override
		public void render( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();

			// It is important to store the context state but not restore it
			context.save();

			context.transform( new Affine( transform ) );
		}

	}

	/**
	 * A render action that restores the graphics context to the state saved by
	 * a preceding {@link #restore()} call.
	 */
	private static class Restore implements IconAction {

		@Override
		public void render( SvgIcon icon ) {
			icon.doRestore();
		}

	}

}
