package com.acromere.zerra.image;

import javafx.scene.image.Image;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
/**
 * An {@link RenderedIcon} backed by a {@link javafx.scene.image.Image} loaded from a URL.
 * <p>
 * The icon renders a remotely fetched image, scaled to the icon's width and height, and
 * caches the loaded images by URL and size.
 */
public class ImageIcon extends RenderedIcon {

	private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

	private String url;

	public ImageIcon() {}

	public ImageIcon( String url ) {
		setUrl( url );
	}

	/**
	 * Loads and proves a sample image icon from a remote URL.
	 *
	 * @param commands command line arguments, ignored
	 */
	public static void main( String[] commands ) {
		ImageIcon icon = new ImageIcon( "https://www.acromere.com/download/latest/xenon/product/icon" );
		proof( icon );
	}

	/**
	 * Copies this image icon, carrying the source URL to the copy.
	 *
	 * @return a copy of this icon
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public RenderedIcon copy() {
		ImageIcon image = super.copy();
		image.url = this.url;
		return image;
	}

	/**
	 * Sets the URL from which this icon loads its image.
	 *
	 * @param url the image URL
	 * @return this icon, for method chaining
	 */
	public ImageIcon setUrl( String url ) {
		this.url = url;
		return this;
	}

	/**
	 * Renders this icon by drawing the image loaded from its {@link #getUrl() URL}.
	 */
	@Override
	protected void render() {
		drawImage( getImageFromUrl( url ) );
	}

	/**
	 * @return a runner that preloads this icon's image from its {@link #getUrl() URL}
	 */
	public Runnable getPreloadRunner() {
		return () -> getImageFromUrl( url );
	}

	private Image getImageFromUrl( String url ) {
		int w = (int)getWidth();
		int h = (int)getHeight();
		String key = url + "?w=" + w + "&h=" + h;
		return CACHE.computeIfAbsent( key, ( k ) -> new Image( url, w, h, true, true ) );
	}

}
