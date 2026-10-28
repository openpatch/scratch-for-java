package org.openpatch.scratch.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.teavm.interop.Async;
import org.teavm.interop.AsyncCallback;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.Int8Array;
import org.teavm.runtime.fs.VirtualFile;
import org.teavm.runtime.fs.VirtualFileAccessor;
import org.teavm.runtime.fs.VirtualFileSystem;
import org.teavm.runtime.fs.VirtualFileSystemProvider;

/**
 * The game's files as java.io / java.nio.file see them. The files next to the page, listed in
 * files.txt (path, size), are read with fetch when first opened; the working directory is "/",
 * so Path.of("images/cat.png") is the page's images/cat.png. Files the game writes (save games,
 * File.save) live in the browser's localStorage, for this page only, and are there again the
 * next time it is opened. They are written through when closed, and override the page's own
 * file of the same name.
 */
final class WebFiles implements VirtualFileSystem {

  /** What the page brings: sizes by path, children by folder (from files.txt). */
  private final Map<String, Integer> sizes = new HashMap<>();
  private final Map<String, TreeSet<String>> children = new HashMap<>();
  private final Map<String, byte[]> fetched = new HashMap<>();
  /** What the game wrote: contents by path, and the folders it made. */
  private final Map<String, byte[]> written = new HashMap<>();
  private final Set<String> folders = new HashSet<>();

  private boolean loaded;

  private WebFiles() {}

  /**
   * Reads files.txt and the stored files on first use, not when the platform is made: the
   * platform is made where TeaVM cannot wait for a fetch (inside the ServiceLoader), file access
   * is not.
   */
  private synchronized void load() {
    if (this.loaded) {
      return;
    }
    this.loaded = true;
    String manifest = fetchText("files.txt");
    if (manifest == null) {
      manifest = "";
    }
    this.children.put("", new TreeSet<>());
    // "path\tsize" per line: no JSON parser needed
    for (String line : manifest.split("\n")) {
      int tab = line.indexOf('\t');
      if (tab < 0) {
        continue;
      }
      String path = line.substring(0, tab);
      this.sizes.put(path, Integer.parseInt(line.substring(tab + 1).trim()));
      String child = path;
      while (true) {
        int slash = child.lastIndexOf('/');
        String parent = slash < 0 ? "" : child.substring(0, slash);
        this.children.computeIfAbsent(parent, p -> new TreeSet<>()).add(child.substring(slash + 1));
        if (slash < 0) {
          break;
        }
        child = parent;
      }
    }
    String[] stored = storedPaths();
    for (String path : stored) {
      String value = storageGet(path);
      if (value != null) {
        this.written.put(path, Base64.getDecoder().decode(value));
        this.addFolders(path);
      }
    }
  }

  /** Makes java.io and java.nio.file use the page's folder. */
  static WebFiles install() {
    WebFiles files = new WebFiles();
    VirtualFileSystemProvider.setInstance(files);
    return files;
  }

  /** The files under a folder, relative to it, sorted. */
  List<String> list(String folder) {
    this.load();
    String prefix = key(folder) + "/";
    TreeSet<String> paths = new TreeSet<>(this.sizes.keySet());
    paths.addAll(this.written.keySet());
    List<String> out = new ArrayList<>();
    for (String path : paths) {
      if (path.startsWith(prefix)) {
        out.add(path.substring(prefix.length()));
      }
    }
    return out;
  }

  private static String key(String path) {
    String p = path.replace('\\', '/');
    while (p.startsWith("/")) {
      p = p.substring(1);
    }
    while (p.endsWith("/")) {
      p = p.substring(0, p.length() - 1);
    }
    return p;
  }

  private static String child(String folder, String name) {
    return folder.isEmpty() ? name : folder + "/" + name;
  }

  private void addFolders(String path) {
    for (int slash = path.lastIndexOf('/'); slash > 0; slash = path.lastIndexOf('/', slash - 1)) {
      this.folders.add(path.substring(0, slash));
    }
  }

  @Override
  public String getUserDir() {
    return "/";
  }

  @Override
  public VirtualFile getFile(String path) {
    this.load();
    return new File(key(path));
  }

  @Override
  public boolean isWindows() {
    return false;
  }

