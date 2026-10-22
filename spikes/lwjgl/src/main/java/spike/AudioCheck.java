package spike;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.AL11.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.SOFTReopenDevice;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryUtil;

/**
 * P1-08: can OpenAL Soft through LWJGL do what the library needs from sound?
 * Every sound decodes once into an AL buffer and plays on a pool of sources,
 * the same binding the library will use.
 */
public class AudioCheck {

  private static final int POOL = 32;

  private long device;
  private long context;
  private ALCCapabilities alc;
  private final int[] sources = new int[POOL];
  private final long[] started = new long[POOL]; // when each source last started
  private long playNanos;
  private int wavBuffer;
  private int oggBuffer;

  public static void main(String[] args) {
    // as in SpikeWindow: the default memory backend warns in red on Java 24+,
    // the FFM one does not, and it must be set before LWJGL first touches memory
    if (Runtime.version().feature() >= 25 && System.getProperty("org.lwjgl.system.memoryBackend") == null) {
      System.setProperty("org.lwjgl.system.memoryBackend", "org.lwjgl.system.MemoryBackendFFM");
    }
    new AudioCheck().run();
  }

  private void run() {
    this.device = alcOpenDevice((ByteBuffer) null);
    if (this.device == 0) {
      // the one and only reaction to a machine without audio: stay silent, never throw
      System.out.println("audio: no audio device, sounds stay silent");
      return;
    }
    this.alc = ALC.createCapabilities(this.device);
    this.context = alcCreateContext(this.device, (IntBuffer) null);
    alcMakeContextCurrent(this.context);
    AL.createCapabilities(this.alc);
    System.out.printf(Locale.ROOT, "audio: device %s, %s, %s %s, %d Hz, reopen %b, disconnect %b%n",
        alcGetString(this.device, ALC_DEVICE_SPECIFIER), alGetString(AL_VERSION), alGetString(AL_VENDOR),
        alGetString(AL_RENDERER), alcGetInteger(this.device, ALC_FREQUENCY), this.alc.ALC_SOFT_reopen_device,
        this.alc.ALC_EXT_disconnect);
    try {
      this.wavBuffer = this.decodeWav(
          System.getProperty("audio.wav", "../../src/examples/java/reference/assets/bump.wav"));
      this.oggBuffer = this.decodeOgg(
          System.getProperty("audio.ogg", "../../core/src/main/resources/sounds/impact/impactMining_002.ogg"));
      this.createPool();
      this.measureStartLatency();
      this.measureOverlap();
      this.measureGain();
      this.watchOutputSwitch();
    } catch (Throwable t) {
      t.printStackTrace(); // a failing sound must not take the library down
    } finally {
      this.cleanup();
    }
  }

  // ---- decode once, into AL buffers ----------------------------------------

