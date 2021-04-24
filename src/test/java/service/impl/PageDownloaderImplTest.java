package service.impl;

import model.Page;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class PageDownloaderImplTest {
    private static Page page;
    private static final Integer countOfWordsInMap = 66;
    private static final String URL = "https://www.test.com/";
    private static final PageDownloaderImpl pageDownloader = new PageDownloaderImpl();

    @BeforeClass
    public static void init() throws IOException {
        File testHtmlPage = new File("./src/test/pages/mytestpage.html");
        page = pageDownloader.getPageByFile(testHtmlPage);
    }

    @Test
    public void givenUrl_whenGetPage_thenReturnPage() {
        assertThat(page).isNotNull();
        assertThat(page.getWords().size()).isEqualTo(countOfWordsInMap);
    }
}