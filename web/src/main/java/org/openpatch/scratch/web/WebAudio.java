package org.openpatch.scratch.web;

import org.openpatch.scratch.internal.platform.AudioDevice;
import org.teavm.jso.JSBody;
import org.teavm.jso.typedarrays.Int8Array;

/**
 * Web Audio (s4j.js): a buffer is an AudioBuffer, a source a gain node that plays it. Browsers
 * only start sound after the reader has clicked or pressed a key; s4j.js resumes it then.
 */
final class WebAudio implements AudioDevice {

  private static final int AL_PLAYING = 4114;

  private WebAudio() {}

  /** The page's AudioContext, or null in a browser without Web Audio. */
  static WebAudio open() {
    return openContext() ? new WebAudio() : null;
  }

  @Override
  public int createBuffer(byte[] samples, int channels, int bits, int sampleRate) {
    return createBuffer(Int8Array.copyFromJavaArray(samples), channels, bits, sampleRate);
  }

  @Override
  public int createSource() {
    return genSource();
  }

  @Override
  public void play(int source, int buffer, float gain, boolean loop) {
    setBuffer(source, buffer);
    setLooping(source, loop);
    nSetGain(source, gain);
    playSource(source);
  }

  @Override
  public boolean isPlaying(int source) {
    return state(source) == AL_PLAYING;
  }

  @Override
  public void deleteBuffer(int handle) {
    nDeleteBuffer(handle);
  }

  @Override
  public void deleteSource(int handle) {
    nDeleteSource(handle);
  }

  @Override
  public void setGain(int handle, float gain) {
    nSetGain(handle, gain);
  }

  @Override
  public void stop(int handle) {
    nStop(handle);
  }

  @Override
  public void close() {
    // the AudioContext belongs to the page
  }

  // ---- s4j.js -------------------------------------------------------------------------------

  @JSBody(script = "return S4J.al.open();")
  private static native boolean openContext();

  @JSBody(params = {"bytes", "channels", "bits", "rate"}, script = "return S4J.al.createBuffer(bytes, channels, bits, rate);")
  private static native int createBuffer(Int8Array bytes, int channels, int bits, int rate);

  @JSBody(params = "id", script = "S4J.al.deleteBuffer(id);")
  private static native void nDeleteBuffer(int id);

  @JSBody(script = "return S4J.al.genSource();")
  private static native int genSource();

  @JSBody(params = "id", script = "S4J.al.deleteSource(id);")
  private static native void nDeleteSource(int id);

  @JSBody(params = {"id", "buffer"}, script = "S4J.al.setBuffer(id, buffer);")
  private static native void setBuffer(int id, int buffer);

  @JSBody(params = {"id", "loop"}, script = "S4J.al.setLooping(id, loop);")
  private static native void setLooping(int id, boolean loop);

  @JSBody(params = {"id", "gain"}, script = "S4J.al.setGain(id, gain);")
  private static native void nSetGain(int id, float gain);

  @JSBody(params = "id", script = "S4J.al.play(id);")
  private static native void playSource(int id);

  @JSBody(params = "id", script = "S4J.al.stop(id);")
  private static native void nStop(int id);

  @JSBody(params = "id", script = "return S4J.al.state(id);")
  private static native int state(int id);
}
