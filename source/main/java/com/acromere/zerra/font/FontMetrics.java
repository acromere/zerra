package com.acromere.zerra.font;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import lombok.Getter;

/**
 * The typographic metrics for a {@link Font}, and the bounds of strings rendered
 * in that font.
 *
 * <p>The lead, ascent, descent and spacing are measured from a single {@link Text}
 * node using the {@link TextBoundsType#LOGICAL} bounds type, while the bounds of
 * individual strings are measured using the {@link TextBoundsType#VISUAL} bounds
 * type. Each instance measures a single font and is not thread-safe.</p>
 */
public class FontMetrics {

	/** The text node used to measure the font and the bounds of strings. */
	private final Text text;

	/** The lead of the font. */
	@Getter
	private final double lead;

	/** The ascent of the font. */
	@Getter
	private final double ascent;

	/** The descent of the font. */
	@Getter
	private final double descent;

	/** The line spacing of the font. */
	@Getter
	private final double spacing;

	/**
	 * Compute the metrics for the specified font.
	 *
	 * @param font The font to measure
	 */
	public FontMetrics( Font font ) {
		text = new Text();
		text.setFont( font );

		text.setBoundsType( TextBoundsType.LOGICAL );
		Bounds bounds = text.getLayoutBounds();

		// Lead will be negative
		lead = -bounds.getMinX();
		// Ascent will be negative
		ascent = -bounds.getMinY();
		// Descent will be negative
		descent = -bounds.getMaxY();
		// Spacing will be positive
		spacing = bounds.getHeight();

		text.setBoundsType( TextBoundsType.VISUAL );
	}

	/**
	 * Compute the bounds of a string rendered in the font.
	 *
	 * @param text The text to measure
	 * @return The bounds of the string, using the visual bounds type
	 */
	public Bounds computeStringBounds( String text ) {
		this.text.setText( text );
		return this.text.getLayoutBounds();
	}

	/**
	 * Compute the width of a string rendered in the font.
	 *
	 * @param text The text to measure
	 * @return The width of the string, using the visual bounds type
	 */
	public double computeStringWidth( String text ) {
		this.text.setText( text );
		return this.text.getLayoutBounds().getWidth();
	}

	/**
	 * Compute the height of a string rendered in the font.
	 *
	 * @param text The text to measure
	 * @return The height of the string, using the visual bounds type
	 */
	public double computeStringHeight( String text ) {
		this.text.setText( text );
		return this.text.getLayoutBounds().getHeight();
	}

	/**
	 * Return a string representation of this font metrics.
	 *
	 * @return A string representation of this font metrics
	 */
	@Override
	public String toString() {
		return "FontMetrics [ascent: " + ascent + ", decent: " + descent + ", spacing: " + spacing + "]";
	}

}
