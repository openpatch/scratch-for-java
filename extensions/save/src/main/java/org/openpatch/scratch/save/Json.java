package org.openpatch.scratch.save;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Objects to JSON and back without a library: the public fields of plain classes (and, for
 * {@link File}, bean getters and setters), strings, numbers, booleans, characters, enums, arrays,
 * lists, sets and maps, nested as deep as they go. Reflection only on the learner's own classes,
 * which a web page build allows too.
 *
 * <p>Strict reading ({@link SaveData}) refuses what does not fit exactly: unknown properties, a
 * number with a fraction for an int, a string for a number, null for a primitive. Lenient reading
 * ({@link File}) skips unknown properties and converts what it can.
 */
final class Json {

  /** The text is not JSON, or does not fit the class. */
  static final class JsonException extends Exception {
    JsonException(String message) {
      super(message);
    }
  }

  private Json() {}

  // ---- writing ------------------------------------------------------------------------------

  static String write(Object value, boolean beans) throws JsonException {
    StringBuilder out = new StringBuilder();
    write(out, value, beans, 0);
    return out.append('\n').toString();
  }

  private static void write(StringBuilder out, Object value, boolean beans, int depth) throws JsonException {
    if (depth > 64) {
      throw new JsonException("The data is nested too deep (does an object contain itself?)");
    }
    if (value == null) {
      out.append("null");
    } else if (value instanceof String || value instanceof Character) {
      string(out, value.toString());
    } else if (value instanceof Boolean || value instanceof Byte || value instanceof Short
        || value instanceof Integer || value instanceof Long) {
      out.append(value);
    } else if (value instanceof Float || value instanceof Double) {
      double d = ((java.lang.Number) value).doubleValue();
      if (Double.isNaN(d) || Double.isInfinite(d)) {
        string(out, value.toString()); // JSON has no NaN or Infinity; read back from the string
      } else {
        out.append(value);
      }
    } else if (value instanceof Enum<?> e) {
      string(out, e.name());
    } else if (value.getClass().isArray()) {
      List<Object> items = new ArrayList<>();
      for (int i = 0; i < Array.getLength(value); i++) {
        items.add(Array.get(value, i));
      }
      list(out, items, beans, depth);
    } else if (value instanceof Collection<?> c) {
      list(out, new ArrayList<>(c), beans, depth);
    } else if (value instanceof Map<?, ?> m) {
      Map<String, Object> entries = new LinkedHashMap<>();
      for (Map.Entry<?, ?> e : m.entrySet()) {
        entries.put(e.getKey() instanceof Enum<?> k ? k.name() : String.valueOf(e.getKey()), e.getValue());
      }
      object(out, entries, beans, depth);
    } else {
      Map<String, Object> properties = new LinkedHashMap<>();
      for (Property p : properties(value.getClass(), beans)) {
        properties.put(p.name, p.get(value));
      }
      object(out, properties, beans, depth);
    }
  }

  private static void list(StringBuilder out, List<Object> items, boolean beans, int depth) throws JsonException {
    if (items.isEmpty()) {
      out.append("[]");
      return;
    }
    boolean flat = items.stream().allMatch(i -> i == null || i instanceof java.lang.Number || i instanceof Boolean
        || i instanceof String || i instanceof Character || i instanceof Enum);
    out.append('[');
    for (int i = 0; i < items.size(); i++) {
      out.append(i == 0 ? "" : ",");
      if (flat) {
        out.append(i == 0 ? "" : " ");
      } else {
        newline(out, depth + 1);
      }
      write(out, items.get(i), beans, depth + 1);
    }
    if (!flat) {
      newline(out, depth);
    }
    out.append(']');
  }

  private static void object(StringBuilder out, Map<String, Object> entries, boolean beans, int depth)
      throws JsonException {
    if (entries.isEmpty()) {
      out.append("{}");
      return;
    }
    out.append('{');
    boolean first = true;
    for (Map.Entry<String, Object> e : entries.entrySet()) {
      out.append(first ? "" : ",");
      first = false;
      newline(out, depth + 1);
      string(out, e.getKey());
      out.append(": ");
      write(out, e.getValue(), beans, depth + 1);
    }
    newline(out, depth);
    out.append('}');
  }

  private static void newline(StringBuilder out, int depth) {
    out.append('\n');
    for (int i = 0; i < depth; i++) {
      out.append("  ");
    }
  }

