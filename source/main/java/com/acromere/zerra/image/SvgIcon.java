package com.acromere.zerra.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javafx.scene.paint.Color;

/**
 * A {@link VectorIcon} defined by a set of {@link IconAction icon actions}.
 *
 * <p>
 * An {@code SvgIcon} is an icon whose appearance is produced by interpreting a
 * sequence of vector commands rather than a single image resource. The
 * abstract methods {@link #name()} and {@link #iconActions()} supply the
 * identity and the drawing commands, while {@link #create()} builds a
 * {@link RenderedIcon} that paints those commands. The {@link #render}
 * methods render the icon at a given size, optionally applying a
 * {@link Fill}.
 *
 * <p>
 * The drawing model is expressed through the nested action types
 * {@link IconAction}, {@link RenderAction}, {@link Fill}, {@link Draw},
 * {@link Clip}, {@link Xform} and {@link Restore}. A {@code RenderAction}
 * carries a {@link Fill} together with a {@link Clip} and one or more
 * {@link Draw} primitives; {@link Xform} supplies a per-action transform and
 * {@link Restore} resets the current transform.
 *
 * @see VectorIcon
 * @see RenderedIcon
 */
public abstract class SvgIcon extends VectorIcon {

	/**
	 * The unique name identifying this icon.
	 *
	 * @return the icon name
	 * @throws Exception if the name cannot be determined
	 */
	abstract String name() throws Exception;

	/**
	 * Returns the icon actions that define this icon's appearance.
	 *
	 * @return the iterable of {@link IconAction}s
	 * @throws IOException if reading the icon definition fails
	 */
	abstract Iterable<IconAction> iconActions() throws IOException;

	/**
	 * Returns the icon actions that define this icon's appearance.
	 *
	 * @return the iterable of {@link IconAction}s
	 */
	default Iterable<IconAction> iconActions() {
		return iconActions();
	}

	/**
	 * Creates a {@link RenderedIcon} that paints this icon's actions.
	 *
	 * @return a new rendered icon
	 * @throws Exception if rendering fails
	 */
	abstract RenderedIcon create() throws Exception;

	/**
	 * Renders this icon at the given size with no fill.
	 *
	 * @param width the target width, in pixels
	 * @param height the target height, in pixels
	 * @return a rendered icon at the requested size
	 * @throws Exception if rendering fails
	 */
	RenderedIcon render(double width, double height) throws Exception {
		return render(width, height, null);
	}

	/**
	 * Renders this icon at the given size using the supplied fill.
	 *
	 * @param width the target width, in pixels
	 * @param height the target height, in pixels
	 * @param fill the fill to apply, or {@code null} for the default
	 * @return a rendered icon at the requested size
	 * @throws Exception if rendering fails
	 */
	RenderedIcon render(double width, double height, Fill fill) throws Exception {
		RenderedIcon ri = create();
		ri.render(ri.width(), ri.height(), fill);
		ri.width(width);
		ri.height(height);
		return ri;
	}

	/**
	 * A drawing command.
	 *
	 * <p>
	 * An {@code IconAction} is either a {@link RenderAction}, which paints a
	 * region, or a {@link Restore}, which undoes the most recent
	 * {@link Xform}.
	 */
	public static abstract class IconAction {

		/**
		 * Returns the type of this action.
		 *
		 * @return the action type
		 */
		public abstract Class<? extends IconAction> type();

		/**
		 * Returns the render action associated with this action, or
		 * {@code null} if this is not a {@link RenderAction}.
		 *
		 * @return the render action, or {@code null}
		 */
		public RenderAction asRender() {
			return null;
		}

		/**
		 * Returns the restore action associated with this action, or
		 * {@code null} if this is not a {@link Restore}.
		 *
		 * @return the restore action, or {@code null}
		 */
		public Restore asRestore() {
			return null;
		}

		/**
		 * Returns the icon action type for the given class.
		 *
		 * @param <T> the action type
		 * @param c the action class
		 * @return the class cast to {@code Class<T>}
		 */
		@SuppressWarnings("unchecked")
		public static <T extends IconAction> Class<T> ica(Class<T> c) {
			return c;
		}
	}

