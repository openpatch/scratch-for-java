/**
 * Scratch for Java. Learners use {@code org.openpatch.scratch}; extensions also use
 * {@code org.openpatch.scratch.extension}. Everything under {@code org.openpatch.scratch.internal}
 * may change without notice and is not exported.
 */
module org.openpatch.scratch {
  requires org.lwjgl;
  requires org.lwjgl.glfw;
  requires org.lwjgl.opengl;
  requires org.lwjgl.openal;
  requires org.lwjgl.stb;
  requires org.lwjgl.tinyfd;

  exports org.openpatch.scratch;
  exports org.openpatch.scratch.extension;

  uses org.openpatch.scratch.extension.Extension;
}
