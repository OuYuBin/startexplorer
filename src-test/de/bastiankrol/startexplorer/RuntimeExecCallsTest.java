package de.bastiankrol.startexplorer;

import static de.bastiankrol.startexplorer.RuntimeExecCalls.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class
 * 
 * @author Bastian Krol
 */
public class RuntimeExecCallsTest
{
  private RuntimeExecCalls runtimeExecCalls;
  private IRuntimeExecDelegate mockRuntimeExecDelegate;
  private String path;
  private String resourceName;
  private String extension;
  private File file;
  private List<File> fileList;

  /**
   * JUnite before
   */
  @Before
  public void setUp()
  {

    this.path = "C:\\file\\to\\";
    this.extension = "txt";
    this.resourceName = "resource";
    this.file = new File(this.path + this.resourceName + "." + this.extension);

    this.fileList = new ArrayList<File>();
    this.fileList.add(new File("C:\\file\\to\\resource"));
    this.fileList.add(new File("/file/to/another/resource"));
    this.fileList
        .add(new File(
            "some weird string (RuntimeExecCalls doesn't check if it's a valid file"));

    this.runtimeExecCalls = new RuntimeExecCalls();
    this.mockRuntimeExecDelegate = mock(IRuntimeExecDelegate.class);
    this.runtimeExecCalls.setRuntimeExecDelegate(mockRuntimeExecDelegate);
  }

  /**
   * JUnit test method
   */
  @Test
  public void testStartWindowsExplorerForFile()
  {
    this.runtimeExecCalls.startWindowsExplorerForFile(this.file, false);
    verify(this.mockRuntimeExecDelegate).exec(
        "Explorer.exe /e,\"" + this.file.getAbsolutePath() + "\"");
  }
  

  /**
   * JUnit test method
   */
  @Test
  public void testSelectFileInWindowsExplorer()
  {
    File fileMock = mock(File.class);
    when(fileMock.isFile()).thenReturn(true);
    when(fileMock.getAbsolutePath()).thenReturn("C:\\file\\to\\resource.txt");
    this.runtimeExecCalls.startWindowsExplorerForFile(fileMock, true);
    verify(this.mockRuntimeExecDelegate).exec(
        "Explorer.exe /select,\"" + this.file.getAbsolutePath() + "\"");
  }

  /**
   * JUnit test method
   */
  @Test
  public void testStartWindowsExplorerForFileList()
  {
    this.runtimeExecCalls.startWindowsExplorerForFileList(this.fileList, false);
    for (File fileFromList : this.fileList)
    {
      verify(this.mockRuntimeExecDelegate).exec(
          "Explorer.exe /e,\"" + fileFromList.getAbsolutePath() + "\"");
    }
  }

  /**
   * JUnit test method
   */
  @Test
  public void testStartWindowsSystemApplicationForFile()
  {
    this.runtimeExecCalls.startWindowsSystemApplicationForFile(this.file);
    verify(this.mockRuntimeExecDelegate).exec(
        "cmd.exe /c \"" + this.file.getAbsolutePath() + "\"");
  }

  /**
   * JUnit test method
   */
  @Test
  public void testStartWindowsSystemApplicationForFileList()
  {
    this.runtimeExecCalls
        .startWindowsSystemApplicationForFileList(this.fileList);
    for (File fileFromList : this.fileList)
    {
      verify(this.mockRuntimeExecDelegate).exec(
          "cmd.exe /c \"" + fileFromList.getAbsolutePath() + "\"");
    }
  }

  /**
   * JUnit test method
   */
  @Test
  public void testStartCmdExeForFile()
  {
    this.runtimeExecCalls.startCmdExeForFile(this.file);
    verify(this.mockRuntimeExecDelegate).exec(
        "cmd.exe /c start /d \"" + this.file.getAbsolutePath() + "\"");

  }

  /**
   * JUnit test method
   */
  @Test
  public void testStartCmdExeForFileList()
  {
    this.runtimeExecCalls.startCmdExeForFileList(this.fileList);
    for (File fileFromList : this.fileList)
    {
      verify(this.mockRuntimeExecDelegate).exec(
          "cmd.exe /c start /d \"" + fileFromList.getAbsolutePath() + "\"");
    }
  }