  /** RIFF/WAVE by hand: PCM only, the one variant Scratch projects ship. */
  private int decodeWav(String path) throws IOException {
    long t0 = System.nanoTime();
    ByteBuffer file = ByteBuffer.wrap(Files.readAllBytes(Path.of(path))).order(ByteOrder.LITTLE_ENDIAN);
    // "RIFF", the size of the rest, "WAVE"
    if (file.remaining() < 12 || file.getInt(0) != 0x46464952 /* "RIFF" */ || file.getInt(8) != 0x45564157 /* "WAVE" */) {
      throw new IOException(path + " is not a RIFF/WAVE file");
    }
    file.position(12);
    int formatTag = 0, channels = 0, rate = 0, bits = 0;
    byte[] samples = null;
    while (file.remaining() >= 8) {
      int id = file.getInt();
      int size = file.getInt();
      // chunks are word aligned: an odd body carries a pad byte before the next header
      int next = (int) Math.min((long) file.position() + size + (size & 1), file.limit());
      if (id == 0x20746D66 /* "fmt " */ && size >= 16) {
        formatTag = file.getShort() & 0xffff;
        channels = file.getShort() & 0xffff;
        rate = file.getInt();
        file.getInt(); // byte rate, follows from the rest
        file.getShort(); // block align
        bits = file.getShort() & 0xffff;
      } else if (id == 0x61746164 /* "data" */) {
        samples = new byte[Math.min(size, file.remaining())];
        file.get(samples);
      }
      file.position(next); // skips unknown chunks
    }
    if (formatTag != 1 /* PCM */ || (bits != 8 && bits != 16) || (channels != 1 && channels != 2) || samples == null) {
      throw new IOException(path + " is not an 8- or 16-bit PCM mono/stereo WAV");
    }
    int format = channels == 1 ? (bits == 8 ? AL_FORMAT_MONO8 : AL_FORMAT_MONO16)
        : (bits == 8 ? AL_FORMAT_STEREO8 : AL_FORMAT_STEREO16);
    ByteBuffer direct = MemoryUtil.memAlloc(samples.length).put(samples).flip();
    int buffer = alGenBuffers();
    if (bits == 8) {
      // 8-bit is unsigned in both WAV and OpenAL, 16-bit signed LE likewise: copy as is
      alBufferData(buffer, format, direct, rate);
    } else {
      alBufferData(buffer, format, direct.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer(), rate);
    }
    MemoryUtil.memFree(direct); // alBufferData copies, the upload buffer is ours
    double seconds = samples.length / (channels * rate * (bits / 8.0));
    System.out.printf(Locale.ROOT, "audio: wav %s, %d Hz, %d-bit %s, %.2f s, decoded in %d ms%n", path, rate, bits,
        channels == 1 ? "mono" : "stereo", seconds, (System.nanoTime() - t0) / 1_000_000);
    return buffer;
  }

  private int decodeOgg(String path) throws IOException {
    long t0 = System.nanoTime();
    byte[] raw = Files.readAllBytes(Path.of(path));
    ByteBuffer file = MemoryUtil.memAlloc(raw.length).put(raw).flip();
    IntBuffer channels = BufferUtils.createIntBuffer(1);
    IntBuffer rate = BufferUtils.createIntBuffer(1);
    ShortBuffer pcm = STBVorbis.stb_vorbis_decode_memory(file, channels, rate);
    MemoryUtil.memFree(file);
    if (pcm == null) {
      throw new IOException("stb_vorbis could not decode " + path);
    }
    int buffer = alGenBuffers();
    alBufferData(buffer, channels.get(0) == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, rate.get(0));
    double seconds = pcm.remaining() / (channels.get(0) * (double) rate.get(0));
    MemoryUtil.memFree(pcm); // alBufferData copies, the decode buffer is ours
    System.out.printf(Locale.ROOT, "audio: ogg %s, %d Hz, 16-bit %s, %.2f s, decoded in %d ms%n", path, rate.get(0),
        channels.get(0) == 1 ? "mono" : "stereo", seconds, (System.nanoTime() - t0) / 1_000_000);
    return buffer;
  }

  // ---- the source pool ------------------------------------------------------

  private void createPool() {
    for (int i = 0; i < POOL; i++) {
      this.sources[i] = alGenSources();
    }
  }

  /** A source that has stopped, or the one that started longest ago: reuse instead of running out. */
  private int play(int buffer, float gain) {
    int pick = 0;
    for (int i = 0; i < POOL; i++) {
      if (this.sourceState(this.sources[i]) != AL_PLAYING) {
        pick = i; // free is always better than stealing one
        break;
      }
      if (this.started[i] < this.started[pick]) {
        pick = i;
      }
    }
    int source = this.sources[pick];
    alSourcei(source, AL_BUFFER, buffer);
    alSourcef(source, AL_GAIN, gain);
    this.playNanos = System.nanoTime(); // what the start latency below measures from
    alSourcePlay(source);
    this.started[pick] = this.playNanos;
    return source;
  }

  private int sourceState(int source) {
    int[] state = new int[1];
    alGetSourcei(source, AL_SOURCE_STATE, state);
    return state[0];
  }

  private int countPlaying() {
    int playing = 0;
    for (int source : this.sources) {
      if (source != 0 && this.sourceState(source) == AL_PLAYING) {
        playing++;
      }
    }
    return playing;
  }

