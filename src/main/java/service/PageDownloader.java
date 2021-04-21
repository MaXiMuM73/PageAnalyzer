package service;

import model.Page;

public interface PageDownloader {
    Page getPage(String url);
}
