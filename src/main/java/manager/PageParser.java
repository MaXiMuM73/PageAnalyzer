package manager;

import model.Page;

import java.io.File;

public interface PageParser {

    /**
     * Return loaded page
     * @param url of {@link Page}
     * @return {@link Page page}
     */
    Page getPageByUrl(String url);

    /**
     * Return loaded page
     * @param file
     * @return {@link Page page}
     */
    Page getPageByFile(File file);

    /**
     * Saves the page and statistics to the files directory
     * @return true if saved successfully; false if not saved
     */
    boolean saveToFiles(String url);
}