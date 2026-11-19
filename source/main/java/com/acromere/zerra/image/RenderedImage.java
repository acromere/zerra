package com.acromere.zerra.image;

import com.acromere.zerra.font.FontUtil;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.*;

import java.util.List;

/**
 * Abstract base for image-backed {@link VectorImage} icons.
 * <p>
 * A {@code RenderedImage} owns the {@link GraphicsContext} used to paint its content
 * and delegates the actual drawing to the {@link #render()}
 * template method, which subclasses override to produce their output. It exposes
 * the protected drawing context plus a set of convenience helpers
 * (e.g. {@link #move(double, double)}, {@link #zoom(double, double)},
 * {@link #spin(double, double, double)} and {@link #addRect(double, double, double, double)})
 * that operate on that context, and the {@link #drawImage(Image, double, double, double, double)}
 * overloads that paint a {@link Image} onto the context.
 * <p>
 * Concrete subclasses include {@link BrokenIcon}, {@link ImageIcon}, {@link SvgIcon} and
 * {@link VectorIcon}.
 *
 * @see VectorImage
 * @see RenderedIcon
 * @see BrokenIcon
 * @see ImageIcon
 * @see SvgIcon
 * @see VectorIcon
 */
public abstract class RenderedImage extends VectorImage {

	protected RenderedImage() {}

	protected RenderedImage( double gridX, double gridY ) {
		super( gridX, gridY );
	}

	/**
	 * Renders this image's content onto its graphics context.
	 *
	 * <p>Subclasses override this method to produce their output.</p>
	 */
	protected abstract void render();

	/**
	 * Translates the graphics context by the given offset.
	 *
	 * @param x the x translation
	 * @param y the y translation
	 */
	protected void move( double x, double y ) {
		getGraphicsContext2D().translate( x, y );
	}

	/**
	 * Scales the graphics context about the origin by the given factors.
	 *
	 * @param x the x scale factor
	 * @param y the y scale factor
	 */
	protected void zoom( double x, double y ) {
		getGraphicsContext2D().scale( x, y );
	}

	/**
	 * Rotates the graphics context by the given angle, in radians, about the
	 * given point.
	 *
	 * @param x the x coordinate of the rotation center
	 * @param y the y coordinate of the rotation center
	 * @param a the angle, in radians
	 */
	protected void spin( double x, double y, double a ) {
		move( x, y );
		getGraphicsContext2D().rotate( a );
		move( -x, -y );
	}

	/**
	 * Clips the graphics context to the current path.
	 */
	protected void clip() {
		getGraphicsContext2D().clip();
	}

	/**
	 * Saves the current state of the graphics context.
	 */
	protected void save() {
		getGraphicsContext2D().save();
	}

	/**
	 * Restores the previously saved state of the graphics context.
	 */
	protected void restore() {
		getGraphicsContext2D().restore();
	}

	/**
	 * Begins a new path on the graphics context.
	 */
	protected void startPath() {
		getGraphicsContext2D().beginPath();
	}

	/**
	 * Begins a new path on the graphics context at the given point.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	protected void startPath( double x, double y ) {
		getGraphicsContext2D().beginPath();
		getGraphicsContext2D().moveTo( x, y );
	}

	/**
	 * Moves the current path to the given point.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	protected void moveTo( double x, double y ) {
		getGraphicsContext2D().moveTo( x, y );
	}

	/**
	 * Adds a line from the current point to the given point.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	protected void lineTo( double x, double y ) {
		getGraphicsContext2D().lineTo( x, y );
	}

	/**
	 * Adds an arc to the current path.
	 *
	 * @param cx     the x coordinate of the arc center
	 * @param cy     the y coordinate of the arc center
	 * @param rx     the x radius
	 * @param ry     the y radius
	 * @param start  the start angle, in degrees
	 * @param extent the arc extent, in degrees
	 */
	protected void addArc( double cx, double cy, double rx, double ry, double start, double extent ) {
		getGraphicsContext2D().arc( cx, cy, rx, ry, start, extent );
	}

	/**
	 * Adds a full oval to the current path, centered at the given point.
	 *
	 * @param cx the x coordinate of the oval center
	 * @param cy the y coordinate of the oval center
	 * @param rx the x radius
	 * @param ry the y radius
	 */
	protected void addOval( double cx, double cy, double rx, double ry ) {
		getGraphicsContext2D().moveTo( cx + rx, cy );
		getGraphicsContext2D().arc( cx, cy, rx, ry, 0, 360 );
	}

