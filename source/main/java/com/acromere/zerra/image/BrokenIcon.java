package com.acromere.zerra.image;

import javafx.scene.paint.Color;

/**
 * A broken-icon placeholder drawn as a red "X" on a transparent background.
 *
 * <p>Used as a visible marker when an image or icon fails to load, so callers
 * can substitute a {@link #render() rendered} {@link Canvas} instead of an
 * empty area.</p>
 *
 * @see RenderedIcon
 * @see javafx.scene.canvas.Canvas
 */
public class BrokenIcon extends RenderedIcon {

	/**
	 * Entry point for the package's interactive proof harness.
	 *
	 * @param commands the command-line arguments, ignored
	 * @see Proof
	 */
	public static void main( String[] commands ) {
		proof( new BrokenIcon() );
	}

	/**
	 * Renders the placeholder: a red "X" spanning the icon bounds.
	 *
	 * @see RenderedIcon#render()
	 */
	@Override
	public void render() {
		double min = g( 8 );
		double max = g( 24 );

		setDrawPaint( Color.RED );
		setDrawWidth( g( 6 ) );

		startPath();
		moveTo( min, min );
		lineTo( max, max );
		moveTo( max, min );
		lineTo( min, max );
		draw();
	}

}
