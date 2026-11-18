package com.acromere.zerra.image;

/**
 * A {@link RenderedImage} that also knows how to behave as an {@link IconTag}
 * icon. Subclasses implement the concrete rendering; this base supplies the
 * constructors that establish the icon's grid origin.
 */
public abstract class RenderedIcon extends RenderedImage implements IconTag {

	/**
	 * Creates an icon at the default grid origin.
	 */
	protected RenderedIcon() {
		super();
	}

	/**
	 * Creates an icon anchored at the given grid position.
	 *
	 * @param gridX the grid column of the icon's origin.
	 * @param gridY the grid row of the icon's origin.
	 */
	protected RenderedIcon( double gridX, double gridY ) {
		super( gridX, gridY );
	}

}