	/**
	 * Adds a single-pixel dot to the current path at the given point.
	 *
	 * @param cx the x coordinate of the dot
	 * @param cy the y coordinate of the dot
	 */
	protected void addDot( double cx, double cy ) {
		addOval( cx, cy, g( 1 ), g( 1 ) );
	}

	/**
	 * Adds a line from the first point to the second point.
	 *
	 * @param x1 the x coordinate of the first point
	 * @param y1 the y coordinate of the first point
	 * @param x2 the x coordinate of the second point
	 * @param y2 the y coordinate of the second point
	 */
	protected void addLine( double x1, double y1, double x2, double y2 ) {
		moveTo( x1, y1 );
		lineTo( x2, y2 );
	}

	/**
	 * Adds a rectangle to the current path.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param w the width
	 * @param h the height
	 */
	protected void addRect( double x, double y, double w, double h ) {
		getGraphicsContext2D().rect( x, y, w, h );
	}

	/**
	 * Adds a quadratic curve to the current path.
	 *
	 * @param xc the x coordinate of the control point
	 * @param yc the y coordinate of the control point
	 * @param x1 the x coordinate of the end point
	 * @param y1 the y coordinate of the end point
	 */
	@SuppressWarnings( "unused" )
	protected void curveTo( double xc, double yc, double x1, double y1 ) {
		getGraphicsContext2D().quadraticCurveTo( xc, yc, x1, y1 );
	}

	/**
	 * Adds a cubic Bezier curve to the current path.
	 *
	 * @param xc1 the x coordinate of the first control point
	 * @param yc1 the y coordinate of the first control point
	 * @param xc2 the x coordinate of the second control point
	 * @param yc2 the y coordinate of the second control point
	 * @param x1  the x coordinate of the end point
	 * @param y1  the y coordinate of the end point
	 */
	protected void curveTo( double xc1, double yc1, double xc2, double yc2, double x1, double y1 ) {
		getGraphicsContext2D().bezierCurveTo( xc1, yc1, xc2, yc2, x1, y1 );
	}

	/**
	 * Closes the current path.
	 */
	protected void closePath() {
		getGraphicsContext2D().closePath();
	}

	/**
	 * Fills the given text at the given position and size, with no maximum
	 * width.
	 *
	 * @param text     the text to fill
	 * @param x        the x position
	 * @param y        the y position
	 * @param textSize the text size
	 */
	@SuppressWarnings( "unused" )
	protected void fillText( String text, double x, double y, double textSize ) {
		fillText( text, x, y, textSize, -1 );
	}

	/**
	 * Fills the given text at the given position and size, constrained to the
	 * given maximum width.
	 *
	 * @param text     the text to fill
	 * @param x        the x position
	 * @param y        the y position
	 * @param textSize the text size
	 * @param maxWidth the maximum width, or a negative value for no limit
	 */
	protected void fillText( String text, double x, double y, double textSize, double maxWidth ) {
		renderText( text, x, y, textSize, maxWidth, false );
	}

	/**
	 * Strokes the given text at the given position and size, with no maximum
	 * width.
	 *
	 * @param text     the text to stroke
	 * @param x        the x position
	 * @param y        the y position
	 * @param textSize the text size
	 */
	@SuppressWarnings( "unused" )
	protected void drawText( String text, double x, double y, double textSize ) {
		drawText( text, x, y, textSize, -1 );
	}

	/**
	 * Strokes the given text at the given position and size, constrained to the
	 * given maximum width.
	 *
	 * @param text     the text to stroke
	 * @param x        the x position
	 * @param y        the y position
	 * @param textSize the text size
	 * @param maxWidth the maximum width, or a negative value for no limit
	 */
	@SuppressWarnings( "SameParameterValue" )
	protected void drawText( String text, double x, double y, double textSize, double maxWidth ) {
		renderText( text, x, y, textSize, maxWidth, true );
	}

	/**
	 * Creates a linear paint from the given stops.
	 *
	 * @param x1    the x coordinate of the start
	 * @param y1    the y coordinate of the start
	 * @param x2    the x coordinate of the end
	 * @param y2    the y coordinate of the end
	 * @param stops the gradient stops
	 * @return a {@link javafx.scene.paint.LinearGradient} built from the stops
	 */
	@SuppressWarnings( "unused" )
	protected Paint linearPaint( double x1, double y1, double x2, double y2, Stop... stops ) {
		return new LinearGradient( x1, y1, x2, y2, false, CycleMethod.NO_CYCLE, stops );
	}

