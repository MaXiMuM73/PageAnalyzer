package manager.impl;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

public class LargePageParser {
    private static final Logger logger = Logger.getLogger(LargePageParser.class);
    public String url;
    public static String path = "files/";
    private URLConnection connection;
    private static final String[] TAGS_OPEN = {"<div", "<p", "<head>", "<li", "<a", "<span", "<button"};
    private static final String[] TAGS_CLOSE = {"</div>", "</p>", "</head>", "</li>", "</a>", "</span>", "</button>"};

    static {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509ExtendedTrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] xcs, String string, Socket socket) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] xcs, String string, Socket socket) throws CertificateException {

                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException {

                    }

                }
        };

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, trustAllCerts, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public LargePageParser(String url) {
        this.url = url;
    }

    public static void setPath(String path) {
        LargePageParser.path = path;
    }

    public static String getPath() {
        return path;
    }

    public static boolean saveAll(String url) {
        try {
            LargePageParser largePageParser = new LargePageParser(url);
            File page = largePageParser.save(url);
            File words = largePageParser.saveWords(page);
            File statistics = largePageParser.saveStatistics(words);
            largePageParser.print(statistics);
            return true;
        } catch (MalformedURLException malformedURLException) {
            logger.error("URL's incorrect. Please, Check the page URL. For example: https://www.yandex.ru.",
                    malformedURLException);
            return false;
        } catch (UnknownHostException unknownHostException) {
            logger.error("Internet connection is unavailable.", unknownHostException);
            return false;
        }
        catch (IOException ioException) {
            logger.error("Page loading error.", ioException);
            return false;
        }
    }

    public File save(String url) throws IOException {
        BufferedReader reader = connect(url);
        String line;
        Path pagePath = Files.createDirectories(Paths.get(path + connection.getURL().getHost()));
        String pageTitle = Jsoup.connect(url).get().title();
        File pageFile = new File(pagePath + "/" + pageTitle
                .replaceAll("['/:*?\"<>|]", "_")
                + ".html");
        BufferedWriter writer = new BufferedWriter(new FileWriter(pageFile));
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            writer.write(line + "\n");
        }
        reader.close();
        writer.close();
        return pageFile;
    }

    public File save(File file) throws IOException {
        Path pagePath = Files.createDirectories(Paths.get(path + file.getName()));
        String line;
        File pageFile = new File(pagePath + "/" + file.getName()
                .replaceAll("['/:*?\"<>|]", "_"));
        BufferedReader reader = new BufferedReader(new FileReader(file));
        BufferedWriter writer = new BufferedWriter(new FileWriter(pageFile));
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            writer.write(line + "\n");
        }
        reader.close();
        writer.close();
        return pageFile;
    }

    public File saveWords(File pageFile) throws IOException {
        File wordsFile = new File(pageFile.getParent() + "/" + pageFile.getName() + "_words.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(wordsFile));
        BufferedReader reader = new BufferedReader(new FileReader(pageFile));
        String line;
        StringBuilder linesWithTeg = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (tagContain(line, TAGS_OPEN)) {
                linesWithTeg.append(line).append(" ");
                if (tagContain(line, TAGS_CLOSE)) {
                    writeWordsInLineToFile(writer, linesWithTeg);
                    continue;
                }
                while ((line = reader.readLine()) != null) {
                    linesWithTeg.append(line).append(" ");
                    if (tagContain(line, TAGS_CLOSE)) {
                        break;
                    }
                }
                writeWordsInLineToFile(writer, linesWithTeg);
            }
        }
        reader.close();
        writer.close();
        return wordsFile;
    }

    public File saveStatistics(File wordsFile) throws IOException {
        File statisticsFile = new File(wordsFile.getParent() + "/" + wordsFile.getName()
                .replaceAll("_words.txt", "_statistics.txt"));
        BufferedWriter writer = new BufferedWriter(new FileWriter(statisticsFile));
        BufferedReader reader = new BufferedReader(new FileReader(wordsFile));
        String word;
        long count;
        long countInStat;
        long countWords = 0;
        while ((word = reader.readLine()) != null) {
            countInStat = count(statisticsFile, word);
            if (countInStat != 0) continue;
            count = count(wordsFile, word);
            writer.write(word + " : " + count + "\n");
            writer.flush();
            countWords++;
        }
        writer.write("Count of words: " + countWords);
        reader.close();
        writer.close();
        return statisticsFile;
    }

    public void print(File statisticsFile) throws IOException {
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(statisticsFile));
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();
    }

    private void writeWordsInLineToFile(BufferedWriter writer, StringBuilder lines) {
//        String text;
//        text = Jsoup.parse(String.valueOf(linesWithTeg), "test").text();
//        if (!text.equals("") & !text.equals("\uFEFF")) {
//            ArrayList<String> words = new ArrayList<>(Arrays.asList(text.split("[ ,.!?:;'#/()–—‑{}«»\"-]")));
//            words.removeIf(s -> s.equals(""));
//            for (String s : words) {
//                writer.write(s + "\n");
//                writer.flush();
//            }
//            words.clear();
//        }
        parseLines(lines).forEach(s -> {
            try {
                writer.write(s + "\n");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        lines.delete(0, lines.length());
    }

    private ArrayList<String> parseLines(StringBuilder lines) {
        String text;
        text = Jsoup.parse(String.valueOf(lines), "test").text();
        ArrayList<String> words = new ArrayList<>();
        if (!text.equals("") & !text.equals("\uFEFF")) {
            words = new ArrayList<>(Arrays.asList(text.split("[ ,.!?:;'#/()–—‑{}«»\"-]")));
            words.removeIf(s -> s.equals(""));
        }
        return words;
    }

    private boolean tagContain(String line, String[] tags) {
        for (String tag : tags) {
            if (line.contains(tag)) return true;
        }
        return false;
    }

    private BufferedReader connect(String url) throws IOException {
        URL pageUrl = new URL(url);
            connection = pageUrl.openConnection();
            return new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

    private long count(File file, String word) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        long count = reader
                .lines()
                .filter(s -> Arrays.asList(s.split(" "))
                        .get(0)
                        .equals(word))
                .count();
        reader.close();
        return count;
    }
}