	/**
	 * A render action that paints a region.
	 *
	 * <p>
	 * A {@code RenderAction} combines a {@link Fill}, a {@link Clip} and one
	 * or more {@link Draw} primitives. It may also carry a {@link Xform}
	 * that is applied before painting and, when {@link #restore()} is
	 * {@code true}, reset afterward.
	 */
	public static abstract class RenderAction extends IconAction {

		/**
		 * The class of a {@link RenderAction}.
		 */
		public static Class<RenderAction> R = ica(RenderAction.class);

		/**
		 * Returns the render action's class.
		 *
		 * @return {@link #R}
		 */
		@Override
		public Class<? extends IconAction> type() {
			return R;
		}

		/**
		 * Returns the fill used to paint this action.
		 *
		 * @return the fill
		 */
		public abstract Fill fill();

		/**
		 * Returns the clip region used to constrain this action.
		 *
		 * @return the clip
		 */
		public abstract Clip clip();

		/**
		 * Returns the primitive used to paint this action.
		 *
		 * @return the draw
		 */
		public abstract Draw draw();

		/**
		 * Returns the transform applied to this action, or {@code null}.
		 *
		 * @return the transform, or {@code null}
		 */
		public Xform xform() {
			return null;
		}

		/**
		 * Indicates whether this action restores the transform afterwards.
		 *
		 * @return {@code true} if the transform should be restored
		 */
		public boolean restore() {
			return false;
		}

		/**
		 * Indicates whether this action applies a fill.
		 *
		 * @return {@code true} if this action has a fill
		 */
		public abstract boolean hasFill();

		/**
		 * Indicates whether this action applies a clip.
		 *
		 * @return {@code true} if this action has a clip
		 */
		public abstract boolean hasClip();

		/**
		 * Indicates whether this action has a primitive.
		 *
		 * @return {@code true} if this action has a {@link Draw}
		 */
		public abstract boolean hasDraw();

		/**
		 * Indicates whether this action applies a transform.
		 *
		 * @return {@code true} if this action has an {@link Xform}
		 */
		public boolean hasXform() {
			return xform() != null;
		}

		/**
		 * Returns this action cast to {@link RenderAction}.
		 *
		 * @return this
		 */
		@Override
		public RenderAction asRender() {
			return this;
		}

		/**
		 * Renders this action with the given renderer.
		 *
		 * @param r the renderer
		 */
		@SuppressWarnings("fallthrough")
		public void render(RenderedIcon r) {
			if (hasXform())
				xform().apply(r);
			if (hasFill())
				fill().apply(r);
			if (hasClip())
				clip().apply(r);
			if (hasDraw())
				draw().apply(r);
			if (restore())
				r.restore();
		}

		/**
		 * Creates a render action from a fill, clip and primitive.
		 *
		 * @param fill the fill
		 * @param clip the clip
		 * @param draw the primitive
		 * @return a new {@link RenderAction}
		 */
		public static RenderAction ra(Fill fill, Clip clip, Draw draw) {
			return ra(fill, clip, draw, null, false);
		}

		/**
		 * Creates a render action from a fill, clip and primitive.
		 *
		 * @param fill the fill, or {@code null}
		 * @param clip the clip, or {@code null}
		 * @param draw the primitive, or {@code null}
		 * @return a new {@link RenderAction}
		 */
		public static RenderAction ra(Fill fill, Clip clip, Draw draw) {
			return ra(fill, clip, draw, null, false);
		}

		/**
		 * Creates a render action with a transform and restore flag.
		 *
		 * @param fill the fill, or {@code null}
		 * @param clip the clip, or {@code null}
		 * @param draw the primitive, or {@code null}
		 * @param xform the transform, or {@code null}
		 * @param restore whether to restore the transform
		 * @return a new {@link RenderAction}
		 */
		public static RenderAction ra(Fill fill, Clip clip, Draw draw, Xform xform, boolean restore) {
			return new RenderAction() {
				@Override
				public Fill fill() {
					return fill;
				}

				@Override
				public boolean hasFill() {
					return fill != null;
				}

				@Override
				public Clip clip() {
					return clip;
				}

				@Override
				public boolean hasClip() {
					return clip != null;
				}

				@Override
				public Draw draw() {
					return draw;
				}

				@Override
				public boolean hasDraw() {
					return draw != null;
				}

				@Override
				public Xform xform() {
					return xform;
				}

				@Override
				public boolean hasXform() {
					return xform != null;
				}

				@Override
				public boolean restore() {
					return restore;
				}
			};
		}
	}

