package service;

import model.Page;

import java.io.File;

public interface PageDownloader {
    Page getPageByUrl(String url);
    Page getPageByFile(File file);
}
