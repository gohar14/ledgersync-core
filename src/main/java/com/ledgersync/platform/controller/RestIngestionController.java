package com.ledgersync.platform.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ingest")
public class RestIngestionController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RestIngestionController.class);

    private final JobLauncher jobLauncher;
    private final org.springframework.batch.core.Job hblIngestionJob;

    public RestIngestionController(JobLauncher jobLauncher, org.springframework.batch.core.Job hblIngestionJob) {
        this.jobLauncher = jobLauncher;
        this.hblIngestionJob = hblIngestionJob;
    }

    @PostMapping("/hbl")
    public ResponseEntity<String> ingestHblFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            // Save file to temp directory
            Path tempDir = Files.createTempDirectory("ledgersync_ingest_");
            Path tempFile = tempDir.resolve("hbl_" + UUID.randomUUID() + ".xlsx");
            file.transferTo(tempFile);

            // Launch Job Async
            CompletableFuture.runAsync(() -> {
                try {
                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString("filePath", tempFile.toAbsolutePath().toString())
                            .addLong("startedAt", System.currentTimeMillis())
                            .toJobParameters();

                    jobLauncher.run(hblIngestionJob, jobParameters);
                } catch (Exception e) {
                    log.error("Job execution failed", e);
                }
            });

            return ResponseEntity.accepted().body("File accepted for processing. Job triggered.");

        } catch (IOException e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.internalServerError().body("Failed to upload file");
        }
    }
}
