package com.mahe.soft.stock.db.ingest;

import com.mahe.soft.stock.db.service.StockService;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class BulkIngestService {

    private final StockService stockService;

    public String processBulkIngestion(String rootPathStr) {
        Path rootPath = Paths.get(rootPathStr);
        if (!Files.exists(rootPath) || !Files.isDirectory(rootPath)) {
            return "Root path does not exist or is not a directory: " + rootPathStr;
        }

        Path unzipDir = rootPath.resolve("unzip");
        try {
            Files.createDirectories(unzipDir);
        } catch (IOException e) {
            log.error("Failed to create unzip directory", e);
            return "Failed to create unzip directory: " + e.getMessage();
        }

        int processedFiles = 0;
        int totalRecords = 0;

        try (Stream<Path> stream = Files.walk(rootPath, 1)) { // Shallow walk to find zips in root
            // 1. Find and Unzip
            stream.filter(path -> path.toString().endsWith(".zip"))
                    .forEach(zipPath -> unzipFile(zipPath, unzipDir));

            // 2. Scan unzip folder and ingest
            try (Stream<Path> extractedStream = Files.walk(unzipDir)) {
                File[] files = extractedStream.filter(Files::isRegularFile).map(Path::toFile).toArray(File[]::new);

                for (File file : files) {
                    // Basic check for csv-like content or extension, but user requirement "unzip
                    // all files and store... create ingestion process"
                    // Assuming all extracted files are CSVs
                    try (Reader reader = new BufferedReader(
                            new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                        log.info("Processing file: {}", file.getName());
                        int count = stockService.saveFromReader(reader);
                        totalRecords += count;
                        processedFiles++;
                    } catch (Exception e) {
                        log.error("Failed to process file: {}", file.getName(), e);
                    }
                }
            }

        } catch (IOException e) {
            log.error("Error during bulk ingestion", e);
            return "Error during ingestion: " + e.getMessage();
        }

        return String.format("Completed. Processed %d files with total %d records.", processedFiles, totalRecords);
    }

    private void unzipFile(Path zipPath, Path destDir) {
        log.info("Unzipping: {}", zipPath);
        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path entryPath = destDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (InputStream is = zipFile.getInputStream(entry)) {
                        Files.copy(is, entryPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed to unzip: " + zipPath, e);
        }
    }
}
