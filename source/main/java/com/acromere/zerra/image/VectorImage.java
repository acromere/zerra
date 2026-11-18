package com.acromere.zerra.image;

import com.acromere.zerra.theme.Theme;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Abstract base class for vector graphics that render themselves onto a
 * {@link Canvas} using a fixed coordinate grid.
 *
 * <p>
 * A {@code VectorImage} is defined on a {@code gridX} by {@code gridY} grid of
 * design coordinates and is scaled to fit its actual width and height.
 * Appearance is driven by four styleable properties &mdash; stroke width, stroke
 * paint, primary paint and secondary paint &mdash; together with a font, all of
 * which can be bound or overridden. Each image is also associated with a
 * {@link Theme} so that it can adopt the current color scheme.
 *
 * <p>
 * Subclasses implement {@link #doRender()} to draw their content.
 */
@CustomLog
public abstract class VectorImage extends Canvas {

	public static final double DEFAULT_SIZE = 256;

	public static final double DEFAULT_GRID = 32;

	public static final double DEFAULT_STROKE_WIDTH = 4.0;

	public static final StrokeLineCap DEFAULT_STROKE_CAP = StrokeLineCap.ROUND;

	public static final StrokeLineJoin DEFAULT_STROKE_JOIN = StrokeLineJoin.ROUND;

	private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	private static final CssMetaData<VectorImage, Number> CSS_STROKE_WIDTH;

	private static final CssMetaData<VectorImage, Paint> CSS_STROKE_PAINT;

	private static final CssMetaData<VectorImage, Paint> CSS_PRIMARY_PAINT;

	private static final CssMetaData<VectorImage, Paint> CSS_SECONDARY_PAINT;

	private static final CssMetaData<VectorImage, Font> CSS_FONT;

	// This is set to a bright color to reveal when the style is not working correctly
	private static final Paint DEFAULT_STROKE_PAINT = Color.MAGENTA;

	// This is set to a bright color to reveal when the style is not working correctly
	private static final Paint DEFAULT_PRIMARY_PAINT = Color.RED;

	// This is set to a bright color to reveal when the style is not working correctly
	private static final Paint DEFAULT_SECONDARY_PAINT = Color.YELLOW;

	private static final Font DEFAULT_FONT = Font.getDefault();

	static {
		// Remember to update the test style sheets
		CSS_STROKE_WIDTH = new CssMetaData<>( "-fx-stroke-width", StyleConverter.getSizeConverter() ) {

			@Override
			public boolean isSettable( VectorImage styleable ) {
				return styleable.strokePaint == null || !styleable.strokePaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Number> getStyleableProperty( VectorImage styleable ) {
				return (StyleableProperty<Number>)styleable.strokeWidthProperty();
			}

		};

		// Remember to update the test style sheets
		CSS_STROKE_PAINT = new CssMetaData<>( "-fx-stroke", StyleConverter.getPaintConverter() ) {

			@Override
			public boolean isSettable( VectorImage styleable ) {
				return styleable.strokePaint == null || !styleable.strokePaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Paint> getStyleableProperty( VectorImage styleable ) {
				return (StyleableProperty<Paint>)styleable.strokePaintProperty();
			}

		};

		// Remember to update the test style sheets
		CSS_PRIMARY_PAINT = new CssMetaData<>( "-fx-primary", StyleConverter.getPaintConverter() ) {

			@Override
			public boolean isSettable( VectorImage styleable ) {
				return styleable.primaryPaint == null || !styleable.primaryPaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Paint> getStyleableProperty( VectorImage styleable ) {
				return (StyleableProperty<Paint>)styleable.primaryPaintProperty();
			}

		};

		// Remember to update the test style sheets
		CSS_SECONDARY_PAINT = new CssMetaData<>( "-fx-secondary", StyleConverter.getPaintConverter() ) {

			@Override
			public boolean isSettable( VectorImage styleable ) {
				return styleable.secondaryPaint == null || !styleable.secondaryPaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Paint> getStyleableProperty( VectorImage styleable ) {
				return (StyleableProperty<Paint>)styleable.secondaryPaintProperty();
			}

		};

		// Remember to update the test style sheets
		CSS_FONT = new CssMetaData<>( "-fx-font", StyleConverter.getFontConverter() ) {

			@Override
			public boolean isSettable( VectorImage styleable ) {
				return styleable.font == null || !styleable.font.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Font> getStyleableProperty( VectorImage styleable ) {
				return (StyleableProperty<Font>)styleable.fontProperty();
			}

		};

		STYLEABLES = List.of( CSS_STROKE_WIDTH, CSS_STROKE_PAINT, CSS_PRIMARY_PAINT, CSS_SECONDARY_PAINT, CSS_FONT );
	}

	private GraphicsContext graphicsContextOverride;

	private Transform baseTransform = new Affine();

	private DoubleProperty strokeWidth;

	private Double strokeWidthOverride;

	private ObjectProperty<Paint> strokePaint;

	private Paint strokePaintOverride;

	private ObjectProperty<Paint> primaryPaint;

	private Paint primaryPaintOverride;

	private ObjectProperty<Paint> secondaryPaint;

	private Paint secondaryPaintOverride;

	private ObjectProperty<Font> font;

	private Font fontOverride;

	@Setter
	@Getter
	private double gridX;

	@Getter
	@Setter
	private double gridY;

	@Setter
	@Getter
	private Theme theme;

	protected VectorImage() {
		this( DEFAULT_GRID, DEFAULT_GRID );
	}

	protected VectorImage( double gridX, double gridY ) {
		this.gridX = gridX;
		this.gridY = gridY;
		resize( DEFAULT_SIZE );
		setTheme( Theme.DARK );
		getStyleClass().add( "xe-image" );
		if( this instanceof IconTag ) IconTag.asIcon( this );
	}

	/**
	 * Returns the CSS metadata that describes the styleable properties supported by
	 * this class.
	 *
	 * @return an unmodifiable list of the {@link CssMetaData} instances.
	 */
	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return STYLEABLES;
	}

	/**
	 * Renders the given image to a proof image on the default colors, delegating
	 * to {@link Proof#proof(VectorImage)}.
	 *
	 * @param image the image to prove; must not be {@code null}.
	 */
	public static void proof( VectorImage image ) {
		Proof.proof( image );
	}

	/**
	 * Applies the rendering defaults to the {@link GraphicsContext} and, on the
	 * first call, establishes the scale transform between the design grid and the
	 * canvas size and clears the canvas. Subclasses override this method to draw
	 * their content after calling {@link #doRender()}.
	 */
	protected void doRender() {
		// Set the defaults
		getGraphicsContext2D().setLineCap( StrokeLineCap.ROUND );
		getGraphicsContext2D().setLineJoin( StrokeLineJoin.ROUND );
		getGraphicsContext2D().setFillRule( FillRule.EVEN_ODD );
		getGraphicsContext2D().setLineWidth( getStrokeWidth() );
		getGraphicsContext2D().setStroke( getStrokePaint() );
		getGraphicsContext2D().setFill( getStrokePaint() );

		// Start rendering by clearing the icon area
		if( graphicsContextOverride == null ) {
			double scaleX = getWidth() / getGridX();
			double scaleY = getHeight() / getGridY();
			baseTransform = Transform.scale( scaleX, scaleY );
			getGraphicsContext2D().clearRect( 0, 0, getGridX(), getGridY() );
			reset();
		}
	}

	/**
	 * Reset the transform back to the initial rendering transform.
	 */
	protected void reset() {
		getGraphicsContext2D().setTransform( new Affine( baseTransform ) );
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return getClassCssMetaData();
	}

	/**
	 * Returns the current stroke width, or {@code null}-derived override value if
	 * one has been set.
	 *
	 * @return the effective stroke width.
	 */
	public double getStrokeWidth() {
		return strokeWidthOverride != null ? strokeWidthOverride : strokeWidthProperty().get();
	}

	/**
	 * Sets an override stroke width that takes precedence over the styleable
	 * property. A value of {@code Double.NaN} clears the override.
	 *
	 * @param width the stroke width, or {@code Double.NaN} to clear the override.
	 */
	public void setStrokeWidth( double width ) {
		strokeWidthOverride = Double.isNaN( width ) ? null : width;
	}

	/**
	 * Returns the lazily-created, styleable property that backs the stroke width.
	 *
	 * @return the stroke width property.
	 */
	public DoubleProperty strokeWidthProperty() {
		if( strokeWidth == null ) {
			strokeWidth = new SimpleStyleableDoubleProperty( CSS_STROKE_WIDTH, VectorImage.this, "outlineWidth", DEFAULT_STROKE_WIDTH ) {

				@Override
				protected void invalidated() {
					doRender();
				}

			};
		}
		return strokeWidth;
	}

	/**
	 * Returns the current stroke paint, taking any override into account.
	 *
	 * @return the effective stroke paint.
	 */
	public Paint getStrokePaint() {
		return strokePaintOverride != null ? strokePaintOverride : strokePaintProperty().get();
	}

	/**
	 * Sets an override stroke paint that takes precedence over the styleable
	 * property.
	 *
	 * @param paint the stroke paint to override with.
	 */
	public void setStrokePaint( Paint paint ) {
		strokePaintOverride = paint;
	}

	/**
	 * Returns the lazily-created, styleable property that backs the stroke paint.
	 *
	 * @return the stroke paint property.
	 */
	public ObjectProperty<Paint> strokePaintProperty() {
		if( strokePaint == null ) {
			strokePaint = new SimpleStyleableObjectProperty<>( CSS_STROKE_PAINT, VectorImage.this, "strokePaint", DEFAULT_STROKE_PAINT ) {

				@Override
				protected void invalidated() {
					doRender();
				}

			};
		}
		return strokePaint;
	}

	/**
	 * Returns the primary paint used when rendering this image, falling back to the
	 * {@link #primaryPaintProperty() primary paint} when no override is set.
	 *
	 * @return the primary paint
	 */
	public Paint getPrimaryPaint() {
		return primaryPaintOverride != null ? primaryPaintOverride : primaryPaintProperty().get();
	}

	/**
	 * Sets an override for the primary paint used when rendering this image.
	 *
	 * @param paint the primary paint, or null to clear the override
	 */
	public void setPrimaryPaint( Paint paint ) {
		primaryPaintOverride = paint;
	}

	/**
	 * Returns the lazily-created, styleable property that backs the primary paint.
	 *
	 * @return the primary paint property.
	 */
	public ObjectProperty<Paint> primaryPaintProperty() {
		if( primaryPaint == null ) {
			primaryPaint = new SimpleStyleableObjectProperty<>( CSS_PRIMARY_PAINT, VectorImage.this, "primaryPaint", DEFAULT_PRIMARY_PAINT ) {

				@Override
				protected void invalidated() {
					doRender();
				}

			};
		}
		return primaryPaint;
	}

	/**
	 * Returns the secondary paint used when rendering this image, falling back to the
	 * {@link #secondaryPaintProperty() secondary paint} when no override is set.
	 *
	 * @return the secondary paint
	 */
	public Paint getSecondaryPaint() {
		return secondaryPaintOverride != null ? secondaryPaintOverride : secondaryPaintProperty().get();
	}

	/**
	 * Sets an override for the secondary paint used when rendering this image.
	 *
	 * @param paint the secondary paint, or null to clear the override
	 */
	public void setSecondaryPaint( Paint paint ) {
		secondaryPaintOverride = paint;
	}

	public ObjectProperty<Paint> secondaryPaintProperty() {
		if( secondaryPaint == null ) {
			secondaryPaint = new SimpleStyleableObjectProperty<>( CSS_SECONDARY_PAINT, VectorImage.this, "secondaryPaint", DEFAULT_SECONDARY_PAINT ) {

				@Override
				protected void invalidated() {
					doRender();
				}

			};
		}
		return secondaryPaint;
	}

	/**
	 * Returns the font used when rendering this image, falling back to the
	 * {@link #fontProperty() font} when no override is set.
	 *
	 * @return the font
	 */
	public Font getFont() {
		return fontOverride != null ? fontOverride : fontProperty().get();
	}

	/**
	 * Sets an override for the font used when rendering this image.
	 *
	 * @param font the font, or null to clear the override
	 */
	public void setFont( Font font ) {
		fontOverride = font;
	}

	/**
	 * Returns the {@link ObjectProperty} backing the {@link #getFont() font}, creating it
	 * lazily the first time it is requested.
	 *
	 * @return the backing font property
	 */
	public ObjectProperty<Font> fontProperty() {
		if( font == null ) {
			font = new SimpleStyleableObjectProperty<>( CSS_FONT, VectorImage.this, "font", DEFAULT_FONT ) {

				@Override
				protected void invalidated() {
					doRender();
				}

			};
		}
		return font;
	}

	/**
	 * Returns the graphics context used to render this image, returning the override set via
	 * {@link #setGraphicsContext2D(GraphicsContext)} when present, otherwise the context
	 * inherited from {@link Group#getGraphicsContext2D()}.
	 *
	 * @return the graphics context used when rendering this image
	 */
	@Override
	public GraphicsContext getGraphicsContext2D() {
		return graphicsContextOverride == null ? super.getGraphicsContext2D() : graphicsContextOverride;
	}

	/**
	 * Sets an override for the graphics context used when rendering this image.
	 *
	 * @param context the graphics context, or null to use the inherited context
	 */
	void setGraphicsContext2D( GraphicsContext context ) {
		this.graphicsContextOverride = context;
	}

	/**
	 * Sets the size of the grid that this image is laid out on.
	 *
	 * @param width  the grid width
	 * @param height the grid height
	 */
	public void regrid( double width, double height ) {
		setGridX( width );
		setGridY( height );
	}

	/**
	 * @return whether this image is an {@link IconTag}
	 */
	public boolean isIcon() {
		return this instanceof IconTag;
	}

	/**
	 * @return the width of this image, treated as its size
	 */
	public double getSize() {
		return getWidth();
	}

	/**
	 * Resizes this image to the given size on both axes.
	 *
	 * @param  size the size to apply on both the width and height
	 * @return this image, cast to the supplied type, to allow method chaining
	 */
	@SuppressWarnings( "unchecked" )
	public <T extends VectorImage> T resize( double size ) {
		resize( size, size );
		return (T)this;
	}

	@Override
	public void resize( double width, double height ) {
		setWidth( width );
		setHeight( height );
	}

	/**
	 * Renders this image at its current width and height.
	 *
	 * @return an image of the rendered content at this image's size
	 */
	public Image getImage() {
		return getImage( getWidth(), getHeight() );
	}

	/**
	 * Get an image of the rendered ProgramImage. A new image of size width x
	 * height is created, and the ProgramImage is rendered on the new image at the
	 * ProgramImage's location.
	 *
	 * @param width The width of the new image, not the ProgramImage width
	 * @param height The height of the new image, not the ProgramImage height
	 * @return An image with the rendered image on it
	 */
	public Image getImage( double width, double height ) {
		Scene scene = Images.getImageScene( this, width, height, null );
		WritableImage snapshot = scene.snapshot( new WritableImage( (int)width, (int)height ) );
		// WORKAROUND
		// Note that just returning the WritableImage that the snapshot() method
		// creates did not work when used as a Stage icon. However, creating a new
		// WritableImage from the snapshot image seemed to solve the problem. That
		// is why a new Writable image is created instead of just returning the
		// snapshot image.
		return new WritableImage( snapshot.getPixelReader(), (int)snapshot.getWidth(), (int)snapshot.getHeight() );
	}

	/**
	 * Creates a copy of this image, sharing its style class, size, style, properties, paint
	 * overrides, grid, and theme. The copy is created via reflection using this class's
	 * declared no-argument constructor.
	 *
	 * @return a copy of this image, cast to the supplied type, or null if the copy could not
	 *         be created
	 */
	@SuppressWarnings( "unchecked" )
	public <T extends VectorImage> T copy() {
		VectorImage copy = null;

		try {
			copy = getClass().getDeclaredConstructor().newInstance();
			copy.getStyleClass().setAll( this.getStyleClass() );
			copy.setHeight( getHeight() );
			copy.setWidth( getWidth() );
			copy.setStyle( getStyle() );
			copy.getProperties().putAll( this.getProperties() );
			copy.strokeWidthOverride = this.strokeWidthOverride;
			copy.strokePaintOverride = this.strokePaintOverride;
			copy.primaryPaintOverride = this.primaryPaintOverride;
			copy.secondaryPaintOverride = this.secondaryPaintOverride;
			copy.gridX = this.gridX;
			copy.gridY = this.gridY;
			copy.theme = this.theme;
		} catch( Exception exception ) {
			log.atWarn( exception ).log( "Unable to copy icon: %s", getClass().getName() );
		}

		return (T)copy;
	}

	/**
	 * Sets this image's theme and immediately applies it, re-rendering the image with the
	 * new theme's style.
	 *
	 * @param theme the theme to apply
	 */
	void setAndApplyTheme( Theme theme ) {
		setTheme( theme );
		applyTheme();
	}

	private void applyTheme() {
		String style = removeTheme();
		Theme theme = this.theme == null ? Theme.DARK : this.theme;
		setStyle( style == null ? theme.getFxTextBackgroundColor() : style + theme.getFxTextBackgroundColor() );
	}

	private String removeTheme() {
		String style = getStyle();
		for( Theme t : Theme.values() ) {
			int index = style.indexOf( t.getFxTextBackgroundColor() );
			if( index > -1 ) style = style.replace( t.getFxTextBackgroundColor(), "" );
		}
		setStyle( style );
		return style;
	}

}
