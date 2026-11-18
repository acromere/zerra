package com.acromere.zerra.color;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A collection of standard material colors.
 */
public class MaterialColor {

	/** The Material Design {@code RED} color. */
	public static final Color RED = Color.web( "#F44336" );

	/** The Material Design {@code PINK} color. */
	public static final Color PINK = Color.web( "#E91E63" );

	/** The Material Design {@code PURPLE} color. */
	public static final Color PURPLE = Color.web( "#9C27B0" );

	/** The Material Design {@code DEEP_PURPLE} color. */
	public static final Color DEEP_PURPLE = Color.web( "#673AB7" );

	/** The Material Design {@code INDIGO} color. */
	public static final Color INDIGO = Color.web( "#3F51B5" );

	/** The Material Design {@code BLUE} color. */
	public static final Color BLUE = Color.web( "#2196F3" );

	/** The Material Design {@code LIGHT_BLUE} color. */
	public static final Color LIGHT_BLUE = Color.web( "#03A9F4" );

	/** The Material Design {@code CYAN} color. */
	public static final Color CYAN = Color.web( "#00BCD4" );

	/** The Material Design {@code TEAL} color. */
	public static final Color TEAL = Color.web( "#009688" );

	/** The Material Design {@code GREEN} color. */
	public static final Color GREEN = Color.web( "#4CAF50" );

	/** The Material Design {@code LIGHT_GREEN} color. */
	public static final Color LIGHT_GREEN = Color.web( "#8BC34A" );

	/** The Material Design {@code LIME} color. */
	public static final Color LIME = Color.web( "#CDDC39" );

	/** The Material Design {@code YELLOW} color. */
	public static final Color YELLOW = Color.web( "#FFEB3B" );

	/** The Material Design {@code AMBER} color. */
	public static final Color AMBER = Color.web( "#FFC107" );

	/** The Material Design {@code ORANGE} color. */
	public static final Color ORANGE = Color.web( "#FF9800" );

	/** The Material Design {@code DEEP_ORANGE} color. */
	public static final Color DEEP_ORANGE = Color.web( "#FF5722" );

	/** The Material Design {@code BROWN} color. */
	public static final Color BROWN = Color.web( "#725548" );

	/** The Material Design {@code GRAY} color. */
	public static final Color GRAY = Color.web( "#9E9E9E" );

	/** The Material Design {@code BLUE_GRAY} color. */
	public static final Color BLUE_GRAY = Color.web( "#607D8B" );

	/** The set of all material colors. */
	private static final Set<Color> COLORS;

	/** The display name for each material color. */
	private static final Map<Color, String> NAMES;

	static {
		COLORS = new CopyOnWriteArraySet<>();
		NAMES = new HashMap<>();

		init( RED, "Red" );
		init( PINK, "Pink" );
		init( PURPLE, "Purple" );
		init( DEEP_PURPLE, "Deep Purple" );
		init( INDIGO, "Indigo" );
		init( BLUE, "Blue" );
		init( LIGHT_BLUE, "Pale Blue" );
		init( CYAN, "Cyan" );
		init( TEAL, "Teal" );
		init( GREEN, "Green" );
		init( LIGHT_GREEN, "Pale Green" );
		init( LIME, "Lime" );
		init( YELLOW, "Yellow" );
		init( AMBER, "Amber" );
		init( ORANGE, "Orange" );
		init( DEEP_ORANGE, "Deep Orange" );

		init( BROWN, "Brown" );
		init( GRAY, "Gray" );
		init( BLUE_GRAY, "Blue Gray" );
	}

	/**
	 * Initialize the color set and names map.
	 *
	 * @param color The color to add.
	 * @param name The name of the color.
	 */
	private static void init( Color color, String name ) {
		COLORS.add( color );
		NAMES.put( color, name );
	}

	/**
	 * Get all material colors.
	 *
	 * @return A set of all material colors.
	 */
	public static Set<Color> getColors() {
		return COLORS;
	}

	/**
	 * Get the name of a material color.
	 *
	 * @param color The color to get the name for.
	 * @return The name of the color, or null if not found.
	 */
	public static String getName( Color color ) {
		return NAMES.get( color );
	}

}