	/**
	 * A fill that defines how a region is painted.
	 *
	 * <p>
	 * A {@code Fill} may be a {@link Color}, an {@link ImageIcon} or a
	 * {@link VectorImage}, or one of the predefined constants {@link #WHITE},
	 * {@link #BLACK} and {@link #CLEAR} for common cases.
	 */
	public interface Fill {

		/**
		 * Applies this fill to the given renderer.
		 *
		 * @param r the renderer
		 */
		void apply(RenderedIcon r);

		/**
		 * Returns the fill as a {@link Color}, or {@code null}.
		 *
		 * @return the color, or {@code null}
		 */
		Color asColor();

		/**
		 * A white fill.
		 */
		Fill WHITE = new Fill() {
			@Override
			public void apply(RenderedIcon r) {
				r.fillColor(Color.WHITE);
			}

			@Override
			public Color asColor() {
				return Color.WHITE;
			}
		};

		/**
		 * A black fill.
		 */
		Fill BLACK = new Fill() {
			@Override
			public void apply(RenderedIcon r) {
				r.fillColor(Color.BLACK);
			}

			@Override
			public Color asColor() {
				return Color.BLACK;
			}
		};

		/**
		 * A transparent fill that clears the current region.
		 */
		Fill CLEAR = new Fill() {
			@Override
			public void apply(RenderedIcon r) {
				r.fillColor(Color.TRANSPARENT);
			}

			@Override
			public Color asColor() {
				return Color.TRANSPARENT;
			}
		};

		/**
		 * Returns a fill backed by the given color.
		 *
		 * @param c the color
		 * @return a fill that paints the color
		 */
		static Fill fill(Color c) {
			return new Fill() {
				@Override
				public void apply(RenderedIcon r) {
					r.fillColor(c);
				}

				@Override
				public Color asColor() {
					return c;
				}
			};
		}

		/**
		 * Returns a fill backed by the given icon.
		 *
		 * @param icon the icon
		 * @return a fill that paints the icon
		 */
		static Fill fill(Icon icon) {
			return new Fill() {
				@Override
				public void apply(RenderedIcon r) {
					r.drawImage(icon);
				}

				@Override
				public Color asColor() {
					return null;
				}
			};
		}
	}

	/**
	 * A primitive that paints a region using the current fill.
	 */
	public interface Draw {

		/**
		 * Paints this primitive onto the given renderer.
		 *
		 * @param r the renderer
		 */
		void apply(RenderedIcon r);

		/**
		 * Returns a {@code Draw} that draws the given image.
		 *
		 * @param ii the image icon
		 * @return a primitive that paints the image
		 */
		static Draw dr(ImageIcon ii) {
			return new Draw() {
				@Override
				public void apply(RenderedIcon r) {
					r.drawImage(ii);
				}
			};
		}

		/**
		 * Returns a {@code Draw} that draws the given vector image.
		 *
		 * @param vi the vector image
		 * @return a primitive that paints the vector image
		 */
		static Draw dr(VectorImage vi) {
			return new Draw() {
				@Override
				public void apply(RenderedIcon r) {
					r.drawImage(vi);
				}
			};
		}
	}

	/**
	 * A clip region that constrains where a fill is painted.
	 *
	 * <p>
	 * A {@code Clip} is built from {@link Draw} primitives that define the
	 * region to retain, optionally combined with a nested {@code Clip}.
	 */
	public interface Clip {

		/**
		 * Applies this clip to the given renderer.
		 *
		 * @param r the renderer
		 */
		void apply(RenderedIcon r);

		/**
		 * Returns a clip that retains only the given primitive.
		 *
		 * @param d the primitive
		 * @return a clip for the primitive
		 */
		static Clip cl(Draw d) {
			return cl(d, null);
		}

