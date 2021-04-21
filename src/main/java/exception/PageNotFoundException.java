package exception;

public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException(Long id) {
        System.out.println("Page with id: " + id + " not found");
    }
    public PageNotFoundException(String url) {
        System.out.println("Page with url: " + url + " not found");
    }
}
