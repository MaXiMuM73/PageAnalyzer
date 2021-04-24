package dao;

import model.Page;

import java.util.List;

public interface PageDAO {
    List<Page> findAll();
    List<String> findAllWords();
    Page findById(Long id);
    Page findByUrl(String url);
    boolean create(Page page);
    boolean deleteById(Long id);
}