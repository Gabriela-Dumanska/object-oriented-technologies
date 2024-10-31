package app;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import model.Photo;
import util.PhotoDownloader;
import util.PhotoProcessor;
import util.PhotoSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhotoCrawler {

    private static final Logger log = Logger.getLogger(PhotoCrawler.class.getName());

    private final PhotoDownloader photoDownloader;

    private final PhotoSerializer photoSerializer;

    private final PhotoProcessor photoProcessor;

    public PhotoCrawler() throws IOException {
        this.photoDownloader = new PhotoDownloader();
        this.photoSerializer = new PhotoSerializer("./photos");
        this.photoProcessor = new PhotoProcessor();
    }

    public void resetLibrary() throws IOException {
        photoSerializer.deleteLibraryContents();
    }

    public void downloadPhotoExamples() {
        try {
            photoDownloader.getPhotoExamples()
                .subscribe(photoSerializer::savePhoto);

        } catch (IOException e) {
            log.log(Level.SEVERE, "Downloading photo examples error", e);
        }
    }

    public void downloadPhotosForQuery(String query) throws IOException {
        try {
            photoDownloader.searchForPhotos(query)
                    .subscribe(photoSerializer::savePhoto,
                            error -> log.log(Level.SEVERE, "Problems with DuckDuckGo :("),
                            () -> log.log(Level.INFO, "Downloading completed :)"));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadPhotosForMultipleQueries(List<String> queries) throws IOException {
        List<Observable<Photo>> observables = new ArrayList<>();
        try{
            for(String query : queries) {
                observables.add(photoDownloader.searchForPhotos(query)
                        .subscribeOn(Schedulers.io()));
            }
            Observable.merge(observables)
                    .subscribe(photoSerializer::savePhoto,
                            error -> log.log(Level.SEVERE, "Problems with DuckDuckGo :("),
                            () -> log.log(Level.INFO, "Downloading completed :)"));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
