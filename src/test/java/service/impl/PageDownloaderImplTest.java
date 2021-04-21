package service.impl;

import model.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PageDownloaderImplTest {
    private static Document myTestHtmlPage;
    private static Page page;
    private static final Integer countOfWordsInMap = 28;
    private static final String URL = "https://www.test.com/";
    private static final PageDownloaderImpl pageDownloader = mock(PageDownloaderImpl.class);

    @BeforeClass
    public static void init() throws IOException {
        page = new Page();
        myTestHtmlPage = Jsoup.parse(
                new File("./src/test/pages/mytestpage.html"), null);
        String pageName = myTestHtmlPage.title()
                .replaceAll("['/:*?\"<>|]", "_");
        page.setPageName(pageName);
        page.setUrl(myTestHtmlPage.location());
        List<String> words = new ArrayList<>(
                Arrays.asList(myTestHtmlPage.text().split("[ ,.!?:;()—‑«»\"-]")));
        words.removeIf(s -> s.equals(""));
        Map<String, Integer> wordsStatistics = new LinkedHashMap<>();
        for (String word : words) {
            wordsStatistics.merge(word, 1, Integer::sum);
        }
        page.setWords(wordsStatistics);
    }

    @Test
    public void givenUrl_whenGetPage_thenReturnPage() {
        when(pageDownloader.getPage(URL)).thenReturn(page);
        Page expectedPage = pageDownloader.getPage(URL);
        assertThat(expectedPage).isNotNull().isEqualTo(page);
        assertThat(page.getWords().size()).isEqualTo(countOfWordsInMap);
    }
}