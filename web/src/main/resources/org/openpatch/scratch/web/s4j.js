// What Scratch for Java's browser platform (org.openpatch.scratch.web) calls: WebGL2 for the
// renderer's GL, the page's canvas and DOM events for the window, Web Audio for sound, the
// browser's decoders for images and Ogg files, fetch for the game's files. Integer handles stand
// for GL objects and sounds, as they do in OpenGL and OpenAL.
"use strict";
const S4J = (() => {
  const canvas = document.getElementById("stage");
  let gl = null;
  const objects = [null];
  const put = (o) => { objects.push(o); o.__id = objects.length - 1; return o.__id; };
  const get = (id) => objects[id] || null;
  const drop = (id) => { objects[id] = null; };

  // ---- GL ----------------------------------------------------------------------------------
  const GL_BGRA = 32993, GL_UNSIGNED_INT_8_8_8_8_REV = 33639;
  const GL_FRAMEBUFFER_BINDING = 36006;

  /** Little-endian ARGB ints (GL_BGRA / 8_8_8_8_REV on the desktop) to RGBA bytes. */
  function argbToRgba(src) {
    const out = new Uint8Array(src.length * 4);
    for (let i = 0, j = 0; i < src.length; i++, j += 4) {
      const c = src[i];
      out[j] = (c >> 16) & 255; out[j + 1] = (c >> 8) & 255; out[j + 2] = c & 255; out[j + 3] = c >>> 24;
    }
    return out;
  }

  function pixels(format, type, data) {
    if (data instanceof Int8Array) data = new Uint8Array(data.buffer, data.byteOffset, data.length); // Java bytes
    if (format === GL_BGRA && type === GL_UNSIGNED_INT_8_8_8_8_REV) {
      return [gl.RGBA, gl.UNSIGNED_BYTE, data ? argbToRgba(data) : null];
    }
    return [format, type, data];
  }

  /** Desktop GLSL 330 to GLSL ES 300; the renderer's shaders need nothing else so far. */
  function translate(source) {
    return source.replace(/#version\s+330(\s+core)?/, "#version 300 es\nprecision highp float;\nprecision highp int;");
  }

  const glApi = {
    genTexture: () => put(gl.createTexture()),
    deleteTexture: (id) => { gl.deleteTexture(get(id)); drop(id); },
    bindTexture: (t, id) => gl.bindTexture(t, get(id)),
    activeTexture: (u) => gl.activeTexture(u),
    pixelStorei: (p, v) => gl.pixelStorei(p, v),
    texImage2D: (t, level, internal, w, h, border, format, type, data) => {
      const [f, ty, d] = pixels(format, type, data);
      gl.texImage2D(t, level, internal, w, h, border, f, ty, d);
    },
    texSubImage2D: (t, level, x, y, w, h, format, type, data) => {
      const [f, ty, d] = pixels(format, type, data);
      gl.texSubImage2D(t, level, x, y, w, h, f, ty, d);
    },
    texParameteri: (t, p, v) => gl.texParameteri(t, p, v),
    generateMipmap: (t) => gl.generateMipmap(t),
    genFramebuffer: () => put(gl.createFramebuffer()),
    deleteFramebuffer: (id) => { gl.deleteFramebuffer(get(id)); drop(id); },
    bindFramebuffer: (t, id) => gl.bindFramebuffer(t, get(id)),
    framebufferTexture2D: (t, a, tt, id, level) => gl.framebufferTexture2D(t, a, tt, get(id), level),
    checkFramebufferStatus: (t) => gl.checkFramebufferStatus(t),
    genRenderbuffer: () => put(gl.createRenderbuffer()),
    deleteRenderbuffer: (id) => { gl.deleteRenderbuffer(get(id)); drop(id); },
    bindRenderbuffer: (t, id) => gl.bindRenderbuffer(t, get(id)),
    renderbufferStorageMultisample: (t, s, f, w, h) =>
      gl.renderbufferStorageMultisample(t, Math.min(s, gl.getParameter(gl.MAX_SAMPLES)), f, w, h),
    framebufferRenderbuffer: (t, a, rt, id) => gl.framebufferRenderbuffer(t, a, rt, get(id)),
    blitFramebuffer: (a, b, c, d, e, f, g, h, mask, filter) => gl.blitFramebuffer(a, b, c, d, e, f, g, h, mask, filter),
    viewport: (x, y, w, h) => gl.viewport(x, y, w, h),
    scissor: (x, y, w, h) => gl.scissor(x, y, w, h),
    readPixels: (x, y, w, h) => {
      const rgba = new Uint8Array(w * h * 4);
      gl.readPixels(x, y, w, h, gl.RGBA, gl.UNSIGNED_BYTE, rgba);
      const out = new Int32Array(w * h);
      for (let i = 0, j = 0; i < out.length; i++, j += 4) {
        out[i] = rgba[j + 3] << 24 | rgba[j] << 16 | rgba[j + 1] << 8 | rgba[j + 2];
      }
      return out;
    },
    genVertexArray: () => put(gl.createVertexArray()),
    bindVertexArray: (id) => gl.bindVertexArray(get(id)),
    genBuffer: () => put(gl.createBuffer()),
    bindBuffer: (t, id) => gl.bindBuffer(t, get(id)),
    bufferData: (t, data, usage) => gl.bufferData(t, data, usage),
    vertexAttribPointer: (i, size, type, norm, stride, offset) => gl.vertexAttribPointer(i, size, type, norm, stride, offset),
    enableVertexAttribArray: (i) => gl.enableVertexAttribArray(i),
    drawArrays: (mode, first, count) => gl.drawArrays(mode, first, count),
    clearColor: (r, g, b, a) => gl.clearColor(r, g, b, a),
    clear: (mask) => gl.clear(mask),
    enable: (c) => gl.enable(c),
    disable: (c) => gl.disable(c),
    blendFunc: (s, d) => gl.blendFunc(s, d),
    createShader: (type) => put(gl.createShader(type)),
    shaderSource: (id, src) => gl.shaderSource(get(id), translate(src)),
    compileShader: (id) => gl.compileShader(get(id)),
    getShaderi: (id, p) => +gl.getShaderParameter(get(id), p),
    getShaderInfoLog: (id) => gl.getShaderInfoLog(get(id)) || "",
    deleteShader: (id) => { gl.deleteShader(get(id)); drop(id); },
    createProgram: () => put(gl.createProgram()),
    attachShader: (p, s) => gl.attachShader(get(p), get(s)),
    linkProgram: (p) => gl.linkProgram(get(p)),
    getProgrami: (p, pname) => +gl.getProgramParameter(get(p), pname),
    getProgramInfoLog: (p) => gl.getProgramInfoLog(get(p)) || "",
    deleteProgram: (p) => { gl.deleteProgram(get(p)); drop(p); },
    useProgram: (p) => gl.useProgram(get(p)),
    getUniformLocation: (p, name) => {
      const loc = gl.getUniformLocation(get(p), name);
      return loc ? put(loc) : -1;
    },
    uniform1f: (l, x) => gl.uniform1f(get(l), x),
    uniform2f: (l, x, y) => gl.uniform2f(get(l), x, y),
    uniform3f: (l, x, y, z) => gl.uniform3f(get(l), x, y, z),
    uniform4f: (l, x, y, z, w) => gl.uniform4f(get(l), x, y, z, w),
    uniform1i: (l, x) => gl.uniform1i(get(l), x),
    uniform1fv: (l, v) => gl.uniform1fv(get(l), v),
    uniform2fv: (l, v) => gl.uniform2fv(get(l), v),
    uniform3fv: (l, v) => gl.uniform3fv(get(l), v),
    uniform4fv: (l, v) => gl.uniform4fv(get(l), v),
    uniformMatrix3fv: (l, t, v) => gl.uniformMatrix3fv(get(l), t, v),
    getInteger: (p) => {
      const v = gl.getParameter(p);
      return p === GL_FRAMEBUFFER_BINDING ? (v ? v.__id : 0) : v;
    },
    getIntegerv: (p) => new Int32Array(gl.getParameter(p)),
    getString: (p) => String(gl.getParameter(p)),
  };

  // ---- window and input (GLFW) --------------------------------------------------------------
  const events = [];
  const keyNames = {};
  let shouldClose = false;
  let focused = true;

  // KeyboardEvent.code (the key's place, as GLFW keys are) to GLFW key codes
  const codes = { Space: 32, Quote: 39, Comma: 44, Minus: 45, Period: 46, Slash: 47, Semicolon: 59,
    Equal: 61, BracketLeft: 91, Backslash: 92, BracketRight: 93, Backquote: 96, Escape: 256, Enter: 257,
    Tab: 258, Backspace: 259, Insert: 260, Delete: 261, ArrowRight: 262, ArrowLeft: 263, ArrowDown: 264,
    ArrowUp: 265, PageUp: 266, PageDown: 267, Home: 268, End: 269, CapsLock: 280, ScrollLock: 281,
    NumLock: 282, PrintScreen: 283, Pause: 284, NumpadDecimal: 330, NumpadDivide: 331,
    NumpadMultiply: 332, NumpadSubtract: 333, NumpadAdd: 334, NumpadEnter: 335, ShiftLeft: 340,
    ControlLeft: 341, AltLeft: 342, MetaLeft: 343, ShiftRight: 344, ControlRight: 345, AltRight: 346,
    MetaRight: 347, ContextMenu: 348 };
  for (let i = 0; i < 26; i++) codes["Key" + String.fromCharCode(65 + i)] = 65 + i;
  for (let i = 0; i < 10; i++) { codes["Digit" + i] = 48 + i; codes["Numpad" + i] = 320 + i; }
  for (let i = 1; i <= 25; i++) codes["F" + i] = 289 + i;

  const mods = (e) => (e.shiftKey ? 1 : 0) | (e.ctrlKey ? 2 : 0) | (e.altKey ? 4 : 0) | (e.metaKey ? 8 : 0);
  // keys the page must not act on itself (scrolling, find-as-you-type)
  const keepFromPage = new Set(["Space", "ArrowLeft", "ArrowRight", "ArrowUp", "ArrowDown", "Tab",
    "Backspace", "Slash", "Quote", "PageUp", "PageDown", "Home", "End", "F1", "F3", "F5", "F7"]);

  function key(e, down) {
    const k = codes[e.code];
    if (k === undefined) return;
    if (e.key && e.key.length === 1) keyNames[k] = e.key.toLowerCase();
    if (keepFromPage.has(e.code)) e.preventDefault();
    events.push([1, k, k, down ? (e.repeat ? 2 : 1) : 0, mods(e), 0]);
    if (down && e.key && [...e.key].length === 1 && !e.ctrlKey && !e.metaKey) {
      events.push([2, e.key.codePointAt(0), 0, 0, 0, 0]);
    }
  }
  window.addEventListener("keydown", (e) => { unlockAudio(); key(e, true); });
  window.addEventListener("keyup", (e) => key(e, false));
  window.addEventListener("focus", () => { focused = true; });
  window.addEventListener("blur", () => { focused = false; });
  const local = (e) => { const r = canvas.getBoundingClientRect(); return [e.clientX - r.left, e.clientY - r.top]; };
  canvas.addEventListener("pointermove", (e) => { const [x, y] = local(e); events.push([3, x, y, 0, 0, 0]); });
  const button = (b) => (b === 1 ? 2 : b === 2 ? 1 : b);
  canvas.addEventListener("pointerdown", (e) => {
    unlockAudio();
    canvas.setPointerCapture(e.pointerId);
    const [x, y] = local(e);
    events.push([3, x, y, 0, 0, 0], [4, button(e.button), 1, mods(e), 0, 0]);
  });
  canvas.addEventListener("pointerup", (e) => events.push([4, button(e.button), 0, mods(e), 0, 0]));
  canvas.addEventListener("contextmenu", (e) => e.preventDefault());
  canvas.addEventListener("wheel", (e) => {
    e.preventDefault();
    const steps = e.deltaMode === 0 ? e.deltaY / 100 : e.deltaMode === 1 ? e.deltaY / 3 : e.deltaY;
    events.push([5, -e.deltaX / 100, -steps, 0, 0, 0]);
  }, { passive: false });

  /** Framebuffer at device pixels for the canvas's CSS size: GLFW_SCALE_TO_MONITOR. */
  function fit() {
    const dpr = window.devicePixelRatio || 1;
    const w = Math.max(1, Math.round(canvas.clientWidth * dpr)), h = Math.max(1, Math.round(canvas.clientHeight * dpr));
    if (canvas.width !== w || canvas.height !== h) { canvas.width = w; canvas.height = h; }
  }

  let current = null;
  const stats = { frames: 0, busy: 0, max: 0, resumed: 0 };
  const glfw = {
    createWindow: (title) => {
      document.title = title;
      gl = canvas.getContext("webgl2", { alpha: false, antialias: false, premultipliedAlpha: true,
        preserveDrawingBuffer: false, powerPreference: "high-performance" });
      if (!gl) return 0;
      fit();
      return 1;
    },
    pollEvents: () => { fit(); },
    nextEvent: () => { current = events.shift() || null; return current ? current[0] : 0; },
    event: (i) => current[i],
    keyName: (k) => keyNames[k] || null,
    windowWidth: () => canvas.clientWidth,
    windowHeight: () => canvas.clientHeight,
    framebufferWidth: () => canvas.width,
    framebufferHeight: () => canvas.height,
    contentScale: () => window.devicePixelRatio || 1,
    screenWidth: () => screen.width,
    screenHeight: () => screen.height,
    focused: () => focused,
    shouldClose: () => shouldClose,
    setShouldClose: (v) => { shouldClose = v; },
    fullScreen: (on) => {
      // only a user gesture may ask for it; the page's own button is the sure way
      if (on && !document.fullscreenElement) document.documentElement.requestFullscreen().catch(() => {});
      if (!on && document.fullscreenElement) document.exitFullscreen().catch(() => {});
      return true;
    },
    /** Resolves on the next animation frame: the swap waits for the screen, as vsync does. */
    frame: (done) => {
      // time from the last resume to this swap: what one frame cost on the page's thread
      const now = performance.now();
      if (stats.resumed) { const t = now - stats.resumed; stats.frames++; stats.busy += t; stats.max = Math.max(stats.max, t); }
      requestAnimationFrame(() => { stats.resumed = performance.now(); done(); });
    },
    /** A mouse pointer from ARGB pixels, as a CSS cursor; 0 when the browser cannot make one. */
    createCursor: (w, h, argb, hotX, hotY) => {
      const c = document.createElement("canvas");
      c.width = w; c.height = h;
      const g = c.getContext("2d");
      const img = g.createImageData(w, h);
      img.data.set(argbToRgba(argb));
      g.putImageData(img, 0, 0);
      return put({ css: `url(${c.toDataURL()}) ${hotX} ${hotY}, auto` });
    },
    setCursor: (id) => { canvas.style.cursor = id ? get(id).css : ""; },
    destroyCursor: drop,
    ended: (status) => { if (status !== 0) console.error("Scratch for Java: the game ended with " + status); },
    message: (title, text) => {
      const box = document.getElementById("message");
      box.textContent = text;
      box.hidden = false;
      console.error(title + ": " + text);
    },
  };

  // ---- sound (OpenAL) -----------------------------------------------------------------------
  let audio = null;
  const AL_PLAYING = 4114, AL_STOPPED = 4116;
  function unlockAudio() { if (audio && audio.state === "suspended") audio.resume(); }
  const al = {
    open: () => { try { audio = audio || new AudioContext(); return true; } catch (e) { return false; } },
    /** Interleaved PCM (bytes) to an AudioBuffer: 8-bit unsigned or 16-bit signed little-endian. */
    createBuffer: (bytes, channels, bits, rate) => {
      const data = bits === 8 ? bytes : new Int16Array(bytes.buffer, bytes.byteOffset, bytes.length >> 1);
      const frames = Math.floor(data.length / channels);
      const buffer = new AudioBuffer({ numberOfChannels: channels, length: Math.max(1, frames), sampleRate: rate });
      for (let c = 0; c < channels; c++) {
        const out = buffer.getChannelData(c);
        for (let i = 0; i < frames; i++) {
          const s = data[i * channels + c];
          out[i] = bits === 8 ? ((s & 255) - 128) / 128 : s / 32768;
        }
      }
      return put({ buffer });
    },
    deleteBuffer: drop,
    genSource: () => {
      const gain = audio.createGain();
      gain.connect(audio.destination);
      return put({ gain, buffer: 0, loop: false, node: null, playing: false });
    },
    deleteSource: (id) => { al.stop(id); drop(id); },
    setBuffer: (id, b) => { get(id).buffer = b; },
    setLooping: (id, loop) => { get(id).loop = loop; },
    setGain: (id, g) => { get(id).gain.gain.value = g; },
    play: (id) => {
      const s = get(id);
      al.stop(id);
      const b = get(s.buffer);
      if (!b || !b.buffer) return;
      const node = audio.createBufferSource();
      node.buffer = b.buffer;
      node.loop = s.loop;
      node.connect(s.gain);
      node.onended = () => { if (s.node === node) { s.playing = false; s.node = null; } };
      s.node = node;
      s.playing = true;
      node.start();
    },
    stop: (id) => {
      const s = get(id);
      if (s && s.node) { const n = s.node; s.node = null; s.playing = false; try { n.stop(); } catch (e) { /* not started */ } }
    },
    state: (id) => (get(id) && get(id).playing ? AL_PLAYING : AL_STOPPED),
  };

  // ---- decoders (stb_image, stb_vorbis) ------------------------------------------------------
  // Straight (not premultiplied) RGBA, exactly as stb_image gives it: through a WebGL texture with
  // premultiplication off, since a 2D canvas would round semi-transparent pixels.
  let decodeGl = null;
  function straightRgba(bitmap) {
    if (!decodeGl) decodeGl = new OffscreenCanvas(1, 1).getContext("webgl2");
    const g = decodeGl, w = bitmap.width, h = bitmap.height;
    const tex = g.createTexture();
    g.bindTexture(g.TEXTURE_2D, tex);
    g.pixelStorei(g.UNPACK_PREMULTIPLY_ALPHA_WEBGL, false);
    g.pixelStorei(g.UNPACK_COLORSPACE_CONVERSION_WEBGL, g.NONE);
    g.texImage2D(g.TEXTURE_2D, 0, g.RGBA8, g.RGBA, g.UNSIGNED_BYTE, bitmap);
    const fb = g.createFramebuffer();
    g.bindFramebuffer(g.FRAMEBUFFER, fb);
    g.framebufferTexture2D(g.FRAMEBUFFER, g.COLOR_ATTACHMENT0, g.TEXTURE_2D, tex, 0);
    const out = new Uint8Array(w * h * 4);
    g.readPixels(0, 0, w, h, g.RGBA, g.UNSIGNED_BYTE, out);
    g.deleteFramebuffer(fb);
    g.deleteTexture(tex);
    return out;
  }
  const decode = {
    image: (bytes, done) => {
      createImageBitmap(new Blob([bytes]), { premultiplyAlpha: "none", colorSpaceConversion: "none" })
        .then((bitmap) => {
          const rgba = straightRgba(bitmap), argb = new Int32Array(bitmap.width * bitmap.height);
          for (let i = 0, j = 0; i < argb.length; i++, j += 4) {
            argb[i] = rgba[j + 3] << 24 | rgba[j] << 16 | rgba[j + 1] << 8 | rgba[j + 2];
          }
          return { width: bitmap.width, height: bitmap.height, argb, error: null };
        })
        .catch((e) => ({ width: 0, height: 0, argb: null, error: String(e) }))
        .then(done);
    },
    vorbis: (bytes, done) => {
      const context = new OfflineAudioContext(1, 1, 48000);
      context.decodeAudioData(bytes.slice().buffer)
        .then((b) => {
          const channels = Math.min(2, b.numberOfChannels), pcm = new Int16Array(b.length * channels);
          for (let c = 0; c < channels; c++) {
            const d = b.getChannelData(c);
            for (let i = 0; i < b.length; i++) pcm[i * channels + c] = Math.max(-32768, Math.min(32767, Math.round(d[i] * 32767)));
          }
          return { channels, rate: b.sampleRate, samples: new Int8Array(pcm.buffer) };
        })
        .catch(() => ({ channels: 0, rate: 0, samples: null }))
        .then(done);
    },
  };

  // ---- files --------------------------------------------------------------------------------
  // done is called last and outside the catch: the game resumes inside it, and an exception the
  // game throws there must not resume it a second time (TeaVM fails with an NPE, the real error lost)
  const files = {
    fetch: (path, done) => {
      fetch(path).then((r) => (r.ok ? r.arrayBuffer() : null))
        .then((b) => (b ? new Int8Array(b) : null))
        .catch(() => null)
        .then(done);
    },
    fetchText: (path, done) => {
      fetch(path).then((r) => (r.ok ? r.text() : null)).catch(() => null).then(done);
    },
    // the game's own files (save games): localStorage, under this page's address so two games
    // on one site keep their files apart; base64, as localStorage holds text
    storedPaths: () => {
      const out = [];
      try {
        for (let i = 0; i < localStorage.length; i++) {
          const k = localStorage.key(i);
          if (k.startsWith(storagePrefix)) out.push(k.substring(storagePrefix.length));
        }
      } catch (e) { /* storage blocked: nothing stored */ }
      return out;
    },
    storageGet: (path) => { try { return localStorage.getItem(storagePrefix + path); } catch (e) { return null; } },
    storageSet: (path, value) => {
      try {
        localStorage.setItem(storagePrefix + path, value);
      } catch (e) {
        console.error("Scratch for Java: could not keep " + path + " in the browser's storage: " + e);
      }
    },
    storageRemove: (path) => { try { localStorage.removeItem(storagePrefix + path); } catch (e) { /* blocked */ } },
  };
  const storagePrefix = "scratch4j:" + location.pathname + ":";

  return { gl: glApi, glfw, al, decode, files, stats };
})();
