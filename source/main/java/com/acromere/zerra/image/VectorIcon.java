package com.acromere.zerra.image;

/**
 * A concrete {@link VectorImage} that also behaves as an {@link IconTag} icon.
 * <p>
 * This is the default concrete vector icon: it inherits all rendering and
 * styling behavior from {@link VectorImage} and exposes the icon contract, so
 * callers can use a {@code VectorIcon} anywhere an {@link IconTag} is expected.
 */
public class VectorIcon extends VectorImage implements IconTag {

	/** Constructs a vector icon with the default grid dimensions. */
	@SuppressWarnings( "unused" )
	VectorIcon() {
		super();
	}

	/**
	 * Constructs a vector icon with the given grid dimensions.
	 *
	 * @param gridX the X dimension of the icon grid
	 * @param gridY the Y dimension of the icon grid
	 */
	VectorIcon( double gridX, double gridY ) {
		super( gridX, gridY );
	}

}
