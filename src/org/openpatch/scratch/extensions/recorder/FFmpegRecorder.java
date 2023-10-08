package org.openpatch.scratch.extensions.recorder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import org.openpatch.scratch.internal.Applet;

public class FFmpegRecorder extends Recorder {
  private String tmpDir;
  private String pattern;

  public FFmpegRecorder(String path) {
    super(path, ".mp4");
    try {
      this.tmpDir = Files.createTempDirectory("tmpDirPrefix").toFile().getAbsolutePath();
      this.pattern = Paths.get(this.tmpDir, "#######.png").toString();
    } catch (IOException e) {
      System.out.println("Can not create temporary folder.");
    }
  }

  @Override
  public void saveFrame() {
    Applet.getInstance().saveFrame(pattern);
  }

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
