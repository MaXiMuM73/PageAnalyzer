package model;

import lombok.Data;

import java.util.*;

/**
 * Page, which application load
 */
@Data
public class Page extends BaseModel {
    /**
     * URL of page
     */
    private String url;

    /**
     * Page name
     */
    private String pageName;

    /**
     * Words belonging to page
     */
    private Map<String, Integer> words = new LinkedHashMap<>();

    public Page() {}
    public Page(Long id, String pageName, String url) {
        super.setId(id);
        this.pageName = pageName;
        this.url = url;
    }

    /**
     * Displays the word statistics of the received page in the console
     */
    public void print() {
        for (Map.Entry<String, Integer> pair : this.words.entrySet()) {
            String key = pair.getKey();
            Integer value = pair.getValue();
            System.out.println(key + " - " + value);
        }
    }

    @Override
    public String toString() {
        return "Page{id=" + super.getId() +
                ", url='" + url + '\'' +
                ", PageName='" + pageName + "'}";
    }
}