	/**
	 * Creates a linear paint from the given stops.
	 *
	 * @param x1    the x coordinate of the start
	 * @param y1    the y coordinate of the start
	 * @param x2    the x coordinate of the end
	 * @param y2    the y coordinate of the end
	 * @param stops the gradient stops
	 * @return a {@link javafx.scene.paint.LinearGradient} built from the stops
	 */
	@SuppressWarnings( "unused" )
	protected Paint linearPaint( double x1, double y1, double x2, double y2, List<Stop> stops ) {
		return new LinearGradient( x1, y1, x2, y2, false, CycleMethod.NO_CYCLE, stops );
	}

	/**
	 * Creates a radial paint from the given stops.
	 *
	 * @param x     the x coordinate of the focus
	 * @param y     the y coordinate of the focus
	 * @param r     the radius
	 * @param stops the gradient stops
	 * @return a {@link javafx.scene.paint.RadialGradient} built from the stops
	 */
	@SuppressWarnings( "unused" )
	protected Paint radialPaint( double x, double y, double r, Stop... stops ) {
		return new RadialGradient( 0, 0, x, y, r, false, CycleMethod.NO_CYCLE, stops );
	}

	/**
	 * Creates a radial paint from the given stops.
	 *
	 * @param x     the x coordinate of the focus
	 * @param y     the y coordinate of the focus
	 * @param r     the radius
	 * @param stops the gradient stops
	 * @return a {@link javafx.scene.paint.RadialGradient} built from the stops
	 */
	protected Paint radialPaint( double x, double y, double r, List<Stop> stops ) {
		return new RadialGradient( 0, 0, x, y, r, false, CycleMethod.NO_CYCLE, stops );
	}

	/**
	 * Derives a variant of the given font, or the current font, at the given
	 * size, preserving its weight and posture.
	 *
	 * @param font the font to derive from, or null for the current font
	 * @param size the size of the derived font
	 * @return a font with the same family, weight and posture at the given size
	 */
	protected Font deriveFont( Font font, double size ) {
		if( font == null ) font = getGraphicsContext2D().getFont();
		FontWeight fontWeight = FontUtil.getFontWeight( font.getStyle() );
		FontPosture fontPosture = FontUtil.getFontPosture( font.getStyle() );
		return Font.font( font.getFamily(), fontWeight, fontPosture, size );
	}

	/**
	 * Sets the stroke line cap on the graphics context.
	 *
	 * @param cap the stroke line cap
	 */
	protected void setDrawCap( StrokeLineCap cap ) {
		getGraphicsContext2D().setLineCap( cap );
	}

	/**
	 * Sets the stroke line join on the graphics context.
	 *
	 * @param join the stroke line join
	 */
	@SuppressWarnings( "unused" )
	protected void setDrawJoin( StrokeLineJoin join ) {
		getGraphicsContext2D().setLineJoin( join );
	}

	/**
	 * Sets the stroke width on the graphics context.
	 *
	 * @param width the stroke width
	 */
	protected void setDrawWidth( double width ) {
		getGraphicsContext2D().setLineWidth( width );
	}

	/**
	 * Sets the stroke paint on the graphics context.
	 *
	 * @param paint the stroke paint
	 */
	protected void setDrawPaint( Paint paint ) {
		getGraphicsContext2D().setStroke( paint );
	}

	/**
	 * Sets the fill paint on the graphics context.
	 *
	 * @param paint the fill paint
	 */
	protected void setFillPaint( Paint paint ) {
		getGraphicsContext2D().setFill( paint );
	}

	/**
	 * Sets the text alignment on the graphics context.
	 *
	 * @param alignment the text alignment
	 */
	@SuppressWarnings( "SameParameterValue" )
	protected void setTextAlign( TextAlignment alignment ) {
		getGraphicsContext2D().setTextAlign( alignment );
	}

	/**
	 * Sets the text baseline on the graphics context.
	 *
	 * @param baseline the text baseline
	 */
	@SuppressWarnings( "unused" )
	protected void setTextBaseLine( VPos baseline ) {
		getGraphicsContext2D().setTextBaseline( baseline );
	}

	/**
	 * Draws the given image at the origin, sized to the grid.
	 *
	 * @param image the image to draw
	 */
	protected void drawImage( Image image ) {
		drawImage( image, 0, 0 );
	}

