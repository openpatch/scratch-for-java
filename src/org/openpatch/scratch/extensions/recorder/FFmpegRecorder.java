package org.openpatch.scratch.extensions.recorder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import org.openpatch.scratch.internal.Applet;

/**
 * The FFmpegRecorder class extends the Recorder class to provide functionality for recording
 * frames and converting them into a video file using FFmpeg.
 * 
 * <p>This class creates a temporary directory to store PNG files for each frame and uses FFmpeg
 * to convert these frames into a video file when the recording is stopped.</p>
 * 
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * FFmpegRecorder recorder = new FFmpegRecorder("/path/to/output/video.mp4");
 * recorder.saveFrame();
 * recorder.stop();
 * }
 * </pre>
 * 
 * <p>Note: Ensure that FFmpeg is installed and available in the system's PATH.</p>
 */
public class FFmpegRecorder extends Recorder {
  private String tmpDir;
  private String pattern;

  /**
   * Constructs an FFmpegRecorder object with the specified path.
   *
   * @param path the path where the recording will be saved
   * 
   * This constructor initializes the FFmpegRecorder by creating a temporary directory
   * and setting up a pattern for temporary PNG files. If the temporary directory cannot
   * be created, an error message is printed to the console.
   */
  public FFmpegRecorder(String path) {
    super(path, ".mp4");
    try {
      this.tmpDir = Files.createTempDirectory("tmpDirPrefix").toFile().getAbsolutePath();
      this.pattern = Paths.get(this.tmpDir, "#######.png").toString();
    } catch (IOException e) {
      System.out.println("Can not create temporary folder.");
    }
  }

  /**
   * Saves the current frame using the specified pattern.
   */
  @Override
  public void saveFrame() {
    Applet.getInstance().saveFrame(pattern);
  }

  /**
   * Stops the recording process and converts the recorded frames into a video file using FFmpeg.
   * 
   * This method overrides the `stop` method from the superclass. It first calls the superclass's
   * `stop` method, then checks if the temporary directory (`tmpDir`) is not null. If it is not null,
   * it constructs a command to run FFmpeg, which converts the recorded frames (stored as PNG files)
   * into a video file with the specified frame rate and codec.
   * 
   * The FFmpeg command is executed, and a message is printed to the console indicating that the
   * video conversion is in progress. If there is an error during the execution of the command,
   * the error message is printed to the console.
   */
  @Override
  public void stop() {
    super.stop();
    if (tmpDir != null) {
      String[] cmd = {
        "ffmpeg",
        "-v",
        "warning",
        "-pattern_type",
        "glob",
        "-framerate",
        Float.toString(Applet.getInstance().frameRate),
        "-i",
        this.tmpDir,
        "/*.png",
        "-vcodec",
        "libx264",
        "-y",
        Paths.get("", path).toAbsolutePath().toString()
      };
      System.out.println("Converting video! Please wait.");
      String error = execCmd(cmd);
      if (error != null) {
        System.out.println(error);
      }
    }
  }

  private static String execCmd(String[] cmd) {
    String result = null;
    try (InputStream inputStream = Runtime.getRuntime().exec(cmd).getErrorStream();
        Scanner s = new Scanner(inputStream).useDelimiter("\\A")) {
      result = s.hasNext() ? s.next() : null;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }
}
