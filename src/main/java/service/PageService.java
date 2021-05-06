package service;

import model.Page;

import java.util.List;

public interface PageService {

    /**
     * Return loaded page
     * @param url of {@link Page}
     * @return {@link Page page}
     */
    Page getPage(String url);

    /**
     * Saves the page to the current directory
     * @return true if the page saved successfully; false if the page not saved
     */
//    boolean saveToHDD();

    /**
     * Save page in database {@link Page page}
     * @param page {@link Page}
     * @return true if the page is saved successfully; false if the page is not saved
     */
    boolean saveToDataBase(Page page);

    /**
     * Returns new List of pages {@link Page}
     * @return {@link List <Page> allPagesList}
     */
    List<Page> findAll();

    /**
     * Returns new List of words for all pages {@link Page}
     * @return {@link List<String> allWordsList}
     */
    List<String> findAllWords();

    /**
     * Delete page from database {@link Page page}
     * @param id of {@link Page}
     * @return true if the page deleted successfully; false if the page is not saved
     */
    boolean deleteById(Long id);

    /**
     * Save large page to /files directory
     * @param url {@link Page}
     * @return true if the page and statistics is saved successfully; false if not saved
     */
    boolean saveLargePageByUrl(String url);
}