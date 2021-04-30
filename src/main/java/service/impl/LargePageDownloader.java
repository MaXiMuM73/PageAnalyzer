package service.impl;

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

public class LargePageDownloader {
    private static final Logger logger = Logger.getLogger(LargePageDownloader.class);
    private static String pageFileAbsolutePath;
    private static FileWriter wordsFileWriter;
    private static String wordsFileAbsolutePath;
    private static final String TEG_DIV_OPEN = "<div";
    private static final String TEG_DIV_CLOSE = "</div>";

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

    public static boolean downloadPageByUrlAndSaveStatistics(String url) {
        try {
            downloadPageToFile(url);
            writeAllWordsInPageToFile(pageFileAbsolutePath);
            uniqueWords(wordsFileAbsolutePath);
        } catch (MalformedURLException malformedURLException) {
            logger.error("URL is incorrect.", malformedURLException);
            return false;
        } catch (UnknownHostException unknownHostException) {
            logger.error("Internet connection unavailable.", unknownHostException);
            return false;
        } catch (IOException ioException) {
            logger.error("Error saving the file", ioException);
            return false;
        }
        return true;
    }

    public static boolean downloadPageByFileAndSaveStatistics(String pageFileAbsolutePath) {
        try {
            writeAllWordsInPageToFile(pageFileAbsolutePath);
            uniqueWords(wordsFileAbsolutePath);
        } catch (IOException ioException) {
            logger.error("Error saving the file", ioException);
            return false;
        }
        return true;
    }

    private static void downloadPageToFile(String url) throws IOException {
        URL pageUrl;
        String line;
        pageUrl = new URL(url);
        URLConnection connection = pageUrl.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        Path pagePath = Files.createDirectories(Paths.get("files/" + pageUrl.getHost()));
        String pageTitle = Jsoup.connect(url).get().title();
        File pageFile = new File(pagePath + "/" + pageTitle
                .replaceAll("['/:*?\"<>|]", "_")
                + ".html");
        pageFileAbsolutePath = pageFile.getAbsolutePath();
        BufferedWriter out = new BufferedWriter(new FileWriter(pageFile));
        while ((line = in.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            out.write(line + "\n");
        }
        in.close();
        out.close();
    }

    private static void writeAllWordsInPageToFile(String pageFilePath) throws IOException {
        File pageFile = new File(pageFilePath);
        File wordsFile = new File(pageFile.getParent() + "/" + pageFile.getName() + "_words.txt");
        wordsFileWriter = new FileWriter(wordsFile);
        BufferedReader pageFileReader = new BufferedReader(new FileReader(pageFile));
        String line;
        StringBuilder linesWithTeg = new StringBuilder();
        while ((line = pageFileReader.readLine()) != null) {
            if (line.contains(TEG_DIV_OPEN)) {
                linesWithTeg.append(line).append(" ");
                if (line.contains(TEG_DIV_CLOSE))
                {
                    writeWordsInLineToFile(linesWithTeg);
                    linesWithTeg.delete(0, linesWithTeg.length());
                    continue;
                }
                while ((line = pageFileReader.readLine()) != null) {
                    linesWithTeg.append(line).append(" ");
                    if (line.contains(TEG_DIV_CLOSE)) {
                        break;
                    }
                }
                writeWordsInLineToFile(linesWithTeg);
                linesWithTeg.delete(0, linesWithTeg.length());
            }
        }
        pageFileReader.close();
        wordsFileWriter.close();
        wordsFileAbsolutePath = wordsFile.getAbsolutePath();
    }

    private static void writeWordsInLineToFile(StringBuilder textInLine) throws IOException {
        String text;
        text = Jsoup.parse(String.valueOf(textInLine), "test").text();
        if (!text.equals("") & !text.equals("\uFEFF")) {
            ArrayList<String> words = new ArrayList<>(Arrays.asList(text.split("[ ,.!?:;'#/()–—‑{}«»\"-]")));
            words.removeIf(s -> s.equals(""));
            for (String s : words) {
                wordsFileWriter.write(s + "\n");
                wordsFileWriter.flush();
            }
            words.clear();
        }
    }

    private static void uniqueWords(String wordsPath) throws IOException {
        File wordsFile = new File(wordsPath);
        File statisticsFile = new File(wordsFile.getParent() + "/" + wordsFile.getName()
                .replaceAll("_words.txt", "_statistics.txt"));
        BufferedReader bufferedReader = new BufferedReader(new FileReader(wordsFile));
        FileWriter fileWriter = new FileWriter(statisticsFile);
        String line;
        long repeat;
        while ((line = bufferedReader.readLine()) != null) {
            String word = line;
            repeat = Files.lines(Paths.get(statisticsFile.getPath()))
                    .filter(s -> Arrays.asList(s.split(" ")).contains(word))
                    .count();
            if (repeat != 0) continue;
            long count = Files.lines(Paths.get(wordsFile.getPath()))
                    .filter(s -> s.equals(word))
                    .count();
            System.out.println(line + " : " + count);
            fileWriter.write(line + " : " + count + "\n");
            fileWriter.flush();
        }
        fileWriter.close();
        bufferedReader.close();
    }
}