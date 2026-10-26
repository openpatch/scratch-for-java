/**
 * The public extension API: what an extension jar uses to add to Scratch for Java without
 * touching its internals. First-party extensions (tiled, save, recorder, drawing) are built on
 * it alone, which the build checks.
 *
 * <p>Desktop only: the online IDE has every first-party extension built in and loads no others.
 *
 * @desktop-only
 */
package org.openpatch.scratch.extension;
