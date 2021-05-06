package manager.impl;

import manager.PageParser;
import model.Page;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PageParserImpl implements PageParser {
    private static final Logger logger = Logger.getLogger(PageParserImpl.class);
    public static String path = "pages/";

    public static void setPath(String path) {
        PageParserImpl.path = path;
    }

    public static String getPath() {
        return path;
    }

    @Override
    public Page getPageByUrl(String url) {
        Document htmlPage = connectToUrl(url);
        Page page = createPage(htmlPage);
        if (htmlPage != null) save(page, htmlPage);
        return page;
    }

    @Override
    public Page getPageByFile(File file) {
        Document htmlPage = connectToFile(file);
        Page page = createPage(htmlPage);
        save(page, htmlPage);
        return page;
    }

    @Override
    public boolean saveToFiles(String url) {
        return LargePageParser.saveAll(url);
    }

    private void save(Page page, Document htmlPage) {
        if (page == null) return;
        try {
            logger.info("Page saving started.");
            String pageFile = createPageFile(page);
            File f = new File(pageFile);
            FileUtils.writeStringToFile(f, htmlPage.outerHtml(), StandardCharsets.UTF_8);
            logger.info("Page is saved to the hard disk. Check the folder /pages.");
        } catch (IOException ioException) {
            logger.error("Page not saved.", ioException);
        }
    }

    private Document connectToUrl(String url) {
        try {
            logger.info("Connecting to a page: " + url + ".");
            return Jsoup.connect(url).maxBodySize(0).userAgent("Chrome").get();
        } catch (IllegalArgumentException illegalArgumentException) {
            logger.error("URL's incorrect. Please, Check the page URL. For example: https://www.yandex.ru.",
                    illegalArgumentException);
            return null;
        } catch (UnknownHostException unknownHostException) {
            logger.error("Internet connection unavailable.", unknownHostException);
            return null;
        }
        catch (IOException ioException){
            logger.error("Page loading error.", ioException);
            return null;
        }
    }

    private Document connectToFile(File file) {
        //page = null;
        logger.info("Connecting to a page: " + file.getPath() + ".");
        try {
            Document htmlPage = Jsoup.parse(file, null);
            //createPage(htmlPage);
            return htmlPage;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
    }

    private Page createPage(Document htmlPage) {
        Page page = new Page();
        if (htmlPage!=null) {
            String pageName = htmlPage.title()
                    .replaceAll("['/:*?\"<>|]", "_");
            page.setPageName(pageName);
            page.setUrl(htmlPage.location());
            ArrayList<String> words = getWordsFromDocument(htmlPage);
            Map<String, Integer> wordsStatistics = getMapOfWordsFromWords(words);
            page.setWords(wordsStatistics);
            logger.info("Page loaded.");
        }
        return page;
    }

    private ArrayList<String> getWordsFromDocument(Document htmlPage) {
        ArrayList<String> words = new ArrayList<>();
        if (htmlPage != null) {
            words = new ArrayList<>(Arrays.asList(htmlPage
                    .text()
                    .split("[ ,.!?:;'#/()–—‑{}«»\"-]")));
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

    private String createPageFile(Page page) {
        String pageFileName = page.getPageName()
                .replaceAll("[/:*?\"<>|]","_");
        File pagesFolder = new File(path);
        pagesFolder.mkdir();
        return pagesFolder.getAbsolutePath() + "/" + pageFileName + ".html";
    }
}