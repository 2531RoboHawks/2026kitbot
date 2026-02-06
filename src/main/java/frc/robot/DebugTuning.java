package frc.robot;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class DebugTuning {
  private static final String DEBUG_SUFFIX = " (debug)";
  private static final String SAVE_KEY = "Tuning/Save" + DEBUG_SUFFIX;
  private static final String FILE_NAME = "tuning.json";

  private static boolean lastSave = false;

  private DebugTuning() {}

  public static void seedNumber(String baseKey, double value) {
    if (!Constants.DEBUG) {
      return;
    }
    SmartDashboard.setDefaultNumber(baseKey + DEBUG_SUFFIX, value);
  }

  public static void seedBoolean(String baseKey, boolean value) {
    if (!Constants.DEBUG) {
      return;
    }
    SmartDashboard.setDefaultBoolean(baseKey + DEBUG_SUFFIX, value);
  }

  public static double getNumber(String baseKey, double fallback) {
    if (!Constants.DEBUG) {
      return fallback;
    }
    return SmartDashboard.getNumber(baseKey + DEBUG_SUFFIX, fallback);
  }

  public static boolean getBoolean(String baseKey, boolean fallback) {
    if (!Constants.DEBUG) {
      return fallback;
    }
    return SmartDashboard.getBoolean(baseKey + DEBUG_SUFFIX, fallback);
  }

  public static void init() {
    seedBoolean("Tuning/Save", false);
    loadFromFile();
  }

  public static void handleSave() {
    if (!Constants.DEBUG) {
      return;
    }

    boolean save = SmartDashboard.getBoolean(SAVE_KEY, false);
    if (!save || lastSave) {
      lastSave = save;
      return;
    }

    Map<String, Object> values = collectDebugValues();
    writeJson(values);
    printCopyPaste(values);

    SmartDashboard.putBoolean(SAVE_KEY, false);
    lastSave = save;
  }

  public static void loadFromFile() {
    if (!Constants.DEBUG) {
      return;
    }

    Path path = getTuningPath();
    if (!Files.exists(path)) {
      return;
    }

    try {
      String json = Files.readString(path, StandardCharsets.UTF_8);
      Map<String, Object> values = parseFlatJson(json);
      for (Map.Entry<String, Object> entry : values.entrySet()) {
        NetworkTableEntry ntEntry = NetworkTableInstance.getDefault().getEntry(entry.getKey());
        Object value = entry.getValue();
        if (value instanceof Boolean) {
          ntEntry.setBoolean((Boolean) value);
        } else if (value instanceof Double) {
          ntEntry.setDouble((Double) value);
        } else if (value instanceof String) {
          ntEntry.setString((String) value);
        }
      }
    } catch (IOException ex) {
      System.out.println("DebugTuning load failed: " + ex.getMessage());
    }
  }

  private static Map<String, Object> collectDebugValues() {
    Map<String, Object> values = new LinkedHashMap<>();
    NetworkTable root = NetworkTableInstance.getDefault().getTable("");
    collectFromTable(root, "", values);
    return values;
  }

  private static void collectFromTable(NetworkTable table, String prefix, Map<String, Object> out) {
    for (String key : table.getKeys()) {
      String fullKey = prefix.isEmpty() ? key : prefix + "/" + key;
      if (!fullKey.endsWith(DEBUG_SUFFIX)) {
        continue;
      }
      NetworkTableEntry entry = NetworkTableInstance.getDefault().getEntry(fullKey);
      NetworkTableValue value = entry.getValue();
      if (value == null) {
        continue;
      }
      switch (value.getType()) {
        case kBoolean:
          out.put(fullKey, value.getBoolean());
          break;
        case kDouble:
          out.put(fullKey, value.getDouble());
          break;
        case kString:
          out.put(fullKey, value.getString());
          break;
        default:
          break;
      }
    }

    for (String sub : table.getSubTables()) {
      NetworkTable child = table.getSubTable(sub);
      String childPrefix = prefix.isEmpty() ? sub : prefix + "/" + sub;
      collectFromTable(child, childPrefix, out);
    }
  }

  private static void writeJson(Map<String, Object> values) {
    Path path = getTuningPath();
    try {
      Files.createDirectories(path.getParent());
    } catch (IOException ex) {
      System.out.println("DebugTuning create dir failed: " + ex.getMessage());
    }

    try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
      writer.write(toJson(values));
    } catch (IOException ex) {
      System.out.println("DebugTuning save failed: " + ex.getMessage());
    }
  }

  private static void printCopyPaste(Map<String, Object> values) {
    System.out.println("DebugTuning saved values (copy/paste as needed):");
    for (Map.Entry<String, Object> entry : values.entrySet()) {
      System.out.println("  " + entry.getKey() + " = " + entry.getValue());
    }
  }

  private static Path getTuningPath() {
    return Filesystem.getDeployDirectory().toPath().resolve(FILE_NAME);
  }

  private static String toJson(Map<String, Object> values) {
    StringBuilder sb = new StringBuilder();
    sb.append("{\n");
    boolean first = true;
    for (Map.Entry<String, Object> entry : values.entrySet()) {
      if (!first) {
        sb.append(",\n");
      }
      first = false;
      sb.append("  \"").append(escapeJson(entry.getKey())).append("\": ");
      Object value = entry.getValue();
      if (value instanceof String) {
        sb.append("\"").append(escapeJson((String) value)).append("\"");
      } else if (value instanceof Boolean) {
        sb.append(((Boolean) value) ? "true" : "false");
      } else {
        sb.append(String.valueOf(value));
      }
    }
    sb.append("\n}\n");
    return sb.toString();
  }

  private static String escapeJson(String input) {
    StringBuilder sb = new StringBuilder(input.length());
    for (int i = 0; i < input.length(); i++) {
      char ch = input.charAt(i);
      switch (ch) {
        case '\\':
          sb.append("\\\\");
          break;
        case '"':
          sb.append("\\\"");
          break;
        case '\n':
          sb.append("\\n");
          break;
        case '\r':
          sb.append("\\r");
          break;
        case '\t':
          sb.append("\\t");
          break;
        default:
          sb.append(ch);
          break;
      }
    }
    return sb.toString();
  }

  private static Map<String, Object> parseFlatJson(String json) {
    Map<String, Object> values = new LinkedHashMap<>();
    if (json == null) {
      return values;
    }

    int i = 0;
    int len = json.length();
    while (i < len && Character.isWhitespace(json.charAt(i))) {
      i++;
    }
    if (i >= len || json.charAt(i) != '{') {
      return values;
    }
    i++;

    while (i < len) {
      i = skipWhitespace(json, i);
      if (i >= len || json.charAt(i) == '}') {
        break;
      }
      if (json.charAt(i) != '"') {
        break;
      }
      ParseResult keyResult = parseJsonString(json, i);
      if (keyResult == null) {
        break;
      }
      String key = (String) keyResult.value;
      i = skipWhitespace(json, keyResult.nextIndex);
      if (i >= len || json.charAt(i) != ':') {
        break;
      }
      i++;
      i = skipWhitespace(json, i);
      ParseResult valueResult = parseJsonValue(json, i);
      if (valueResult == null) {
        break;
      }
      values.put(key, valueResult.value);
      i = skipWhitespace(json, valueResult.nextIndex);
      if (i < len && json.charAt(i) == ',') {
        i++;
      }
    }

    return values;
  }

  private static int skipWhitespace(String json, int i) {
    int len = json.length();
    while (i < len && Character.isWhitespace(json.charAt(i))) {
      i++;
    }
    return i;
  }

  private static ParseResult parseJsonString(String json, int startIndex) {
    int i = startIndex;
    int len = json.length();
    if (i >= len || json.charAt(i) != '"') {
      return null;
    }
    i++;
    StringBuilder sb = new StringBuilder();
    while (i < len) {
      char ch = json.charAt(i);
      if (ch == '"') {
        return new ParseResult(sb.toString(), i + 1);
      }
      if (ch == '\\') {
        if (i + 1 >= len) {
          return null;
        }
        char next = json.charAt(i + 1);
        switch (next) {
          case '"':
            sb.append('"');
            break;
          case '\\':
            sb.append('\\');
            break;
          case 'n':
            sb.append('\n');
            break;
          case 'r':
            sb.append('\r');
            break;
          case 't':
            sb.append('\t');
            break;
          default:
            sb.append(next);
            break;
        }
        i += 2;
        continue;
      }
      sb.append(ch);
      i++;
    }
    return null;
  }

  private static ParseResult parseJsonValue(String json, int startIndex) {
    int i = startIndex;
    int len = json.length();
    if (i >= len) {
      return null;
    }
    char ch = json.charAt(i);
    if (ch == '"') {
      ParseResult str = parseJsonString(json, i);
      if (str == null) {
        return null;
      }
      return new ParseResult(str.value, str.nextIndex);
    }

    int end = i;
    while (end < len) {
      char c = json.charAt(end);
      if (c == ',' || c == '}' || Character.isWhitespace(c)) {
        break;
      }
      end++;
    }
    String token = json.substring(i, end);
    if ("true".equals(token)) {
      return new ParseResult(Boolean.TRUE, end);
    }
    if ("false".equals(token)) {
      return new ParseResult(Boolean.FALSE, end);
    }
    try {
      double number = Double.parseDouble(token);
      return new ParseResult(Double.valueOf(number), end);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  private static final class ParseResult {
    private final Object value;
    private final int nextIndex;

    private ParseResult(Object value, int nextIndex) {
      this.value = value;
      this.nextIndex = nextIndex;
    }
  }
}
