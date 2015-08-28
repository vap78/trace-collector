package personal.vap78.logging.diagtool.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import personal.vap78.logging.diagtool.TraceConfiguration;

@RunWith(PowerMockRunner.class)
@PrepareForTest({File.class, FileReader.class, BufferedReader.class, TraceConfiguration.class})
public class TraceConfigurationTest {

  @Test
  public void testLoading() throws Exception {
    String[] testLocations  = {"test1", "test2", "test3"};
    File mockedFolder = createMocks(
        new String[] {".", "..", "test.txt", "locations.1.txt", "locations.test.txt", "test.jar", "locations.txt", "justname"},
        testLocations
        );
    
    TraceConfiguration.addFromDirectory(mockedFolder);
    
    Collection<String> names = TraceConfiguration.getAllConfigurationNames();
    assertEquals(names.size(), 2);
    assertTrue(names.contains("1"));
    assertTrue(names.contains("test"));
    
    TraceConfiguration cfg = TraceConfiguration.getByName("1");
    List<String> locations = cfg.getLoggers();
    assertEquals(locations.size(), testLocations.length);
    for (String tLoc : testLocations) {
      assertTrue(locations.contains(tLoc));
    }
  }
  
  private File createMocks(String[] fileList, String[] content) throws Exception, IOException {
    PowerMock.reset(File.class, FileReader.class, BufferedReader.class, TraceConfiguration.class);
    
    File mockedFolder = EasyMock.createMock(File.class);
    
    File[] files = new File[fileList.length];
    EasyMock.expect(mockedFolder.listFiles()).andReturn(files).anyTimes();
    
    for (int i = 0; i < fileList.length; i++) {
      files[i] = EasyMock.createMock(File.class);
      PowerMock.expectNew(File.class, mockedFolder, fileList[i]).andReturn(files[i]).anyTimes();
      EasyMock.expect(files[i].getName()).andReturn(fileList[i]).anyTimes();
      EasyMock.expect(files[i].getPath()).andReturn(fileList[i]).anyTimes();
      if (fileList[i].contains("locations") && fileList[i].indexOf(".") + 1 < fileList[i].lastIndexOf(".")) {
        FileInputStream mockInputStream = EasyMock.createMock(FileInputStream.class);
        PowerMock.expectNew(FileInputStream.class, files[i]).andReturn(mockInputStream).once();
        InputStreamReader mockISReader = EasyMock.createMock(InputStreamReader.class);
        PowerMock.expectNew(InputStreamReader.class, mockInputStream, Charset.forName("UTF-8")).andReturn(mockISReader);
        
        BufferedReader mockBufferedReader = EasyMock.createMock(BufferedReader.class);
        IExpectationSetters<String> readerContent = EasyMock.expect(mockBufferedReader.readLine());
        for (String line : content) {
          readerContent.andReturn(line);
        }
        readerContent.andReturn(null);
        mockBufferedReader.close();
        EasyMock.expectLastCall().once();
        PowerMock.expectNew(BufferedReader.class, mockISReader).andReturn(mockBufferedReader).once();
        
        EasyMock.replay(mockInputStream, mockBufferedReader);
      }
      EasyMock.replay(files[i]);
    }
    EasyMock.replay(mockedFolder);
    
    PowerMock.replay(File.class, FileInputStream.class, InputStreamReader.class, BufferedReader.class, TraceConfiguration.class);
    return mockedFolder;
  }
}