		/**
		 * Returns a clip that combines a primitive with a nested clip.
		 *
		 * @param d the primitive
		 * @param c the nested clip, or {@code null}
		 * @return a clip for the primitive and nested clip
		 */
		static Clip cl(Draw d, Clip c) {
			return new Clip() {
				@Override
				public void apply(RenderedIcon r) {
					if (c != null)
						c.apply(r);
					r.clip(true);
					d.apply(r);
				}
			};
		}
	}

	/**
	 * A transform applied before a {@link RenderAction} is painted.
	 *
	 * <p>
	 * A {@code Xform} may apply a horizontal and/or vertical scale and a
	 * rotation about a given point. When the associated {@link RenderAction}
	 * has {@link RenderAction#restore()} set to {@code true}, the transform
	 * is undone after painting.
	 */
	public interface Xform {

		/**
		 * Applies this transform to the given renderer.
		 *
		 * @param r the renderer
		 */
		void apply(RenderedIcon r);

		/**
		 * Returns a scale transform with a default rotation of 0.
		 *
		 * @param sx the horizontal scale factor
		 * @param sy the vertical scale factor
		 * @return a scale transform
		 */
		static Xform xf(double sx, double sy) {
			return xf(sx, sy, 0, 0, 0);
		}

		/**
		 * Returns a transform with scale and rotation about a point.
		 *
		 * @param sx the horizontal scale factor
		 * @param sy the vertical scale factor
		 * @param rotate the rotation, in degrees
		 * @param x the rotation center x-coordinate
		 * @param y the rotation center y-coordinate
		 * @return a transform with scale and rotation
		 */
		static Xform xf(double sx, double sy, double rotate, double x, double y) {
			return new Xform() {
				@Override
				public void apply(RenderedIcon r) {
					if (sx != 1 || sy != 1)
						r.scale(sx, sy);
					if (rotate != 0)
						r.rotate(rotate, x, y);
				}
			};
		}
	}

	/**
	 * An icon action that restores the most recent transform.
	 */
	public static final class Restore extends IconAction {

		/**
		 * The class of a {@link Restore}.
		 */
		public static Class<Restore> R = ica(Restore.class);

		/**
		 * Returns the restore action's class.
		 *
		 * @return {@link #R}
		 */
		@Override
		public Class<? extends IconAction> type() {
			return R;
		}

		/**
		 * Returns this action cast to {@link Restore}.
		 *
		 * @return this
		 */
		@Override
		public Restore asRestore() {
			return this;
		}

		/**
		 * Restores the current transform on the given renderer.
		 *
		 * @param r the renderer
		 */
		public void render(RenderedIcon r) {
			r.restore();
		}

		/**
		 * Returns a new {@link Restore}.
		 *
		 * @return a restore action
		 */
		public static Restore rs() {
			return new Restore();
		}
	}

	/**
	 * Interprets SVG markup into a sequence of {@link IconAction}s.
	 *
	 * <p>
	 * The static {@link #icon} methods read an SVG document, extract a list of
	 * top-level elements and, for each element that begins with {@code <a>},
	 * build a {@link RenderAction} that paints the element and applies the
	 * {@code width} attribute of its parent when present.
	 */
	public static Iterable<IconAction> icon(Node root) {
		Iterable<Node> e = root.childNodes().stream().filter(n -> n.getNodeName().startsWith("<a")).collect(java.util.stream.Collectors.toList()).stream()
			.map((n) -> {
				Node parent = n.getParentNode();
				NamedNodeMap attrs = parent.getAttributes();
				Node wnode = attrs != null ? attrs.getNamedItem("width") : null;
				String wstr = wnode != null ? wnode.getNodeValue() : "0";
				double w = Double.parseDouble(wstr);
				return (IconAction) RenderAction.ra(null, null, dr(icon(n)), null, false);
			}).collect(java.util.stream.Collectors.toList()).stream();
		return e;
	}

	/**
	 * Interprets an SVG file into a sequence of {@link IconAction}s.
	 *
	 * @param path the path to the SVG file
	 * @return the icon actions
	 * @throws Exception if the file cannot be read or parsed
	 */
	public static Iterable<IconAction> icon(String path) throws Exception {
		return icon(new org.w3c.dom.DocumentBuilderFactory().newInstance().newDocumentBuilder().parse(new java.io.File(path)));
	}
}
