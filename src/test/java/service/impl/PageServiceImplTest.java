package service.impl;

import dao.PageDAO;
import manager.PageParser;
import model.Page;
import org.junit.*;
import service.PageService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PageServiceImplTest {
    private static final PageParser pageParser = mock(PageParser.class);
    private static final PageDAO pageDAO = mock(PageDAO.class);
    private static Page pageExpected;
    private static Page pageActual;
    private static List<Page> pages;
    private static final String URL = "https://www.test.com/";

    @BeforeClass
    public static void createTestPage() {
        pageActual = new Page(1L, "test", "https://www.test.ru");
        Map<String, Integer> words = new LinkedHashMap<>();
        words.put("word 1", 1);
        words.put("word 2", 2);
        words.put("word 3", 3);
        pageActual.setWords(words);
        pages = new ArrayList<>();
        pages.add(pageActual);

    }

    @Test
    public void givenUrl_whenGetPage_thenReturnNotNullPage() {
        when(pageParser.getPageByUrl(URL)).thenReturn(pageActual);
        pageExpected = pageParser.getPageByUrl(URL);
        assertEquals(pageExpected, pageActual);
        assertNotNull(pageExpected);
    }

    @Test
    public void givenPage_whenSaveToDataBase_thenReturnTrue() {
        when(pageDAO.create(pageActual)).thenReturn(true);
        assertTrue(pageDAO.create(pageActual));
    }

    @Test
    public void givenNothing_whenFindAll_thenReturnListPage() {
        when(pageDAO.findAll()).thenReturn(pages);
        List<Page> pagesExpected = pageDAO.findAll();
        assertEquals(pages, pagesExpected);
    }

    @Test
    public void givenId_whenDeleteById_thenReturnTrue() {
        when(pageDAO.deleteById(pageActual.getId())).thenReturn(true);
        assertTrue(pageDAO.deleteById(pageActual.getId()));
    }

    @Test
    public void givenUrl_whenSaveToFiles() {
        when(pageParser.saveToFiles(URL)).thenReturn(true);
        assertTrue(pageParser.saveToFiles(URL));
    }
}