  private void awaitSilence(long timeoutMs) throws InterruptedException {
    long deadline = System.currentTimeMillis() + timeoutMs;
    while (this.countPlaying() > 0 && System.currentTimeMillis() < deadline) {
      Thread.sleep(20);
    }
  }

  // ---- the measurements ------------------------------------------------------

  private void measureStartLatency() {
    int source = this.play(this.oggBuffer, 1f);
    float[] offset = new float[1];
    long deadline = System.nanoTime() + 1_000_000_000L;
    while (System.nanoTime() < deadline) {
      alGetSourcef(source, AL_SEC_OFFSET, offset);
      if (this.sourceState(source) == AL_PLAYING && offset[0] > 0) {
        break;
      }
      Thread.onSpinWait();
    }
    // until the mixer picked the source up, not until the sound left the speakers
    System.out.printf(Locale.ROOT, "audio: start latency %.1f ms%n", (System.nanoTime() - this.playNanos) / 1e6);
  }

  private void measureOverlap() throws InterruptedException {
    int atOnce = 0;
    for (int i = 0; i < 24; i++) {
      this.play(this.wavBuffer, 1f);
      Thread.sleep(15);
      atOnce = Math.max(atOnce, this.countPlaying());
    }
    System.out.printf(Locale.ROOT, "audio: %d of %d sources playing at once (expected more than 1)%n", atOnce, POOL);
    this.awaitSilence(5_000);
  }

  private void measureGain() throws InterruptedException {
    this.play(this.oggBuffer, 0.2f);
    Thread.sleep(600);
    this.play(this.oggBuffer, 1f);
    this.awaitSilence(5_000);
    System.out.println("audio: gain 0.2 and 1.0 played together, all stopped");
  }

  /** Opt-in (-Daudio.switchSeconds=N): does a default-output switch keep the loop running? */
  private void watchOutputSwitch() throws InterruptedException {
    int seconds = Integer.getInteger("audio.switchSeconds", 0);
    if (seconds <= 0) {
      return;
    }
    String name = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
    int source = this.play(this.oggBuffer, 1f);
    alSourcei(source, AL_LOOPING, AL_TRUE);
    int connected = this.alc.ALC_EXT_disconnect ? alcGetInteger(this.device, 0x313 /* ALC_CONNECTED */) : 1;
    long deadline = System.currentTimeMillis() + seconds * 1000L;
    while (System.currentTimeMillis() < deadline) {
      Thread.sleep(250);
      if (this.alc.ALC_EXT_disconnect) {
        int now = alcGetInteger(this.device, 0x313 /* ALC_CONNECTED */);
        if (now != connected) {
          connected = now;
          System.out.printf(Locale.ROOT, "audio: ALC_CONNECTED is now %d%n", now);
        }
      }
      if (this.alc.ALC_SOFT_reopen_device) {
        String current = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        if (!current.equals(name)) {
          name = current;
          boolean reopened = SOFTReopenDevice.alcReopenDeviceSOFT(this.device, (ByteBuffer) null, (IntBuffer) null);
          System.out.printf(Locale.ROOT, "audio: default output changed to %s, reopened: %b, still playing: %b%n",
              current, reopened, this.sourceState(source) == AL_PLAYING);
        }
      }
    }
    alSourceStop(source);
    alSourcei(source, AL_LOOPING, AL_FALSE);
  }

  // ---- teardown --------------------------------------------------------------

  private void cleanup() {
    for (int source : this.sources) {
      if (source != 0) {
        alSourceStop(source);
        alDeleteSources(source);
      }
    }
    if (this.wavBuffer != 0) {
      alDeleteBuffers(this.wavBuffer);
    }
    if (this.oggBuffer != 0) {
      alDeleteBuffers(this.oggBuffer);
    }
    alcMakeContextCurrent(0L);
    if (this.context != 0) {
      alcDestroyContext(this.context);
    }
    if (this.device != 0) {
      alcCloseDevice(this.device);
    }
    System.out.println("audio: done");
  }
}
