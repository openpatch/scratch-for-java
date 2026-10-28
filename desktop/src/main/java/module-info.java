/**
 * Scratch for Java on the desktop: the core's window, OpenGL, sound and decoders through LWJGL.
 * Put it next to {@code org.openpatch.scratch}; the core finds it on its own.
 */
module org.openpatch.scratch.desktop {
  requires org.openpatch.scratch;
  requires org.lwjgl;
  requires org.lwjgl.glfw;
  requires org.lwjgl.opengl;
  requires org.lwjgl.openal;
  requires org.lwjgl.stb;
  requires org.lwjgl.tinyfd;

  provides org.openpatch.scratch.internal.platform.Platform with org.openpatch.scratch.desktop.DesktopPlatform;
}
