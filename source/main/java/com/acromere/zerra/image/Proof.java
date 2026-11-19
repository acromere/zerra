package com.acromere.zerra.image;

import com.acromere.zerra.color.Colors;
import com.acromere.zerra.javafx.Fx;
import com.acromere.zerra.javafx.FxStarter;
import com.acromere.zerra.theme.Theme;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods that produce visual "proofs" of images and icons by
 * rendering them into a new {@link javafx.stage.Stage} window.
 *
 * <p>A proof is a side-by-side preview of a {@link VectorImage} rendered at
 * several sizes on both the
 * {@link com.acromere.zerra.theme.Theme#DARK} and
 * {@link com.acromere.zerra.theme.Theme#LIGHT} backgrounds, which is useful for
 * inspecting how an icon looks at small sizes and against different themes.</p>
 *
 * @see javafx.stage.Stage
 * @see VectorImage
 */
public class Proof {

	/** The default fill used for the proof's background panes. */
	private static final Color FILL = Color.GRAY;

	/**
	 * Proves the given node, choosing the appropriate proof method based on its
	 * type.
	 *
	 * <p>If the node is an {@link IconTag} it is proved as an icon; otherwise it
	 * is proved as a {@link javafx.scene.canvas.Canvas} image.</p>
	 *
	 * @param node the node to prove
	 * @see #proof(Canvas, double, double, Paint)
	 * @see #proof(Canvas, Color, Color)
	 */
	public static void proof( Canvas node ) {
		if( node instanceof IconTag ) {
			proof( node, null, null );
		} else {
			proof( node, node.getWidth(), node.getHeight(), null );
		}
	}

	/**
	 * Proves the given {@link javafx.scene.canvas.Canvas} image at its natural
	 * size, filling any transparent regions with the supplied background
	 * {@link Paint}.
	 *
	 * @param node the {@link javafx.scene.canvas.Canvas} to prove
	 * @param fill the background {@link Paint} used to fill transparent regions;
	 *             may be {@code null}
	 * @see #proof(Canvas, double, double, Paint)
	 */
	public static void proof( Canvas node, Paint fill ) {
		proof( node, node.getWidth(), node.getHeight(), fill );
	}

	/**
	 * Proves the given {@link javafx.scene.canvas.Canvas} image at the specified
	 * dimensions, filling any transparent regions with the supplied background
	 * {@link Paint}.
	 *
	 * <p>This is the core proof method for images; the {@link #proof(Canvas)} and
	 * {@link #proof(Canvas, Paint)} convenience overloads delegate here.</p>
	 *
	 * @param node the {@link javafx.scene.canvas.Canvas} to prove
	 * @param width the image width
	 * @param height the image height
	 * @param fill the background {@link Paint} used to fill transparent regions;
	 *             may be {@code null}
	 */
	public static void proof( Canvas node, double width, double height, Paint fill ) {
		FxStarter.startAndWait( 1000 );
		Fx.run( () -> proofImage( node, width, height, fill ) );
	}

	/**
	 * Proves the given {@link IconTag} as an icon, rendering it against the
	 * {@link com.acromere.zerra.theme.Theme#DARK} and
	 * {@link com.acromere.zerra.theme.Theme#LIGHT} backgrounds.
	 *
	 * <p>Each fill color may be {@code null}, in which case a default gray
	 * background is used for the corresponding theme.</p>
	 *
	 * @param node the {@link IconTag} to prove
	 * @param darkFill the background {@link Color} for the dark theme; may be
	 *                 {@code null}
	 * @param lightFill the background {@link Color} for the light theme; may be
	 *                  {@code null}
	 */
	public static void proof( Canvas node, Color darkFill, Color lightFill ) {
		FxStarter.startAndWait( 1000 );
		Fx.run( () -> proofIcon( (VectorImage)node, darkFill, lightFill ) );
	}

	/**
	 * Opens a stage showing the given canvas image at the given size.
	 *
	 * @param node   the canvas to prove
	 * @param width  the image width
	 * @param height the image height
	 * @param fill   the background fill, or null for a transparent fill
	 */
	private static void proofImage( Canvas node, double width, double height, Paint fill ) {
		Stage stage = new Stage();
		stage.setTitle( node.getClass().getSimpleName() );
		stage.setScene( Images.getImageScene( node, width, height, fill ) );
		// The following line causes the stage not to show on Linux
		//stage.setResizable( false );
		stage.centerOnScreen();
		stage.sizeToScene();
		stage.show();
	}

	/**
	 * Opens a stage showing the given icon on the dark and light theme
	 * backgrounds, alongside a set of progressively smaller stage icons.
	 *
	 * @param icon      the icon to prove
	 * @param darkFill  the fill for the dark theme, or null for the default
	 * @param lightFill the fill for the light theme, or null for the default
	 */
	private static void proofIcon( VectorImage icon, Color darkFill, Color lightFill ) {
		Pane darkPane = proofPane( icon.copy(), Theme.DARK, darkFill == null ? Color.web( "#404040" ) : darkFill );
		Pane lightPane = proofPane( icon.copy(), Theme.LIGHT, lightFill == null ? Color.web( "#C0C0C0" ) : lightFill );
		HBox box = new HBox( 5, darkPane, lightPane );
		box.setStyle( "-fx-background-color: " + Colors.toString( FILL ) + ";" );
		Scene scene = new Scene( box );

		List<Image> stageIcons = new ArrayList<>();
		stageIcons.add( icon.copy().resize( 256 ).getImage() );
		stageIcons.add( icon.copy().resize( 128 ).getImage() );
		stageIcons.add( icon.copy().resize( 64 ).getImage() );
		stageIcons.add( icon.copy().resize( 48 ).getImage() );
		stageIcons.add( icon.copy().resize( 32 ).getImage() );
		stageIcons.add( icon.copy().resize( 16 ).getImage() );

		Stage stage = new Stage();
		stage.setTitle( icon.getClass().getSimpleName() );
		stage.getIcons().addAll( stageIcons );
		stage.setScene( scene );
		// The following line causes the stage not to show on Linux
		//stage.setResizable( false );
		stage.centerOnScreen();
		stage.sizeToScene();
		stage.show();
	}

	/**
	 * Builds a pane showing the given icon on the given theme and fill,
	 * alongside a set of progressively smaller versions of the icon.
	 *
	 * @param icon  the icon to show
	 * @param theme the theme to apply to the icon
	 * @param fill  the fill for the pane's background
	 * @return the populated pane
	 */
	private static Pane proofPane( VectorImage icon, Theme theme, Color fill ) {
		icon.setAndApplyTheme( theme );

		String style = "";
		if( fill != null ) style += "-fx-background-color: " + Colors.toString( fill ) + ";";
		icon.getProperties().put( "container-style", style );

		ImageView imageView16 = new ImageView( Images.resample( icon.copy().resize( 16 ).getImage(), 16 ) );
		ImageView imageView32 = new ImageView( Images.resample( icon.copy().resize( 32 ).getImage(), 8 ) );

		VectorImage icon256 = icon.copy().resize( VectorImage.DEFAULT_SIZE );
		AnchorPane.setTopAnchor( icon256, 0.0 );
		AnchorPane.setLeftAnchor( icon256, 0.0 );

		VectorImage icon128 = icon.copy().resize( 128 );
		AnchorPane.setTopAnchor( icon128, 0.0 );
		AnchorPane.setLeftAnchor( icon128, 0.0 );

		VectorImage icon64 = icon.copy().resize( 64 );
		AnchorPane.setTopAnchor( icon64, 128.0 );
		AnchorPane.setLeftAnchor( icon64, 128.0 );

		VectorImage icon32 = icon.copy().resize( 32 );
		AnchorPane.setTopAnchor( icon32, 192.0 );
		AnchorPane.setLeftAnchor( icon32, 192.0 );

		VectorImage icon16 = icon.copy().resize( 16 );
		AnchorPane.setTopAnchor( icon16, 224.0 );
		AnchorPane.setLeftAnchor( icon16, 224.0 );

		VectorImage icon8 = icon.copy().resize( 8 );
		AnchorPane.setTopAnchor( icon8, 240.0 );
		AnchorPane.setLeftAnchor( icon8, 240.0 );

		AnchorPane scaledIconPane = new AnchorPane();
		scaledIconPane.getChildren().addAll( icon128, icon64, icon32, icon16, icon8 );

		StackPane iconPane256 = new StackPane( icon256 );
		StackPane iconPane16 = new StackPane( imageView16 );
		StackPane iconPane32 = new StackPane( imageView32 );

		Images.applyContainerStylesheets( icon, iconPane256 );
		Images.applyContainerStylesheets( icon, iconPane16 );
		Images.applyContainerStylesheets( icon, iconPane32 );
		Images.applyContainerStylesheets( icon, scaledIconPane );

		GridPane grid = new GridPane();
		GridPane.setRowIndex( iconPane256, 1 );
		GridPane.setRowIndex( iconPane16, 1 );
		GridPane.setRowIndex( iconPane32, 2 );
		GridPane.setRowIndex( scaledIconPane, 2 );
		GridPane.setColumnIndex( iconPane256, 1 );
		GridPane.setColumnIndex( iconPane16, 2 );
		GridPane.setColumnIndex( iconPane32, 1 );
		GridPane.setColumnIndex( scaledIconPane, 2 );
		grid.getChildren().add( iconPane256 );
		grid.getChildren().add( iconPane16 );
		grid.getChildren().add( iconPane32 );
		grid.getChildren().add( scaledIconPane );

		//		TilePane pane = new TilePane( Orientation.HORIZONTAL, 5, 5 );
		//		pane.setPrefColumns( 2 );
		//		pane.setPrefRows( 2 );
		//		pane.setHgap( 5 );
		//		pane.setVgap( 5 );
		//		pane.getChildren().add( iconPane256 );
		//		pane.getChildren().add( iconPane16 );
		//		pane.getChildren().add( iconPane32 );
		//		pane.getChildren().add( scaledIconPane );

		StackPane root = new StackPane( grid );
		root.getStyleClass().add( "root" );
		root.setStyle( "-fx-background-color: " + Colors.toString( FILL ) + ";" );
		return root;
	}

}
