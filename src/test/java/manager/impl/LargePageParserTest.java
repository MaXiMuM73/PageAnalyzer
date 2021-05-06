package manager.impl;

import org.junit.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.*;

public class LargePageParserTest {
    private static final File PAGE_FILE = new File(
            "src/test/pages/Разработка программного обеспечения на заказ - SimbirSoft.html");
    private static final File LARGE_PAGE_FILE = new File(
            "src/test/pages/large_page.html");
    private static final File INCORRECT_FILE = new File("Incorrect file given");
    private static final String PAGE_URL = "https://www.simbirsoft.ru";
    private static final String INCORRECT_URL = "INCORRECT URL";
    private static final LargePageParser largePageParser = new LargePageParser(PAGE_FILE.getName());
    private static File pageFileExpected;
    private static File wordsFileExpected;
    private static File statisticsFileExpected;

    @BeforeClass
    public static void init() {
        try {
            LargePageParser.setPath("src/test/files/test/");
            pageFileExpected = largePageParser.save(PAGE_FILE);
            wordsFileExpected = largePageParser.saveWords(pageFileExpected);
            statisticsFileExpected = largePageParser.saveStatistics(wordsFileExpected);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @AfterClass
    public static void cleanAfterTests() {
        File folder = new File(LargePageParser.getPath());
        deleteFiles(folder);
    }

    @Test
    public void givenPageFile_whenSave_thenReturnPageFile() {
        assertEquals(pageFileExpected.getName(), PAGE_FILE.getName());
    }

    @Test (expected = FileNotFoundException.class)
    public void givenIncorrectPageFile_whenSave_thenThrowFileNotFoundException() throws IOException {
            largePageParser.save(INCORRECT_FILE);
    }

    @Test (expected = MalformedURLException.class)
    public void givenNull_whenSave_thanThrowMalformedURLException() throws IOException {
        largePageParser.save((String) null);
    }

    @Test
    public void givenNull_whenSave_thenReturnFalse() {
        assertFalse(LargePageParser.saveAll(null));
    }

    @Test
    public void givenPageFile_whenSaveWords_thenReturnWordsFile() {
            assertNotNull(wordsFileExpected);
            assertEquals(wordsFileExpected.getName(), pageFileExpected.getName() + "_words.txt");
    }

    @Test
    public void givenWordsFile_whenSaveStatistics_thenReturnStatisticsFile() {
        assertNotNull(statisticsFileExpected);
        assertEquals(statisticsFileExpected.getName(), pageFileExpected.getName() + "_statistics.txt");
    }

    @Test
    public void givenBadUrl_whenSaveAll_thenReturnFalse() {
        assertFalse(LargePageParser.saveAll(INCORRECT_URL));
    }

    @Ignore
    public void givenLargePage_whenSaveAll_thenReturnTrue() {
        try {
            largePageParser.saveStatistics(
                    largePageParser.saveWords(
                            largePageParser.save(LARGE_PAGE_FILE)
                    )
            );
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void deleteFiles(File folder) {
        if (folder.isDirectory()) {
            File[] list = folder.listFiles();
            if (list != null) {
                for (File tmpF : list) {
                    if (tmpF.isDirectory()) {
                        deleteFiles(tmpF);
                    }
                    tmpF.delete();
                }
            }
            folder.delete();
        }
    }
}