	/**
	 * Draw an image.
	 * <p>
	 * Note that x, y are relative to the grid. The width and height are set to
	 * the grid width and height.
	 * </p>
	 *
	 * @param image The image to render
	 * @param x The position x
	 * @param y The position x
	 */
	@SuppressWarnings( "SameParameterValue" )
	protected void drawImage( Image image, double x, double y ) {
		drawImage( image, x, y, getGridX(), getGridY() );
	}

	/**
	 * Draw an image.
	 * <p>
	 * Note that x, y, w and h are relative to the grid.
	 * </p>
	 *
	 * @param image The image to render
	 * @param x The position x
	 * @param y The position x
	 * @param w The width
	 * @param h The height
	 */
	protected void drawImage( Image image, double x, double y, double w, double h ) {
		getGraphicsContext2D().drawImage( image, x, y, w, h );
	}

	/**
	 * Clears the given rectangle on the graphics context.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param w the width
	 * @param h the height
	 */
	@SuppressWarnings( "unused" )
	protected void clearRect( double x, double y, double w, double h ) {
		getGraphicsContext2D().clearRect( x, y, w, h );
	}

	/**
	 * Strokes the current path.
	 */
	protected void draw() {
		getGraphicsContext2D().stroke();
	}

	/**
	 * Strokes the current path using the given paint.
	 *
	 * @param paint the stroke paint
	 */
	protected void draw( Paint paint ) {
		getGraphicsContext2D().save();
		getGraphicsContext2D().setStroke( paint );
		getGraphicsContext2D().stroke();
		getGraphicsContext2D().restore();
	}

	/**
	 * Fills the current path.
	 */
	protected void fill() {
		getGraphicsContext2D().fill();
	}

	/**
	 * Fills the current path using the given paint.
	 *
	 * @param paint the fill paint
	 */
	protected void fill( Paint paint ) {
		getGraphicsContext2D().save();
		getGraphicsContext2D().setFill( paint );
		getGraphicsContext2D().fill();
		getGraphicsContext2D().restore();
	}

	/**
	 * Renders the given image onto this image's graphics context, inheriting
	 * this image's stroke, primary and secondary paints.
	 *
	 * @param image the image to render
	 */
	protected void render( VectorImage image ) {
		image.setStrokePaint( getStrokePaint() );
		image.setPrimaryPaint( getPrimaryPaint() );
		image.setSecondaryPaint( getSecondaryPaint() );
		image.setGraphicsContext2D( getGraphicsContext2D() );
		image.doRender();
	}

	/**
	 * Applies the base rendering defaults and then invokes {@link #render()}.
	 */
	protected void doRender() {
		super.doRender();
		render();
	}

	/**
	 * Returns the value scaled by the grid; a no-op in this base
	 * implementation.
	 *
	 * @param value the value to scale
	 * @return the scaled value
	 */
	protected double g( double value ) {
		return value;
	}

	/**
	 * Strokes or fills the given text at the given position and size, wrapping
	 * the drawing in a scale transform to accommodate small font sizes.
	 *
	 * @param text     the text to render
	 * @param x        the x position, in grid coordinates
	 * @param y        the y position, in grid coordinates
	 * @param textSize the text size in points
	 * @param maxWidth the maximum width, or a negative value for no limit
	 * @param draw     whether to stroke the text when true, or fill it when false
	 */
	private void renderText( String text, double x, double y, double textSize, double maxWidth, boolean draw ) {
		// Font sizes smaller than one don't scale as expected,
		// so the workaround is to scale according to the text
		// size and divide the coordinates by the text size.

		double fontSize = 72;
		double scale = textSize / fontSize;

		// Scale the transform
		getGraphicsContext2D().save();
		getGraphicsContext2D().scale( scale, scale );

		getGraphicsContext2D().setFont( deriveFont( getFont(), fontSize ) );
		getGraphicsContext2D().setFontSmoothingType( FontSmoothingType.GRAY );

		// Stroke or fill the text
		if( draw ) {
			if( maxWidth < 0 ) {
				getGraphicsContext2D().strokeText( text, x / scale, y / scale );
			} else {
				getGraphicsContext2D().strokeText( text, x / scale, y / scale, maxWidth / scale );
			}
		} else {
			if( maxWidth < 0 ) {
				getGraphicsContext2D().fillText( text, x / scale, y / scale );
			} else {
				getGraphicsContext2D().fillText( text, x / scale, y / scale, maxWidth / scale );
			}
		}

		getGraphicsContext2D().restore();
	}

}
