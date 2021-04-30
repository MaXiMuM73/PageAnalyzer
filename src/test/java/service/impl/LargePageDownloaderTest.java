package service.impl;

import org.junit.Test;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

public class LargePageDownloaderTest {
    private static final String badUrl = "test";
    private static final String pageFileAbsolutePath = String.valueOf(
            Paths.get("src/test/pages/big.html").toAbsolutePath());

    @Test
    public void givenBadUrl_whenDownloadPageByUrlAndSaveStatistics_thenReturnFalse() {
        assertThat(LargePageDownloader.downloadPageByUrlAndSaveStatistics(badUrl)).isFalse();
    }
}