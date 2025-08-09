import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class MultiThreadedFileDownloader {
    private static final String URL_PATH = "https://github.com/club-de-programacion-competitiva/libros/raw/refs/heads/master/Cracking-the-Coding-Interview-6th-Edition-189-Programming-Questions-and-Solutions.pdf";
    private static final String DOWNLOAD_PATH = "C:\\Users\\govin\\Downloads\\Cracking the coding interview.pdf";
    private static final String FILE_MODE = "rw";

    static class DownloadTask implements Supplier<Void> {
        long start;
        long end;
        int id;

        DownloadTask(long start, long end, int id) {
            this.start = start;
            this.end = end;
            this.id = id;
        }

        @Override
        public Void get() {
            URL url;
            HttpURLConnection conn = null;
            try {
                url = new URL(URL_PATH);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Range", "bytes="+ start + "-" + end);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.printf("Thread %d downloading file from %d to %d\n", id, start, end);

            try(InputStream iStream = conn.getInputStream();
                RandomAccessFile oStream = new RandomAccessFile(DOWNLOAD_PATH, FILE_MODE)) {
                oStream.seek(start);
                byte[] bytes = new byte[8192];
                int bytesRead = -1;
                while ((bytesRead = iStream.read(bytes)) != -1)
                    oStream.write(bytes, 0, bytesRead);
            } catch (IOException e) {
                System.out.println("Exception occurred: " + e.getMessage());
            }
            return null;
        }
    }

    public static void downloadFileFromUrl() throws IOException {
        long s = System.currentTimeMillis();
        URL url = new URL(URL_PATH);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        long fileSize = conn.getContentLength();
        conn.disconnect();

        RandomAccessFile file = new RandomAccessFile(DOWNLOAD_PATH, FILE_MODE);
        file.setLength(fileSize);
        file.close();

        int numThreads = 2;
        long chunkSize = fileSize/numThreads;
        List<CompletableFuture<Void>> f = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        for(int i = 0 ; i < numThreads ; i++) {
            long start = i * chunkSize;
            long end = i == numThreads-1 ? fileSize-1 : start + chunkSize - 1;
            f.add(CompletableFuture.supplyAsync(new DownloadTask(start, end, i), executorService));
        }

        CompletableFuture.allOf(f.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
        long e = System.currentTimeMillis();

        System.out.println("Total time taken to download file from URL: " + ((e-s)*1.0/1000));
    }

    public static void main(String[] args) {
        try {
            downloadFileFromUrl();
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        System.out.println("File downloaded successfully");
    }
}