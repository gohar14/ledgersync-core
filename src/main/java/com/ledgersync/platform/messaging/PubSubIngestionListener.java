package com.ledgersync.platform.messaging;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.pubsub.v1.PubsubMessage;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PubSubIngestionListener {

    private static final Logger log = LoggerFactory.getLogger(PubSubIngestionListener.class);

    private final PubSubTemplate pubSubTemplate;
    private final JobLauncher jobLauncher;
    private final Job hblIngestionJob;
    private final String subscriptionName;

    public PubSubIngestionListener(PubSubTemplate pubSubTemplate,
            JobLauncher jobLauncher,
            Job hblIngestionJob,
            @Value("${ledgersync.pubsub.subscription:ingestion-subscription}") String subscriptionName) {
        this.pubSubTemplate = pubSubTemplate;
        this.jobLauncher = jobLauncher;
        this.hblIngestionJob = hblIngestionJob;
        this.subscriptionName = subscriptionName;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void subscribe() {
        log.info("Starting Pub/Sub listener for subscription: {}", subscriptionName);
        try {
            pubSubTemplate.subscribe(subscriptionName, this::messageConsumer);
        } catch (Exception e) {
            log.warn("Failed to subscribe to Pub/Sub (likely due to missing credentials or emulator): {}",
                    e.getMessage());
        }
    }

    private void messageConsumer(BasicAcknowledgeablePubsubMessage message) {
        PubsubMessage pubsubMessage = message.getPubsubMessage();
        String filePath = pubsubMessage.getData().toString(StandardCharsets.UTF_8);
        log.info("Received ingestion request via Pub/Sub for file: {}", filePath);

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("filePath", filePath)
                    .addString("source", "PUBSUB")
                    .addLong("startedAt", System.currentTimeMillis())
                    .addString("uuid", UUID.randomUUID().toString()) // Ensure uniqueness
                    .toJobParameters();

            jobLauncher.run(hblIngestionJob, jobParameters);
            message.ack();
            log.info("Job triggered and message acknowledged.");
        } catch (Exception e) {
            log.error("Failed to process message or trigger job", e);
            message.nack();
        }
    }
}
