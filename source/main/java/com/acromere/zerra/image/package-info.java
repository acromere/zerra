/**
 * Image and icon support for Zerra.
 *
 * <p>This package provides the abstractions and concrete classes for representing,
 * creating, rendering and writing image data used throughout Zerra's scene graph.
 * Icons are modelled as {@link com.acromere.zerra.image.VectorIcon} implementations
 * that can render themselves to a {@link com.acromere.zerra.image.RenderedImage}, while
 * {@link com.acromere.zerra.image.VectorImage} describes an image source (file, stream, URL
 * or raw text) that a proof consumes to produce pixels.</p>
 *
 * <ul>
 *   <li><b>Icon helpers</b> - {@link com.acromere.zerra.image.IconTag} for shared icon utilities and {@link com.acromere.zerra.image.BrokenIcon} as the missing-resource placeholder.</li>
 *   <li><b>Image resources</b> - {@link com.acromere.zerra.image.ImageIcon}, {@link com.acromere.zerra.image.RenderedIcon} and {@link com.acromere.zerra.image.RenderedImage} for in-memory and rendered images.</li>
 *   <li><b>Vector support</b> - {@link com.acromere.zerra.image.VectorIcon}, {@link com.acromere.zerra.image.VectorImage} and {@link com.acromere.zerra.image.VectorImageWriter} for vector icons and writers.</li>
 *   <li><b>Proofs</b> - {@link com.acromere.zerra.image.Proof} as the abstract base that consumes a {@link com.acromere.zerra.image.VectorImage} and produces a {@link com.acromere.zerra.image.RenderedImage}.</li>
 *   <li><b>SVG support</b> - {@link com.acromere.zerra.image.SvgIcon} as a fluent builder over {@link com.acromere.zerra.image.VectorIcon} for SVG document generation.</li>
 *   <li><b>Image factories</b> - {@link com.acromere.zerra.image.Images} for locating and creating built-in icon resources.</li>
 * </ul>
 *
 * <p>Main classes:</p>
 * <ul>
 *   <li>{@link com.acromere.zerra.image.VectorIcon} - abstract base for icons that can render themselves.</li>
 *   <li>{@link com.acromere.zerra.image.RenderedIcon} - a vector icon rendered to a pixel image.</li>
 *   <li>{@link com.acromere.zerra.image.VectorImage} - describes an image source for a proof to consume.</li>
 *   <li>{@link com.acromere.zerra.image.Proof} - abstract base that consumes a vector image and produces a rendered image.</li>
 *   <li>{@link com.acromere.zerra.image.Images} - factory and registry for built-in icon resources.</li>
 *   <li>{@link com.acromere.zerra.image.BrokenIcon} - placeholder icon for a missing resource.</li>
 *   <li>{@link com.acromere.zerra.image.IconTag} - convenience helpers for tagging and proving vector icons.</li>
 * </ul>
 */
package com.acromere.zerra.image;
