package com.acromere.zerra.image;

import com.acromere.util.FileUtil;
import com.acromere.util.TextUtil;
import com.acromere.zerra.color.Colors;
import com.acromere.zerra.javafx.Fx;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.Color;
import net.sf.image4j.codec.ico.ICOEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes {@link VectorImage} renderers to image and icon files.
 *
 * <p>Supports raster formats (such as PNG) and multi-size {@code .ico} icons.
 * Rendering is delegated to the FX toolkit, which requires the
 * {@link javafx.application.Platform startup thread}.</p>
 *
 * @see VectorImage
 * @see javafx.embed.swing.SwingFXUtils
 */
public class VectorImageWriter {

	/** The most recently created AWT image. */
	private BufferedImage image;

	/**
	 * Saves a single {@link VectorImage} to a file at its natural size.
	 *
	 * <p>Delegates to {@link #save(List, Path)} with a single-element list.</p>
	 *
	 * @param renderer the image renderer
	 * @param path the path of the saved image
	 * @throws Exception if an error occurs
	 * @see #save(List, Path)
	 */
	public void save( VectorImage renderer, Path path ) throws Exception {
		save( List.of( renderer ), path );
	}

	/**
	 * Saves a single {@link VectorImage} at the specified dimensions.
	 *
	 * @param renderer the image renderer
	 * @param path the path of the saved image
	 * @param width the image width
	 * @param height the image height
	 * @throws Exception if an error occurs
	 * @see #save(VectorImage, Path, double, double, Color)
	 */
	public void save( VectorImage renderer, Path path, double width, double height ) throws Exception {
		save( renderer, path, width, height, null );
	}

	/**
	 * Save a rendered image using the specified width and height. This is useful
	 * for creating an image that does not use the renderer's width and height.
	 * The renderer can also be repositioned using the relocate() method.
	 *
	 * @param renderer The image renderer
	 * @param path The path of the saved image
	 * @param width The image width
	 * @param height The image height
	 * @param fill The background fill color
	 * @throws Exception If an error occurs
	 */
	public void save( VectorImage renderer, Path path, double width, double height, Color fill ) throws Exception {
		BufferedImage image = createAwtImage( renderer, width, height, fill );
		saveAwtImages( image == null ? List.of() : List.of( image ), path );
	}

	/**
	 * Saves a list of {@link VectorImage} renderers at their natural sizes.
	 *
	 * <p>Delegates to {@link #save(List, Path, Color)} with a {@code null}
	 * background fill.</p>
	 *
	 * @param renderers the list of image renderers
	 * @param path the path of the saved image
	 * @throws Exception if an error occurs
	 * @see #save(List, Path, Color)
	 */
	public void save( List<VectorImage> renderers, Path path ) throws Exception {
		save( renderers, path, null );
	}

	/**
	 * Save a list of images as an icon. If the icon format supports multiple
	 * images, then all images are used. If the icon format does not support
	 * multiple images, then only the first image is used.
	 *
	 * @param renderers The list of image renderers
	 * @param path The path of the saved image
	 * @param fill The background fill color
	 * @throws Exception If an error occurs
	 */
	public void save( List<VectorImage> renderers, Path path, Color fill ) throws Exception {
		List<BufferedImage> images = new ArrayList<>();
		for( VectorImage renderer : renderers ) {
			images.add( createAwtImage( renderer, renderer.getWidth(), renderer.getHeight(), fill ) );
		}
		saveAwtImages( images, path );
	}

	/**
	 * Writes the given AWT images to the path, choosing the writer by file
	 * extension.
	 *
	 * @param images the images to write
	 * @param path   the path of the saved image
	 * @throws Exception if an error occurs
	 */
	private void saveAwtImages( List<BufferedImage> images, Path path ) throws Exception {
		Path parent = path.getParent();
		if( !Files.exists( parent ) ) Files.createDirectories( parent );
		File absoluteFile = path.toFile().getAbsoluteFile();
		String type = FileUtil.getExtension( path );
		if( TextUtil.isEmpty( type ) ) type = "png";
		type = type.toLowerCase();

		if( "ico".equals( type ) ) {
			ICOEncoder.write( images, absoluteFile );
		} else {
			boolean result = ImageIO.write( images.getFirst(), type, absoluteFile );
			if( !result ) throw new IllegalArgumentException( "Image writer not available for " + type );
		}
	}

	/**
	 * Renders the given renderer to an AWT image on the FX toolkit, falling
	 * back to the platform startup thread when it is not yet running.
	 *
	 * @param renderer the image renderer
	 * @param width    the image width
	 * @param height   the image height
	 * @param fill     the background fill color, or null for a transparent fill
	 * @return the rendered AWT image
	 * @throws Exception if an error occurs
	 */
	private BufferedImage createAwtImage( VectorImage renderer, double width, double height, Color fill ) throws Exception {
		String style = "";
		if( fill != null ) style += "-fx-background-color: " + Colors.toString( fill ) + ";";
		renderer.getProperties().put( "container-style", style );

		Runnable createImage = () -> doCreateAwtImageFx( renderer, width, height, fill );
		try {
			Fx.run( createImage );
		} catch( IllegalStateException exception ) {
			Platform.startup( createImage );
		}
		Fx.waitFor( 5000 );
		if( this.image == null ) throw new NullPointerException( "Image not created" );
		return this.image;
	}

	/**
	 * Creates the AWT image on the FX thread by converting the renderer's
	 * snapshot into a {@link BufferedImage}.
	 *
	 * @param renderer the image renderer
	 * @param width    the image width
	 * @param height   the image height
	 * @param fill     the background fill color, or null for a transparent fill
	 */
	private void doCreateAwtImageFx( VectorImage renderer, double width, double height, Color fill ) {
		int type = BufferedImage.TYPE_INT_ARGB;
		if( fill != null && fill.isOpaque() ) type = BufferedImage.TYPE_INT_RGB;

		BufferedImage buffer = new BufferedImage( (int)width, (int)height, type );
		this.image = SwingFXUtils.fromFXImage( renderer.getImage( width, height ), buffer );
	}

}
