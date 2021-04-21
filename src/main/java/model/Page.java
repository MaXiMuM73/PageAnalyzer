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

    @Override
    public String toString() {
        return "Page{id=" + super.getId() +
                ", url='" + url + '\'' +
                ", PageName='" + pageName + "'}";
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
}























//    private String getPageAsString(String pageURL) {
//        try {
//            String line;
//            URL url = new URL(pageURL);
//            BufferedReader bufferedReader =
//                    new BufferedReader(new InputStreamReader(url.openStream()));
//            StringBuilder stringBuilder = new StringBuilder();
//            while((line = bufferedReader.readLine()) != null) {
//                stringBuilder.append(line);
//            }
//            return stringBuilder.toString();
//        } catch (MalformedURLException malformedURLException) {
//            System.out.println("Exception: " + malformedURLException.toString());
//            return "Incorrect page";
//        } catch (IOException ioException) {
//            System.out.println("Exception: " + ioException.toString());
//            return "Incorrect content";
//        }
//    }
//
//    private ArrayList<String> getArrayListOfWords(String content) {
//        ArrayList<String> arrayListOwWords = new ArrayList<>();
//
//        Document htmlPage = Jsoup.parse(content);
//        //System.out.println(htmlPage);
//        Elements allParagraphs = htmlPage.getAllElements();
//        for(Element e : allParagraphs) {
//            arrayListOwWords.addAll(Arrays
//                    .asList(e.text()
//                            .split("[ |\\n\\r—,.?!/()\"-]+")));
//        }
//        return arrayListOwWords;
//    }
