package dao;

import model.Page;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PageDAOImplTest {
    private static final String TEST_DB_URL = "jdbc:h2:./dbtest";
    private static final PageDAOImpl pageDAO = new PageDAOImpl(TEST_DB_URL);
    private static Page page;

    @BeforeClass
    public static void initTestPage() {
        page = new Page();
        page.setPageName("Generated test page");
        page.setUrl(UUID.randomUUID().toString());
        Map<String, Integer> words = new LinkedHashMap<>();
        words.put("This", 1);
        words.put("is", 2);
        words.put("Google", 3);
        page.setWords(words);
    }

    @Test
    public void givenNothing_whenFindAll_thenReturnPagesList() {
        List<Page> pageList = pageDAO.findAll();
        Assert.assertNotNull(pageList);
    }

    @Test
    public void givenNullId_whenFindById_thenNotThrowPageNotFoundException() {
            pageDAO.findById(0L);
    }

    @Test
    public void createPage() {
        pageDAO.create(page);
    }

    @Test
    public void givenId_whenFindById_thenReturnPagedById() {
        Page page = pageDAO.findById(1L);
        Assert.assertEquals(1L,page.getId());
        Assert.assertNotNull(page);
    }

    @Test
    public void givenPageOrNullPage_whenCreate_thenReturnTrueOrFalse() {
        Assert.assertTrue(pageDAO.create(page));
        Assert.assertFalse(pageDAO.create(null));
    }

    @Test
    public void printAllPages() {
        List<Page> pageList = pageDAO.findAll();
        for (Page p : pageList) {
            System.out.println(p);
        }
    }

    @Test
    public void givenPageId_whenDeleteById_thenNotThrowPageNotFoundException() {
        pageDAO.deleteById(35165L);
    }

    @Test
    public void givenPageId_whenDeleteById_thenReturnTrue() {
        Page page = new Page (1L,"TestPage", UUID.randomUUID().toString());
        pageDAO.create(page);
        Page pageWithIdFromBase = pageDAO.findByUrl(page.getUrl());
        Assert.assertTrue(pageDAO.deleteById(pageWithIdFromBase.getId()));
    }
}