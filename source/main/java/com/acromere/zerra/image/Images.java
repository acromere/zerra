package com.acromere.zerra.image;

import com.acromere.zerra.theme.Theme;
import com.acromere.zerra.Stylesheet;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Utility class for producing and transforming {@link Image images} from
 * {@link VectorImage vector icons}.
 *
 * <p>Provides pixel scaling, stage-icon generation at multiple sizes, and
 * helpers for wrapping a rendered {@link Canvas} in a {@link Scene} for export
 * or preview.</p>
 *
 * @see VectorImage
 * @see javafx.scene.image.Image
 */
public class Images {

	/**
	 * Resamples an {@link Image} to a new size using nearest-neighbor scaling.
	 *
	 * <p>Each source pixel is replicated into a {@code scale} x {@code scale}
	 * block, preserving crisp edges without interpolation.</p>
	 *
	 * @param input the source image
	 * @param scale the integer scaling factor; the output is
	 *        {@code scale} times the input width and height
	 * @return a new {@link WritableImage} with the scaled dimensions
	 */
	public static Image resample( Image input, int scale ) {
		int w = (int)input.getWidth();
		int h = (int)input.getHeight();

		WritableImage output = new WritableImage( w * scale, h * scale );

		PixelReader reader = input.getPixelReader();
		PixelWriter writer = output.getPixelWriter();

		for( int y = 0; y < h; y++ ) {
			for( int x = 0; x < w; x++ ) {
				final int argb = reader.getArgb( x, y );
				for( int dy = 0; dy < scale; dy++ ) {
					for( int dx = 0; dx < scale; dx++ ) {
						writer.setArgb( x * scale + dx, y * scale + dy, argb );
					}
				}
			}
		}

		return output;
	}

	/**
	 * Returns a default set of stage icons for the given image.
	 *
	 * <p>Delegates to {@link #getStageIcons(VectorImage, int...)} with the
	 * standard sizes 16, 24, 32, 48, 64, 128, and 256.</p>
	 *
	 * @param image the source image; if {@code null}, a
	 *        {@link BrokenIcon} is used for each size
	 * @return an array of {@link Image} instances at the standard sizes
	 * @see #getStageIcons(VectorImage, int...)
	 */
	public static Image[] getStageIcons( VectorImage image ) {
		return getStageIcons( image, 16, 24, 32, 48, 64, 128, 256 );
	}

	/**
	 * Produces stage icons at each of the given pixel sizes.
	 *
	 * @param image the source image; if {@code null}, a
	 *        {@link BrokenIcon} is used for each size
	 * @param sizes the pixel sizes for the resulting icons
	 * @return an array of {@link Image} instances, one per requested size
	 * @see #getStageIcons(VectorImage)
	 */
	public static Image[] getStageIcons( VectorImage image, int... sizes ) {
		Image[] images = new Image[ sizes.length ];
		for( int index = 0; index < sizes.length; index++ ) {
			if( image == null ) image = new BrokenIcon();
			images[ index ] = image.resize( sizes[ index ] ).getImage();
		}
		return images;
	}

	/**
	 * Wraps a rendered {@link Canvas} in a {@link Scene} for export or preview.
	 *
	 * <p>If the canvas is a {@link VectorImage}, its theme is re-applied. The
	 * canvas is placed in a {@link Pane} with an empty background and the
	 * container stylesheets are applied via
	 * {@link #applyContainerStylesheets(Canvas, Parent)}.</p>
	 *
	 * @param canvas the canvas to wrap
	 * @param imageWidth the preferred width of the wrapping pane
	 * @param imageHeight the preferred height of the wrapping pane
	 * @param fill the scene fill; a {@code null} value yields a transparent fill
	 * @return a {@link Scene} containing the canvas
	 * @see #applyContainerStylesheets(Canvas, Parent)
	 */
	static Scene getImageScene( Canvas canvas, double imageWidth, double imageHeight, Paint fill ) {
		if( canvas instanceof VectorImage image ) applyTheme( image, image.getTheme() );
		Pane pane = new Pane( canvas );
		pane.setBackground( Background.EMPTY );
		pane.setPrefSize( imageWidth, imageHeight );
		applyContainerStylesheets( canvas, pane );
		Scene scene = new Scene( pane );
		scene.setFill( fill == null ? Color.TRANSPARENT : fill );
		return scene;
	}

	/**
	 * Applies the default container stylesheet to a node, along with any
	 * container-style property stored on the canvas.
	 *
	 * <p>Adds {@link Stylesheet#ZERRA} to the node's stylesheets and, when the
	 * canvas carries a {@code "container-style"} property, sets it as the node's
	 * style.</p>
	 *
	 * @param image the canvas whose container-style property is consulted
	 * @param node the node that receives the stylesheet
	 */
	static void applyContainerStylesheets( Canvas image, Parent node ) {
		// Add the default container stylesheet
		node.getStylesheets().add( Stylesheet.ZERRA );

		// Add extra container style
		String style = (String)image.getProperties().get( "container-style" );
		if( style != null ) node.setStyle( style );
	}

	/**
	 * Applies the given theme's text background color to the node's style.
	 *
	 * @param node  the image to apply the theme to
	 * @param theme the theme to apply
	 */
	private static void applyTheme( VectorImage node, Theme theme ) {
		String style = removeTheme( node );
		node.setStyle( style == null ? theme.getFxTextBackgroundColor() : style + theme.getFxTextBackgroundColor() );
	}

	/**
	 * Removes every theme's text background color from the node's style,
	 * applies the resulting style, and returns it.
	 *
	 * @param node the node to remove the theme color from
	 * @return the style with the theme color removed, or null
	 */
	private static String removeTheme( Canvas node ) {
		String style = node.getStyle();
		for( Theme t : Theme.values() ) {
			int index = style.indexOf( t.getFxTextBackgroundColor() );
			if( index > -1 ) style = style.replace( t.getFxTextBackgroundColor(), "" );
		}
		node.setStyle( style );
		return style;
	}

}
