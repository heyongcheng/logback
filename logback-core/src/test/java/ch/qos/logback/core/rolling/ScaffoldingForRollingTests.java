package ch.qos.logback.core.rolling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.layout.EchoLayout;
import ch.qos.logback.core.testUtil.FileToBufferUtil;
import ch.qos.logback.core.testUtil.RandomUtil;
import ch.qos.logback.core.util.CoreTestConstants;

/**
 * Scaffolding for various rolling tests. Some assumptions are made: - rollover
 * periodicity is 1 second (without precluding size based roll-over)
 * 
 * @author Ceki G&uuml;lc&uuml;
 * 
 */
public class ScaffoldingForRollingTests {

  static final String DATE_PATTERN_WITH_SECONDS = "yyyy-MM-dd_HH_mm_ss";
  SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_WITH_SECONDS);

  int diff = RandomUtil.getPositiveInt();
  String randomOutputDir = CoreTestConstants.OUTPUT_DIR_PREFIX + diff + "/";
  EchoLayout<Object> layout = new EchoLayout<Object>();
  Context context = new ContextBase();
  List<String> expectedFilenameList = new ArrayList<String>();

  long nextRolloverThreshold; // initialized in setUp()
  long currentTime; // initialized in setUp()
  Calendar cal = Calendar.getInstance();

  public void setUp() {
    context.setName("test");
    cal.set(Calendar.MILLISECOND, 333);
    currentTime = cal.getTimeInMillis();
    recomputeRolloverThreshold(currentTime);
    System.out.println(randomOutputDir);
  }

  public static void existenceCheck(String filename) {
    assertTrue("File " + filename + " does not exist", new File(filename)
        .exists());
  }

  public static File[] getFilesInDirectory(String outputDirStr) {
    File outputDir = new File(outputDirStr);
    return outputDir.listFiles();
  }

  public static void fileContentCheck(File[] fileArray, int runLength,
      String prefix) throws IOException {
    List<String> stringList = new ArrayList<String>();
    for (File file : fileArray) {
      FileToBufferUtil.readIntoList(file, stringList);
    }

    List<String> witnessList = new ArrayList<String>();

    for (int i = 0; i < runLength; i++) {
      witnessList.add(prefix + i);
    }
    assertEquals(witnessList, stringList);
  }

  public static void contentCheck(String outputDirStr, int runLength,
      String prefix) throws IOException {
    File[] fileArray = getFilesInDirectory(outputDirStr);
    fileContentCheck(fileArray, runLength, prefix);
  }

  public static void reverseOrderedContentCheck(String outputDirStr,
      int runLength, String prefix) throws IOException {
    File[] fileArray = getFilesInDirectory(outputDirStr);
    File[] reversedArray = new File[fileArray.length];
    for (int i = 0; i < fileArray.length; i++) {
      reversedArray[fileArray.length - 1 - i] = fileArray[i];
    }
    System.out.println(Arrays.toString(reversedArray));
    fileContentCheck(reversedArray, runLength, prefix);
  }

  public static void existenceCheck(List<String> filenameList) {
    for (String filename : filenameList) {
      assertTrue("File " + filename + " does not exist", new File(filename)
          .exists());
    }
  }

  String testId2FileName(String testId) {
    return randomOutputDir + testId + ".log";
  }

  // assuming rollover every second
  void recomputeRolloverThreshold(long ct) {
    long delta = ct % 1000;
    nextRolloverThreshold = (ct - delta) + 1000;
  }

  boolean passThresholdTime(long nextRolloverThreshold) {
    return currentTime >= nextRolloverThreshold;
  }

  void incCurrentTime(long increment) {
    currentTime += increment;
  }

  Date getDateOfCurrentPeriodsStart() {
    long delta = currentTime % 1000;
    return new Date(currentTime - delta);
  }

  String addGZIfNotLast(int i) {
    int lastIndex = expectedFilenameList.size() - 1;
    if (i != lastIndex) {
      return ".gz";
    } else {
      return "";
    }
  }
}