  @Override
  public String canonicalize(String path) {
    return path;
  }

  @Override
  public String[] getRoots() {
    return new String[] {"/"};
  }

  private byte[] read(String path) throws IOException {
    byte[] own = this.written.get(path);
    if (own != null) {
      return own;
    }
    byte[] bytes = this.fetched.get(path);
    if (bytes == null) {
      Int8Array response = fetch(path);
      if (response == null) {
        throw new IOException("Could not load " + path);
      }
      bytes = response.copyToJavaArray();
      this.fetched.put(path, bytes);
    }
    return bytes;
  }

  private void store(String path, byte[] bytes) {
    this.written.put(path, bytes);
    this.addFolders(path);
    storageSet(path, Base64.getEncoder().encodeToString(bytes));
  }

  private void remove(String path) {
    this.written.remove(path);
    storageRemove(path);
  }

  private final class File implements VirtualFile {
    private final String path;

    File(String path) {
      this.path = path;
    }

    @Override
    public String getName() {
      return this.path.substring(this.path.lastIndexOf('/') + 1);
    }

    @Override
    public boolean isDirectory() {
      return this.path.isEmpty() || WebFiles.this.children.containsKey(this.path)
          || WebFiles.this.folders.contains(this.path);
    }

    @Override
    public boolean isFile() {
      return WebFiles.this.written.containsKey(this.path) || WebFiles.this.sizes.containsKey(this.path);
    }

    @Override
    public String[] listFiles() {
      if (!this.isDirectory()) {
        return null;
      }
      TreeSet<String> names = new TreeSet<>();
      TreeSet<String> own = WebFiles.this.children.get(this.path);
      if (own != null) {
        names.addAll(own);
      }
      String prefix = this.path.isEmpty() ? "" : this.path + "/";
      List<String> written = new ArrayList<>(WebFiles.this.written.keySet());
      written.addAll(WebFiles.this.folders);
      for (String path : written) {
        if (path.startsWith(prefix) && path.length() > prefix.length()) {
          String rest = path.substring(prefix.length());
          int slash = rest.indexOf('/');
          names.add(slash < 0 ? rest : rest.substring(0, slash));
        }
      }
      return names.toArray(new String[0]);
    }

    @Override
    public VirtualFileAccessor createAccessor(boolean readable, boolean writable, boolean append) {
      if (!writable) {
        if (!this.isFile()) {
          return null;
        }
        try {
          return new Accessor(this.path, WebFiles.this.read(this.path), false);
        } catch (IOException e) {
          return null;
        }
      }
      byte[] start = new byte[0];
      if (append && this.isFile()) {
        try {
          start = WebFiles.this.read(this.path);
        } catch (IOException e) {
          return null;
        }
      }
      Accessor accessor = new Accessor(this.path, start, true);
      if (append) {
        accessor.position = start.length;
      }
      if (!this.isFile()) {
        WebFiles.this.store(this.path, start);
      }
      return accessor;
    }

    @Override
    public boolean createFile(String name) {
      String path = child(this.path, name);
      if (WebFiles.this.written.containsKey(path) || WebFiles.this.sizes.containsKey(path)) {
        return false;
      }
      WebFiles.this.store(path, new byte[0]);
      return true;
    }

    @Override
    public boolean createDirectory(String name) {
      String path = child(this.path, name);
      if (WebFiles.this.folders.contains(path) || WebFiles.this.children.containsKey(path)) {
        return false;
      }
      WebFiles.this.folders.add(path);
      WebFiles.this.addFolders(path);
      return true;
    }

    @Override
    public boolean delete() {
      if (WebFiles.this.written.containsKey(this.path)) {
        WebFiles.this.remove(this.path);
        return true;
      }
      if (WebFiles.this.folders.contains(this.path)) {
        String[] names = this.listFiles();
        if (names != null && names.length > 0) {
          return false;
        }
        WebFiles.this.folders.remove(this.path);
        return true;
      }
      return false; // the page's own files stay
    }

    @Override
    public boolean adopt(VirtualFile file, String name) {
      if (!(file instanceof File other) || !WebFiles.this.written.containsKey(other.path)) {
        return false;
      }
      byte[] bytes = WebFiles.this.written.get(other.path);
      WebFiles.this.remove(other.path);
      WebFiles.this.store(child(this.path, name), bytes);
      return true;
    }

