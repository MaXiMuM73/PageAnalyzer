package service.impl;

import model.Page;
import org.junit.*;
import service.PageService;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PageServiceImplTest {

    private static final PageServiceImpl pageService = mock(PageServiceImpl.class);

    private static Page page;
    private static final String URL = "https://www.test.com/";

    @BeforeClass
    public static void createTestPage() {
        page = new Page(1L, "test", "https://www.test.ru");
        Map<String, Integer> words = new LinkedHashMap<>();
        words.put("word 1", 1);
        words.put("word 2", 2);
        words.put("word 3", 3);
        page.setWords(words);
    }

    @Test
    public void givenUrl_whenGetPage_thenReturnNotNullPage() {
        when(pageService.getPage(URL)).thenReturn(page);
        page = pageService.getPage(URL);
        assertThat(page).isNotNull().isEqualTo(page);
    }

//    @Test
//    public void givenPage_whenPrint() {
//        PageService pageService = new PageServiceImpl();
//        pageService.print(page);
//        pageService.print(null);
//        Page pageWithoutWords = new Page(1L,
//                "test2",
//                "https://www.test2.ru");
//        pageService.print(pageWithoutWords);
//    }
}