package manager;

import model.Page;
import org.jsoup.nodes.Document;

import java.io.File;

public interface PageParser {
    Page getPageByUrl(String url);
    Page getPageByFile(File file);
    boolean saveToFiles(String url);
}