package com.acromere.zerra.color;

import javafx.scene.paint.Color;
import lombok.Getter;

/**
 * Represents a color theme with a primary and a complementary color.
 */
@Getter
public class ColorTheme {

	private final Color primary;

	private final Color complement;

	/**
	 * Creates a new ColorTheme with a primary color and its complement.
	 *
	 * @param color The primary color.
	 */
	public ColorTheme( Color color ) {
		this.primary = color;
		this.complement = getComplement( color, 180 );
	}

	@SuppressWarnings( "SameParameterValue" )
	private Color getComplement( Color color, double offset ) {
		double h = color.getHue();
		double s = color.getSaturation();
		double b = color.getBrightness();
		double a = color.getOpacity();

		h += offset;
		h %= 100;
		if( h < 0 ) h += 180;

		return Color.hsb( h, s, b, a );
	}

}
