package manager.impl;

import model.Page;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class PageParserImplTest {
    private static final File PAGE_FILE = new File(
            "src/test/pages/Разработка программного обеспечения на заказ - SimbirSoft.html");
    private static final PageParserImpl pageParser = new PageParserImpl();
    private static Page pageActual;

    @BeforeClass
    public static void init() {
        PageParserImpl.setPath("src/test/pages/test");
        pageActual = pageParser.getPageByFile(PAGE_FILE);
    }

    @AfterClass
    public static void deleteFiles() {
        File folder = new File(PageParserImpl.getPath());
        deleteFiles(folder);
    }

    @Test
    public void givenFile_whenGetPageByFile_thenReturnPage() {
        Page pageExpected = pageParser.getPageByFile(PAGE_FILE);
        assertEquals(pageExpected, pageActual);
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