  /**
   * JUnit test method
   */
  @Test
  public void testStartCustomCommandForFile()
  {
    String customCommand = "parent: " + RESOURCE_PARENT_VAR //
        + " name: " + RESOURCE_NAME_VAR //
        + " complete path: " + RESOURCE_PATH_VAR //
        + " name without extension: " + RESOURCE_NAME_WIHTOUT_EXTENSION_VAR //
        + " extension: " + RESOURCE_EXTENSION_VAR //
    ;
    String expectedCall = //
    "parent: \"" + this.file.getParentFile().getAbsolutePath() //
        + "\" name: \"" + this.file.getName() //
        + "\" complete path: \"" + this.file.getAbsolutePath() //
        + "\" name without extension: " + this.resourceName //
        + " extension: " + this.extension //
    ;
    this.runtimeExecCalls.startCustomCommandForFile(customCommand, this.file);
    verify(this.mockRuntimeExecDelegate).exec(expectedCall);
  }

  @Test
  public void shouldSplitFilenamesWithoutDotCorrectly()
  {
    String[] nameWithoutExtensionAndExtension = RuntimeExecCalls
        .separateNameAndExtension(new File("/path/to/resource"));
    assertEquals("resource", nameWithoutExtensionAndExtension[0]);
    assertEquals("", nameWithoutExtensionAndExtension[1]);
  }

  @Test
  public void shouldSplitFilenamesWithOneDotCorrectly()
  {
    String[] nameWithoutExtensionAndExtension = RuntimeExecCalls
        .separateNameAndExtension(new File("/path/to/resource.extension"));
    assertEquals("resource", nameWithoutExtensionAndExtension[0]);
    assertEquals("extension", nameWithoutExtensionAndExtension[1]);
  }

  @Test
  public void shouldSplitFilenamesWithSeveralDotsCorrectly()
  {
    String[] nameWithoutExtensionAndExtension = RuntimeExecCalls
        .separateNameAndExtension(new File("/path/to/re.so.ur.ce.extension"));
    assertEquals("re.so.ur.ce", nameWithoutExtensionAndExtension[0]);
    assertEquals("extension", nameWithoutExtensionAndExtension[1]);
  }

  @Test
  public void shouldSplitFilenamesWithTrailingDotCorrectly()
  {
    // Not a valid file name on Windows, but on Linux
    String[] nameWithoutExtensionAndExtension = RuntimeExecCalls
        .separateNameAndExtension(new File("/path/to/resource."));
    assertEquals("resource.", nameWithoutExtensionAndExtension[0]);
    assertEquals("", nameWithoutExtensionAndExtension[1]);
  }

  @Test
  public void shouldSplitFilenamesWithSeveralDotsAndTrailingDotCorrectly()
  {
    // Not a valid file name on Windows, but on Linux
    String[] nameWithoutExtensionAndExtension = RuntimeExecCalls
        .separateNameAndExtension(new File("/path/to/re.so.ur.ce.extension."));
    assertEquals("re.so.ur.ce", nameWithoutExtensionAndExtension[0]);
    assertEquals("extension.", nameWithoutExtensionAndExtension[1]);
  }

  @Test
  public void shouldSplitFilenamesWithOnlyLeadingDotCorrectly()
  {
    // Arguable: From my point of view, a leading dot should not
    // be interpreted as a name separator because it's
    // used to hide files in *nix.
    String[] nameWithoutExtensionAndExtension = RuntimeExecCalls
        .separateNameAndExtension(new File("/path/to/.resource"));
    assertEquals(".resource", nameWithoutExtensionAndExtension[0]);
    assertEquals("", nameWithoutExtensionAndExtension[1]);
  }

  @Test
  public void shouldSplitFilenamesWithLeadingDotAndMoreDotsCorrectly()
  {
    // Arguable: From my point of view, a leading dot should not
    // be interpreted as a name separator because it's
    // used to hide files in *nix.
    String[] nameWithoutExtensionAndExtension = RuntimeExecCalls
        .separateNameAndExtension(new File("/path/to/.re.so.ur.ce.extension"));
    assertEquals(".re.so.ur.ce", nameWithoutExtensionAndExtension[0]);
    assertEquals("extension", nameWithoutExtensionAndExtension[1]);
  }
}