  private static void string(StringBuilder out, String text) {
    out.append('"');
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      switch (c) {
        case '"' -> out.append("\\\"");
        case '\\' -> out.append("\\\\");
        case '\n' -> out.append("\\n");
        case '\r' -> out.append("\\r");
        case '\t' -> out.append("\\t");
        case '\b' -> out.append("\\b");
        case '\f' -> out.append("\\f");
        default -> {
          if (c < 0x20) {
            out.append(String.format(Locale.ROOT, "\\u%04x", (int) c));
          } else {
            out.append(c);
          }
        }
      }
    }
    out.append('"');
  }

  // ---- reading ------------------------------------------------------------------------------

  /** A JSON number as written, so a fraction can be told from an integer. */
  record JsonNumber(String text) {
    boolean integral() {
      return text.indexOf('.') < 0 && text.indexOf('e') < 0 && text.indexOf('E') < 0;
    }
  }

  static <T> T read(String text, Class<T> type, boolean strict, boolean beans) throws JsonException {
    Parser parser = new Parser(text);
    Object tree = parser.value();
    parser.end();
    return type.cast(bind(tree, type, strict, beans, "$"));
  }

  /** Parsed JSON: null, Boolean, String, JsonNumber, List and LinkedHashMap. */
  static Object parse(String text) throws JsonException {
    Parser parser = new Parser(text);
    Object tree = parser.value();
    parser.end();
    return tree;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  static Object bind(Object tree, Type type, boolean strict, boolean beans, String where) throws JsonException {
    Class<?> raw = raw(type);
    if (tree == null) {
      if (raw.isPrimitive()) {
        if (strict) {
          throw new JsonException(where + " is null, but " + raw.getName() + " needs a value");
        }
        return defaultValue(raw);
      }
      return null;
    }
    if (raw == Object.class) {
      return natural(tree);
    }
    if (raw == String.class) {
      if (tree instanceof String s) {
        return s;
      }
      if (!strict && (tree instanceof JsonNumber || tree instanceof Boolean)) {
        return tree instanceof JsonNumber n ? n.text() : tree.toString();
      }
      throw mismatch(where, "a string", tree);
    }
    if (raw == char.class || raw == Character.class) {
      if (tree instanceof String s && s.length() == 1) {
        return s.charAt(0);
      }
      throw mismatch(where, "one character", tree);
    }
    if (raw == boolean.class || raw == Boolean.class) {
      if (tree instanceof Boolean b) {
        return b;
      }
      if (!strict && tree instanceof String s && (s.equals("true") || s.equals("false"))) {
        return Boolean.parseBoolean(s);
      }
      throw mismatch(where, "true or false", tree);
    }
    if (isNumber(raw)) {
      return number(tree, raw, strict, where);
    }
    if (raw.isEnum()) {
      if (tree instanceof String s) {
        for (Object constant : raw.getEnumConstants()) {
          if (((Enum<?>) constant).name().equals(s)) {
            return constant;
          }
        }
      }
      throw mismatch(where, "one of " + java.util.Arrays.toString(raw.getEnumConstants()), tree);
    }
    if (raw.isArray()) {
      List<?> items = list(tree, where);
      Type component = type instanceof GenericArrayType g ? g.getGenericComponentType() : raw.getComponentType();
      Object array = Array.newInstance(raw.getComponentType(), items.size());
      for (int i = 0; i < items.size(); i++) {
        Array.set(array, i, bind(items.get(i), component, strict, beans, where + "[" + i + "]"));
      }
      return array;
    }
    if (Collection.class.isAssignableFrom(raw)) {
      List<?> items = list(tree, where);
      Type element = argument(type, 0);
      Collection out = Set.class.isAssignableFrom(raw) ? new LinkedHashSet<>() : new ArrayList<>();
      for (int i = 0; i < items.size(); i++) {
        out.add(bind(items.get(i), element, strict, beans, where + "[" + i + "]"));
      }
      return out;
    }
    if (Map.class.isAssignableFrom(raw)) {
      if (!(tree instanceof Map<?, ?> entries)) {
        throw mismatch(where, "an object", tree);
      }
      Type keyType = argument(type, 0), valueType = argument(type, 1);
      Map out = new LinkedHashMap<>();
      for (Map.Entry<?, ?> e : entries.entrySet()) {
        String key = (String) e.getKey();
        Object k = raw(keyType) == Object.class || raw(keyType) == String.class ? key
            : bind(raw(keyType).isEnum() ? key : new JsonNumber(key), keyType, false, beans, where + "." + key);
        out.put(k, bind(e.getValue(), valueType, strict, beans, where + "." + key));
      }
      return out;
    }
    if (!(tree instanceof Map<?, ?> entries)) {
      throw mismatch(where, "an object", tree);
    }
    Object target = instantiate(raw);
    java.util.Map<String, Property> byName = new LinkedHashMap<>();
    for (Property p : properties(raw, beans)) {
      byName.put(p.name, p);
    }
    for (Map.Entry<?, ?> e : entries.entrySet()) {
      String name = (String) e.getKey();
      Property p = byName.get(name);
      if (p == null || !p.writable()) {
        if (strict) {
          throw new JsonException(where + "." + name + " is not a public field of " + raw.getSimpleName());
        }
        continue;
      }
      p.set(target, bind(e.getValue(), p.type, strict, beans, where + "." + name));
    }
    return target;
  }

  private static Object number(Object tree, Class<?> raw, boolean strict, String where) throws JsonException {
    String text;
    if (tree instanceof JsonNumber n) {
      text = n.text();
    } else if (tree instanceof String s && (!strict || raw == double.class || raw == Double.class
        || raw == float.class || raw == Float.class) && (s.equals("NaN") || s.endsWith("Infinity"))) {
      text = s; // how NaN and Infinity are written
    } else if (!strict && tree instanceof String s) {
      text = s.trim();
    } else {
      throw mismatch(where, "a number", tree);
    }
    try {
      boolean integralType = raw == int.class || raw == Integer.class || raw == long.class || raw == Long.class
          || raw == short.class || raw == Short.class || raw == byte.class || raw == Byte.class;
      if (integralType) {
        long value;
        if (new JsonNumber(text).integral()) {
          value = Long.parseLong(text);
        } else if (strict) {
          throw new JsonException(where + " is " + text + ", but " + raw.getSimpleName() + " takes whole numbers");
        } else {
          value = (long) Double.parseDouble(text);
        }
        if (raw == int.class || raw == Integer.class) {
          if (value != (int) value) {
            throw new JsonException(where + " is too big for an int: " + text);
          }
          return (int) value;
        }
        if (raw == short.class || raw == Short.class) {
          return (short) value;
        }
        if (raw == byte.class || raw == Byte.class) {
          return (byte) value;
        }
        return value;
      }
      double value = Double.parseDouble(text);
      return raw == float.class || raw == Float.class ? (Object) (float) value : (Object) value;
    } catch (NumberFormatException e) {
      throw mismatch(where, "a number", tree);
    }
  }

  private static boolean isNumber(Class<?> raw) {
    return raw == int.class || raw == long.class || raw == double.class || raw == float.class || raw == short.class
        || raw == byte.class || java.lang.Number.class.isAssignableFrom(raw);
  }

  private static List<?> list(Object tree, String where) throws JsonException {
    if (tree instanceof List<?> items) {
      return items;
    }
    throw mismatch(where, "a list [...]", tree);
  }

  /** A tree as plain Java values: numbers as Long or Double. */
  private static Object natural(Object tree) {
    if (tree instanceof JsonNumber n) {
      return n.integral() ? (Object) Long.parseLong(n.text()) : (Object) Double.parseDouble(n.text());
    }
    if (tree instanceof List<?> items) {
      List<Object> out = new ArrayList<>();
      for (Object item : items) {
        out.add(natural(item));
      }
      return out;
    }
    if (tree instanceof Map<?, ?> entries) {
      Map<Object, Object> out = new LinkedHashMap<>();
      for (Map.Entry<?, ?> e : entries.entrySet()) {
        out.put(e.getKey(), natural(e.getValue()));
      }
      return out;
    }
    return tree;
  }

  private static JsonException mismatch(String where, String wanted, Object tree) {
    String found = tree == null ? "null" : tree instanceof JsonNumber n ? n.text()
        : tree instanceof String s ? "\"" + s + "\"" : tree instanceof List ? "a list"
        : tree instanceof Map ? "an object" : tree.toString();
    return new JsonException(where + " should be " + wanted + ", but is " + found);
  }

  private static Object defaultValue(Class<?> primitive) {
    return Array.get(Array.newInstance(primitive, 1), 0);
  }

  static Class<?> raw(Type type) {
    if (type instanceof Class<?> c) {
      return c;
    }
    if (type instanceof ParameterizedType p) {
      return raw(p.getRawType());
    }
    if (type instanceof GenericArrayType g) {
      return Array.newInstance(raw(g.getGenericComponentType()), 0).getClass();
    }
    return Object.class; // a type variable or wildcard
  }

  private static Type argument(Type type, int index) {
    if (type instanceof ParameterizedType p && p.getActualTypeArguments().length > index) {
      return p.getActualTypeArguments()[index];
    }
    return Object.class;
  }

  private static Object instantiate(Class<?> type) throws JsonException {
    if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
      throw new JsonException(type.getSimpleName() + " is abstract: save a class that can be created");
    }
    if (type.getEnclosingClass() != null && !Modifier.isStatic(type.getModifiers())) {
      throw new JsonException(type.getSimpleName() + " is an inner class: make it static to save it");
    }
    try {
      Constructor<?> constructor = type.getDeclaredConstructor();
      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (NoSuchMethodException e) {
      throw new JsonException(type.getSimpleName() + " needs a constructor without parameters to be loaded");
    } catch (ReflectiveOperationException | RuntimeException e) {
      throw new JsonException("Could not create " + type.getSimpleName() + ": " + e);
    }
  }

  // ---- properties ---------------------------------------------------------------------------

  /** A public field, or with beans a getter/setter pair. */
  private static final class Property {
    final String name;
    final Type type;
    final Field field;
    final Method getter, setter;

    Property(String name, Type type, Field field, Method getter, Method setter) {
      this.name = name;
      this.type = type;
      this.field = field;
      this.getter = getter;
      this.setter = setter;
    }

    boolean writable() {
      return this.field != null || this.setter != null;
    }

    Object get(Object target) throws JsonException {
      try {
        return this.field != null ? this.field.get(target) : this.getter.invoke(target);
      } catch (ReflectiveOperationException | RuntimeException e) {
        throw new JsonException("Could not read " + this.name + ": " + e);
      }
    }

    void set(Object target, Object value) throws JsonException {
      try {
        if (this.field != null) {
          this.field.set(target, value);
        } else {
          this.setter.invoke(target, value);
        }
      } catch (ReflectiveOperationException | RuntimeException e) {
        throw new JsonException("Could not set " + this.name + ": " + e);
      }
    }
  }

  /** Public instance fields, not transient, superclasses first; with beans also get/is/set methods. */
  private static List<Property> properties(Class<?> type, boolean beans) {
    List<Class<?>> chain = new ArrayList<>();
    for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
      chain.add(0, c);
    }
    Map<String, Property> out = new LinkedHashMap<>();
    for (Class<?> c : chain) {
      for (Field f : c.getDeclaredFields()) {
        int m = f.getModifiers();
        if (Modifier.isPublic(m) && !Modifier.isStatic(m) && !Modifier.isTransient(m) && !f.isSynthetic()) {
          out.put(f.getName(), new Property(f.getName(), f.getGenericType(), f, null, null));
        }
      }
    }
    if (beans) {
      Map<String, Method> getters = new LinkedHashMap<>(), setters = new LinkedHashMap<>();
      for (Method m : type.getMethods()) {
        if (Modifier.isStatic(m.getModifiers()) || m.getDeclaringClass() == Object.class) {
          continue;
        }
        String n = m.getName();
        if (m.getParameterCount() == 0 && n.startsWith("get") && n.length() > 3 && m.getReturnType() != void.class) {
          getters.put(decapitalize(n.substring(3)), m);
        } else if (m.getParameterCount() == 0 && n.startsWith("is") && n.length() > 2
            && (m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class)) {
          getters.put(decapitalize(n.substring(2)), m);
        } else if (m.getParameterCount() == 1 && n.startsWith("set") && n.length() > 3) {
          setters.put(decapitalize(n.substring(3)), m);
        }
      }
      for (Map.Entry<String, Method> g : getters.entrySet()) {
        if (!out.containsKey(g.getKey())) {
          out.put(g.getKey(), new Property(g.getKey(), g.getValue().getGenericReturnType(), null, g.getValue(),
              setters.get(g.getKey())));
        }
      }
    }
    return new ArrayList<>(out.values());
  }

  private static String decapitalize(String name) {
    return Character.toLowerCase(name.charAt(0)) + name.substring(1);
  }

  // ---- parsing ------------------------------------------------------------------------------

  private static final class Parser {
    private final String text;
    private int at;

    Parser(String text) {
      this.text = text;
    }

    void end() throws JsonException {
      this.space();
      if (this.at < this.text.length()) {
        throw this.error("unexpected text after the data");
      }
    }

    Object value() throws JsonException {
      this.space();
      if (this.at >= this.text.length()) {
        throw this.error("the data ends too early");
      }
      char c = this.text.charAt(this.at);
      return switch (c) {
        case '{' -> this.object();
        case '[' -> this.array();
        case '"' -> this.string();
        case 't' -> this.word("true", Boolean.TRUE);
        case 'f' -> this.word("false", Boolean.FALSE);
        case 'n' -> this.word("null", null);
        default -> {
          if (c == '-' || c >= '0' && c <= '9') {
            yield this.number();
          }
          throw this.error("unexpected '" + c + "'");
        }
      };
    }

    private Map<String, Object> object() throws JsonException {
      Map<String, Object> out = new LinkedHashMap<>();
      this.at++;
      this.space();
      if (this.peek('}')) {
        this.at++;
        return out;
      }
      while (true) {
        this.space();
        if (!this.peek('"')) {
          throw this.error("a property name in quotes is missing");
        }
        String key = this.string();
        this.space();
        this.expect(':');
        out.put(key, this.value());
        this.space();
        if (this.peek(',')) {
          this.at++;
        } else {
          this.expect('}');
          return out;
        }
      }
    }

    private List<Object> array() throws JsonException {
      List<Object> out = new ArrayList<>();
      this.at++;
      this.space();
      if (this.peek(']')) {
        this.at++;
        return out;
      }
      while (true) {
        out.add(this.value());
        this.space();
        if (this.peek(',')) {
          this.at++;
        } else {
          this.expect(']');
          return out;
        }
      }
    }

    private String string() throws JsonException {
      StringBuilder out = new StringBuilder();
      this.at++;
      while (this.at < this.text.length()) {
        char c = this.text.charAt(this.at++);
        if (c == '"') {
          return out.toString();
        }
        if (c != '\\') {
          out.append(c);
          continue;
        }
        if (this.at >= this.text.length()) {
          break;
        }
        char e = this.text.charAt(this.at++);
        switch (e) {
          case '"', '\\', '/' -> out.append(e);
          case 'n' -> out.append('\n');
          case 'r' -> out.append('\r');
          case 't' -> out.append('\t');
          case 'b' -> out.append('\b');
          case 'f' -> out.append('\f');
          case 'u' -> {
            if (this.at + 4 > this.text.length()) {
              throw this.error("an unfinished \\u escape");
            }
            out.append((char) Integer.parseInt(this.text.substring(this.at, this.at + 4), 16));
            this.at += 4;
          }
          default -> throw this.error("an unknown escape \\" + e);
        }
      }
      throw this.error("a string is not closed");
    }

    private JsonNumber number() {
      int start = this.at;
      while (this.at < this.text.length() && "+-0123456789.eE".indexOf(this.text.charAt(this.at)) >= 0) {
        this.at++;
      }
      return new JsonNumber(this.text.substring(start, this.at));
    }

    private Object word(String word, Object value) throws JsonException {
      if (!this.text.startsWith(word, this.at)) {
        throw this.error("unexpected text");
      }
      this.at += word.length();
      return value;
    }

    private boolean peek(char c) {
      return this.at < this.text.length() && this.text.charAt(this.at) == c;
    }

    private void expect(char c) throws JsonException {
      if (!this.peek(c)) {
        throw this.error("'" + c + "' is missing");
      }
      this.at++;
    }

    private void space() {
      while (this.at < this.text.length() && Character.isWhitespace(this.text.charAt(this.at))) {
        this.at++;
      }
    }

    private JsonException error(String what) {
      int line = 1;
      for (int i = 0; i < Math.min(this.at, this.text.length()); i++) {
        if (this.text.charAt(i) == '\n') {
          line++;
        }
      }
      return new JsonException("Not valid JSON: " + what + " (line " + line + ")");
    }
  }
}
