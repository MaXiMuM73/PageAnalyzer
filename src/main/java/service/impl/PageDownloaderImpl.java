package service.impl;

import model.Page;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import service.PageDownloader;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PageDownloaderImpl implements PageDownloader {
    private static final Logger logger = Logger.getLogger(PageDownloaderImpl.class);
    private static Document htmlPage;
    private static Page page;

    public Page getPage(String url) {
        connect(url);
        return page;
    }

    public boolean savePageOnHDD() {
        if (page == null) return false;
        try {
            logger.info("Page saving started.");
            String pageFile = createPageFile();
            File f = new File(pageFile);
            FileUtils.writeStringToFile(f, htmlPage.outerHtml(), StandardCharsets.UTF_8);
            logger.info("Page is saved to the hard disk. Check the folder /pages.");
            return true;
        } catch (IOException ioException) {
            logger.error("Page not saved.", ioException);
            return false;
        }
    }

    private void connect(String url) {
        try {
            logger.info("Connecting to a page: " + url + ".");
            htmlPage = Jsoup.connect(url).maxBodySize(0).userAgent("Chrome").get();
            String pageName = htmlPage.title()
                    .replaceAll("['/:*?\"<>|]","_");
            page = new Page();
            page.setPageName(pageName);
            page.setUrl(url);
            ArrayList<String> words = getWordsFromPage();
            Map<String, Integer> wordsStatistics = getMapOfWordsFromWords(words);
            page.setWords(wordsStatistics);
            logger.info("Page loaded.");
        } catch (IllegalArgumentException exception) {
            logger.error("URL's incorrect. Please, Check the page URL. For example: https://www.yandex.ru.");
        } catch (UnknownHostException unknownHostException) {
            logger.error("Internet connection unavailable.", unknownHostException);
        }
        catch (IOException ioException){
            logger.error("Page loading error.", ioException);
        }
    }

    private ArrayList<String> getWordsFromPage() {
        ArrayList<String> words = new ArrayList<>();
        if (htmlPage != null) {
            words = new ArrayList<>(Arrays.asList(htmlPage
                    .text()
                    .split("[ ,.!?:;()—‑«»\"-]")));
            words.removeIf(s -> s.equals(""));
        }
        return words;
    }

    private Map<String, Integer> getMapOfWordsFromWords(ArrayList<String> words) {
        Map<String, Integer> wordsStatistics = new LinkedHashMap<>();
        for (String word : words) {
            wordsStatistics.merge(word, 1, Integer::sum);
        }
        return wordsStatistics;
    }

    private String createPageFile() {
        String pageFileName = htmlPage.title()
                .replaceAll("[/:*?\"<>|]","_");
        File pagesFolder = new File("pages");
        pagesFolder.mkdir();
        return pagesFolder.getAbsolutePath() + "/" + pageFileName + ".html";
    }
}
