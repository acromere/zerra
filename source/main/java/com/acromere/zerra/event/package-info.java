/**
 * JavaFX event integration for the Acromere event system.
 *
 * <p>This package bridges the JavaFX event model with the
 * {@link com.acromere.event.Event} event system, allowing JavaFX events to be
 * published through, registered to, and waited on like any other Acromere
 * event. It is primarily used in tests that need to observe and synchronize on
 * JavaFX events.</p>
 *
 * <p>Main classes:</p>
 * <ul>
 *   <li>{@link com.acromere.zerra.event.FxEventWrapper} - Wraps a
 *       {@link javafx.event.Event} as a {@link com.acromere.event.Event}</li>
 *   <li>{@link com.acromere.zerra.event.FxEventHub} - An
 *       {@link com.acromere.event.EventHub} that dispatches JavaFX events to
 *       registered handlers</li>
 *   <li>{@link com.acromere.zerra.event.FxEventWatcher} - Captures JavaFX
 *       events and waits for events of a given type</li>
 * </ul>
 */
package com.acromere.zerra.event;
