package org.openpatch.scratch.internal.draw;

import java.util.Map;

/**
 * A student shader (fragment body in the 7.0 format) and the values of its uniforms at the end
 * of the batch. {@code time} and {@code resolution} are filled in by the renderer.
 *
 * @param name the shader's name, for compile errors
 * @param source the body the student wrote; the renderer prepends the header
 * @param uniforms name → {@code float[]} (1–4 values, or an array of them with {@code arrayStride}
 *     values each) or {@code int[]}, copied at the end of the batch
 */
public record ShaderDraw(String name, String source, Map<String, Object> uniforms) {}
