package com.ledgersync.platform.config;

import com.ledgersync.platform.batch.SettlementItemReader;
import com.ledgersync.platform.batch.SettlementProcessor;
import com.ledgersync.platform.model.SettlementEntry;
import com.ledgersync.platform.model.dto.SettlementEntryDto;
import com.ledgersync.platform.repository.SettlementEntryRepository;
import com.ledgersync.platform.service.parser.impl.HblExcelParser;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class HblIngestionJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final HblExcelParser hblExcelParser;
    private final SettlementProcessor settlementProcessor;
    private final SettlementEntryRepository settlementEntryRepository;

    private final com.ledgersync.platform.batch.JobIntegrityListener jobIntegrityListener;

    public HblIngestionJobConfig(JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            HblExcelParser hblExcelParser,
            SettlementProcessor settlementProcessor,
            SettlementEntryRepository settlementEntryRepository,
            com.ledgersync.platform.batch.JobIntegrityListener jobIntegrityListener) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.hblExcelParser = hblExcelParser;
        this.settlementProcessor = settlementProcessor;
        this.settlementEntryRepository = settlementEntryRepository;
        this.jobIntegrityListener = jobIntegrityListener;
    }

    @Bean
    public Job hblIngestionJob() {
        return new JobBuilder("hblIngestionJob", jobRepository)
                .listener(jobIntegrityListener)
                .start(ingestionStep())
                .build();
    }

    @Bean
    public Step ingestionStep() {
        return new StepBuilder("ingestionStep", jobRepository)
                .<SettlementEntryDto, SettlementEntry>chunk(100, transactionManager)
                .reader(itemReader(null))
                .processor(settlementProcessor)
                .writer(itemWriter())
                .listener(jobIntegrityListener)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<SettlementEntryDto> itemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        if (filePath == null) {
            // For validation or testing when parameter is missing
            return new SettlementItemReader(hblExcelParser, new ByteArrayResource(new byte[0]));
        }
        return new SettlementItemReader(hblExcelParser, new FileSystemResource(filePath));
    }

    @Bean
    public RepositoryItemWriter<SettlementEntry> itemWriter() {
        return new RepositoryItemWriterBuilder<SettlementEntry>()
                .repository(settlementEntryRepository)
                .methodName("save")
                .build();
    }
}
