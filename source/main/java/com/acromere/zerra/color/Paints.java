package com.acromere.zerra.color;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;

/**
 * Utility class for parsing and converting various paint types.
 */
public class Paints {

	private Paints() {}

	/**
	 * Convert a Paint object to its string representation.
	 *
	 * @param paint The paint to convert.
	 * @return The string representation of the paint, or null if the paint is null.
	 */
	public static String toString( Paint paint ) {
		return switch( paint ) {
			case null -> null;
			case Color color -> toString( color );
			case LinearGradient gradient -> toString( gradient );
			case RadialGradient gradient -> toString( gradient );
			default -> throw new IllegalArgumentException( "Unknown paint type: " + paint.getClass().getName() );
		};
	}

	/**
	 * Convert a Color to its string representation.
	 *
	 * @param color The color to convert.
	 * @return The string representation of the color, or null if the color is null.
	 */
	public static String toString( Color color ) {
		if( color == null ) return null;
		return Colors.toString( color );
	}

	/**
	 * Convert a LinearGradient to its string representation.
	 *
	 * @param gradient The gradient to convert.
	 * @return The string representation of the gradient, or null if the gradient is null.
	 */
	public static String toString( LinearGradient gradient ) {
		if( gradient == null ) return null;
		return gradient.toString();
	}

	/**
	 * Convert a RadialGradient to its string representation.
	 *
	 * @param gradient The gradient to convert.
	 * @return The string representation of the gradient, or null if the gradient is null.
	 */
	public static String toString( RadialGradient gradient ) {
		if( gradient == null ) return null;
		return gradient.toString();
	}

	/**
	 * Parse a string into a Paint object.
	 *
	 * @param string The string to parse.
	 * @return The parsed paint object, or null if the string is null.
	 */
	public static Paint parse( String string ) {
		if( string == null ) return null;

		if( string.startsWith( "#" ) ) return Colors.parse( string );
		if( string.startsWith( "linear-gradient" ) ) return LinearGradient.valueOf( string );
		if( string.startsWith( "radial-gradient" ) ) return RadialGradient.valueOf( string );

		return Colors.parse( string );
	}

	/**
	 * Parse a string into a Paint object, returning null on exception.
	 *
	 * @param string The string to parse.
	 * @return The parsed paint object, or null if parsing fails or the string is null.
	 */
	public static Paint parseWithNullOnException( String string ) {
		try {
			return parse( string );
		} catch( IllegalArgumentException exception ) {
			return null;
		}
	}

	/**
	 * Check if a paint is transparent.
	 *
	 * @param paint The paint to check.
	 * @return True if the paint is transparent, false otherwise.
	 */
	@SuppressWarnings( "unused" )
	public static boolean isTransparent( Paint paint ) {
		if( paint == null ) return true;
		if( paint instanceof Color color ) return Colors.isTransparent( color );
		return false;
	}

}
