package org.openpatch.scratch.extensions.tiled;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * The {@code <data>} of a tile layer in every encoding Tiled writes: CSV, or
 * base64 (plain, zlib or gzip). Tiled's default for new maps is base64 with
 * zlib, so maps made in Tiled load without changing its settings.
 */
class LayerData {

  @JacksonXmlProperty(isAttribute = true)
  public String encoding;

  @JacksonXmlProperty(isAttribute = true)
  public String compression;

  @JacksonXmlText
  public String text;

  /** An infinite map stores its tiles in chunks, which are not supported. */
  @JacksonXmlProperty(localName = "chunk")
  public Object chunk;

  /** The raw tile ids, flip bits included (unsigned 32 bit). */
  long[] gids() {
    if (chunk != null) {
      throw new IllegalStateException(
          "This map is infinite (Map > Map Properties > Infinite in Tiled). Scratch for Java "
              + "loads fixed-size maps only: untick Infinite and save the map again.");
    }
    String content = text == null ? "" : text.trim();
    if (encoding == null || encoding.equals("csv")) {
      if (encoding == null && !content.isEmpty() && !content.matches("[0-9,\\s]+")) {
        throw new IllegalStateException("The map stores tiles as XML elements, which are not "
            + "supported: choose CSV or Base64 as Tile Layer Format in Tiled.");
      }
      return Arrays.stream(content.split("[,\\s]+"))
          .filter(s -> !s.isEmpty())
          .mapToLong(Long::parseLong)
          .toArray();
    }
    if (!encoding.equals("base64")) {
      throw new IllegalStateException("Unknown tile layer encoding '" + encoding + "'.");
    }
    byte[] bytes = Base64.getMimeDecoder().decode(content);
    try {
      if ("zlib".equals(compression)) {
        bytes = readAll(new InflaterInputStream(new ByteArrayInputStream(bytes)));
      } else if ("gzip".equals(compression)) {
        bytes = readAll(new GZIPInputStream(new ByteArrayInputStream(bytes)));
      } else if (compression != null && !compression.isEmpty()) {
        throw new IllegalStateException("Tile layer compression '" + compression
            + "' is not supported: choose zlib, gzip or no compression in Tiled.");
      }
    } catch (IOException e) {
      throw new IllegalStateException("The tile layer data is damaged: " + e.getMessage(), e);
    }
    ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
    long[] gids = new long[bytes.length / 4];
    for (int i = 0; i < gids.length; i++) {
      gids[i] = Integer.toUnsignedLong(buffer.getInt());
    }
    return gids;
  }

  private static byte[] readAll(InputStream in) throws IOException {
    try (in) {
      return in.readAllBytes();
    }
  }
}
