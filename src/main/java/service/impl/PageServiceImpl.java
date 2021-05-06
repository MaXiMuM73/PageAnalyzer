package service.impl;

import dao.PageDAO;
import dao.PageDAOImpl;
import manager.PageParser;
import manager.impl.PageParserImpl;
import model.Page;
import service.PageService;

import java.util.*;

public class PageServiceImpl implements PageService {
    private static final PageParser pageParser = new PageParserImpl();
    private static final String DB_URL = "jdbc:h2:./db";
    private static final PageDAO pageDAO = new PageDAOImpl(DB_URL);

    @Override
    public Page getPage(String url) {
        return pageParser.getPageByUrl(url);
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

    @Override
    public boolean saveLargePageByUrl(String url) {
        return pageParser.saveToFiles(url);
    }
}