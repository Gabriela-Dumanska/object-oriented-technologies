package app;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CrawlerApp {

    public static final String SCRAPER_API_KEY = "11e4f3cda35b231d9e4058a970625f85";

    private static final List<String> TOPICS = List.of("cat", "rat", "dog", "hamster");


    public static void main(String[] args) throws IOException, InterruptedException {
        PhotoCrawler photoCrawler = new PhotoCrawler();
        photoCrawler.resetLibrary();
        //photoCrawler.downloadPhotoExamples();
        //photoCrawler.downloadPhotosForQuery("rats");
        photoCrawler.downloadPhotosForMultipleQueries(TOPICS);

        Thread.sleep(100_000);
    }
}