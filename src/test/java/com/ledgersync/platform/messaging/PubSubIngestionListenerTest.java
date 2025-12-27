package com.ledgersync.platform.messaging;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PubSubIngestionListenerTest {

    @Mock
    private PubSubTemplate pubSubTemplate;
    @Mock
    private JobLauncher jobLauncher;
    @Mock
    private Job hblIngestionJob;
    @Mock
    private BasicAcknowledgeablePubsubMessage message;

    private PubSubIngestionListener listener;

    @BeforeEach
    void setUp() {
        listener = new PubSubIngestionListener(pubSubTemplate, jobLauncher, hblIngestionJob, "test-sub");
    }

    @Test
    void testSubscribe() {
        listener.subscribe();
        verify(pubSubTemplate).subscribe(eq("test-sub"), any());
    }

    @Test
    void testMessageConsumerTrigger() throws Exception {
        // Given
        String filePath = "/tmp/test.xlsx";
        PubsubMessage pubsubMsg = PubsubMessage.newBuilder()
                .setData(ByteString.copyFromUtf8(filePath))
                .build();

        when(message.getPubsubMessage()).thenReturn(pubsubMsg);
        when(jobLauncher.run(eq(hblIngestionJob), any(JobParameters.class))).thenReturn(new JobExecution(1L));

        // When
        ReflectionTestUtils.invokeMethod(listener, "messageConsumer", message);

        // Then
        verify(jobLauncher).run(eq(hblIngestionJob), any(JobParameters.class));
        verify(message).ack();
    }
}
