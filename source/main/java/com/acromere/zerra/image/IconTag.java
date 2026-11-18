package com.acromere.zerra.image;

import javafx.scene.paint.Color;

/**
 * Convenience helpers for working with {@link VectorImage} icons. The static
 * methods add the shared {@code "xe-icon"} style class and route an icon through
 * the {@link Proof} renderer so it can be exported to a file.
 */
public interface IconTag {

	/**
	 * Marks the given image as an icon by adding the shared {@code "xe-icon"}
	 * style class, which lets a stylesheet style all icons uniformly.
	 *
	 * @param image the image to tag as an icon; must not be {@code null}.
	 */
	static void asIcon( VectorImage image ) {
		image.getStyleClass().add( "xe-icon" );
	}

	/**
	 * Renders and saves the given icon using the default proof colors by
	 * delegating to {@link Proof#proof(VectorImage)}.
	 *
	 * @param icon the icon to render and save; must not be {@code null}.
	 */
	static void proof( VectorImage icon ) {
		Proof.proof( icon );
	}

	/**
	 * Renders and saves the given icon using the supplied light and dark fill
	 * colors by delegating to {@link Proof#proof(VectorImage, Color, Color)}.
	 *
	 * @param icon     the icon to render and save; must not be {@code null}.
	 * @param darkFill the fill color used for the "dark" theme variant.
	 * @param lightFill the fill color used for the "light" theme variant.
	 */
	static void proof( VectorImage icon, Color darkFill, Color lightFill ) {
		Proof.proof( icon, darkFill, lightFill );
	}

}