    @Override
    public boolean canRead() {
      return this.isFile() || this.isDirectory();
    }

    @Override
    public boolean canWrite() {
      return true;
    }

    @Override
    public long lastModified() {
      return 0;
    }

    @Override
    public boolean setLastModified(long time) {
      return false;
    }

    @Override
    public boolean setReadOnly(boolean readOnly) {
      return false;
    }

    @Override
    public int length() {
      byte[] own = WebFiles.this.written.get(this.path);
      if (own != null) {
        return own.length;
      }
      Integer size = WebFiles.this.sizes.get(this.path);
      return size == null ? 0 : size;
    }
  }

  /** Reads a file's bytes; a writable one collects them and stores them when flushed or closed. */
  private final class Accessor implements VirtualFileAccessor {
    private final String path;
    private final boolean writable;
    private byte[] bytes;
    private int size;
    private int position;

    Accessor(String path, byte[] bytes, boolean writable) {
      this.path = path;
      this.writable = writable;
      this.bytes = bytes.clone();
      this.size = bytes.length;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
      int n = Math.min(length, this.size - this.position);
      if (n <= 0) {
        return -1;
      }
      System.arraycopy(this.bytes, this.position, buffer, offset, n);
      this.position += n;
      return n;
    }

    @Override
    public void write(byte[] buffer, int offset, int length) throws IOException {
      if (!this.writable) {
        throw new IOException(this.path + " is open for reading only");
      }
      this.ensure(this.position + length);
      System.arraycopy(buffer, offset, this.bytes, this.position, length);
      this.position += length;
      this.size = Math.max(this.size, this.position);
    }

    private void ensure(int capacity) {
      if (capacity > this.bytes.length) {
        this.bytes = java.util.Arrays.copyOf(this.bytes, Math.max(capacity, this.bytes.length * 2 + 64));
      }
    }

    @Override
    public int tell() {
      return this.position;
    }

    @Override
    public void seek(int target) {
      this.position = target;
    }

    @Override
    public void skip(int amount) {
      this.position += amount;
    }

    @Override
    public int size() {
      return this.size;
    }

    @Override
    public void resize(int size) throws IOException {
      if (!this.writable) {
        throw new IOException(this.path + " is open for reading only");
      }
      this.ensure(size);
      this.size = size;
      this.position = Math.min(this.position, size);
    }

    @Override
    public void close() {
      this.flush();
    }

    @Override
    public void flush() {
      if (this.writable) {
        WebFiles.this.store(this.path, java.util.Arrays.copyOf(this.bytes, this.size));
      }
    }
  }

  // ---- the page's files: fetch, waited for -------------------------------------------------------

  @JSFunctor
  interface BytesDone extends JSObject {
    void done(Int8Array bytes);
  }

  @JSFunctor
  interface TextDone extends JSObject {
    void done(String text);
  }

  @Async
  private static native Int8Array fetch(String path);

  private static void fetch(String path, AsyncCallback<Int8Array> callback) {
    fetchBytes(path, callback::complete);
  }

  @JSBody(params = {"path", "done"}, script = "S4J.files.fetch(path, done);")
  private static native void fetchBytes(String path, BytesDone done);

  @Async
  private static native String fetchText(String path);

  private static void fetchText(String path, AsyncCallback<String> callback) {
    fetchText(path, callback::complete);
  }

  @JSBody(params = {"path", "done"}, script = "S4J.files.fetchText(path, done);")
  private static native void fetchText(String path, TextDone done);

  // ---- the game's files: localStorage, under this page's address -----------------------------

  @JSBody(script = "return S4J.files.storedPaths();")
  private static native String[] storedPaths();

  @JSBody(params = "path", script = "return S4J.files.storageGet(path);")
  private static native String storageGet(String path);

  @JSBody(params = {"path", "value"}, script = "S4J.files.storageSet(path, value);")
  private static native void storageSet(String path, String value);

  @JSBody(params = "path", script = "S4J.files.storageRemove(path);")
  private static native void storageRemove(String path);
}
