package service.impl;

import dao.PageDAO;
import dao.PageDAOImpl;
import model.Page;
import service.PageService;

import java.util.*;

public class PageServiceImpl implements PageService {
    private static final PageDownloaderImpl pageDownloader = new PageDownloaderImpl();
    private static final String DB_URL = "jdbc:h2:./db";
    private static final PageDAO pageDAO = new PageDAOImpl(DB_URL);

    @Override
    public Page getPage(String url) {
        return pageDownloader.getPageByUrl(url);
    }

    @Override
    public boolean saveToHDD() {
        return pageDownloader.savePageOnHDD();
    }

    @Override
    public boolean saveToDataBase(Page page) {
        return pageDAO.create(page);
    }

    @Override
    public List<Page> findAll() {
        return pageDAO.findAll();
    }

    @Override
    public List<String> findAllWords() {
        return pageDAO.findAllWords();
    }

    @Override
    public boolean deleteById(Long id) {
        return pageDAO.deleteById(id);
    }
}
