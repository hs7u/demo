package com.batch.demo.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.batch.demo.batch.util.processor.ProcessorUtil;
import com.batch.demo.batch.util.writer.RepoWriterUtil;
import com.batch.demo.vo.vo.Emp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableBatchProcessing
public class SingleFilePartitionedJob {
    
    @Autowired
	private JobRepository jobRepository;
	@Autowired
	private PlatformTransactionManager platformTransactionManager;
    @Autowired
    private ResourcePatternResolver resourcePatternResolver;
    @Value("${split-file-pattern:split/*.csv}")
    private String splitFilePattern;
    @Value("${file-to-split}")
    private Resource resource;
    @Autowired
    private ProcessorUtil<Emp> processor;
    @Autowired
    private RepoWriterUtil<Emp, UUID> writer;

    // 按設定的行數拆分文件
    public int splitCsvFiles(File bigFile, int maxRows) throws IOException {    
        int fileCount = 1;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(bigFile.getPath()))) {
            String line = null;
            int lineNum = 1;
            Path splitFile = Paths.get(bigFile.getParent() + "/" + fileCount + "split.csv");
            BufferedWriter writer = Files.newBufferedWriter(splitFile, StandardOpenOption.CREATE);
    
            while ((line = reader.readLine()) != null) {
    
                if (lineNum > maxRows) {
                    writer.close();
                    lineNum = 1;
                    fileCount++;
                    splitFile = Paths.get("split/" + fileCount + "split.csv");
                    writer = Files.newBufferedWriter(splitFile, StandardOpenOption.CREATE);
                }
    
                writer.append(line);
                writer.newLine();
                lineNum++;
            }
            writer.close();
        }
    
        return fileCount;
    }

    // 文件拆分Tasklet
    @Bean
    public Tasklet splitFiles(){
        return (StepContribution contribution, ChunkContext chunkContext) -> {
            int count = splitCsvFiles(resource.getFile(), 100);
            log.info("File was split on {} files", count);
            return RepeatStatus.FINISHED;
        };
            
    }

    @Bean
    public Step splitFileStep() throws IOException {
        return new StepBuilder("splitFile", jobRepository)
                .tasklet(splitFiles(), platformTransactionManager)
                .build();
    }

    @Bean
    public Step csvToDbSlaveStep() throws MalformedURLException {
        return new StepBuilder("csvToDbStep", jobRepository)
                .<Emp, Emp>chunk(50, platformTransactionManager)
                .reader(csvReaderSplitted(null))
                .processor(processor)
                .writer(writer)
                .build();

    }        

    @Bean
    @StepScope
    public FlatFileItemReader<Emp> csvReaderSplitted(@Value("#{stepExecutionContext[fileName]}") String fileName) throws MalformedURLException {
        return new FlatFileItemReaderBuilder<Emp>()
                .name("csvReaderSplitted")
                .resource(new UrlResource(fileName))
                .delimited()
                .names(new String[]{"empName","password"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Emp>() {{
                    setTargetType(Emp.class);
                }})
                .build();

    }

    @Bean
    public TaskExecutor splitTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(30);
        taskExecutor.setCorePoolSize(25);
        taskExecutor.setThreadNamePrefix("cust-job-exec2-");
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    // 依命名規則拿取拆分後所有的文件，把文件的URL 暫存進ExecutionContext，並給予執行緒
    @Bean
    public Partitioner partitioner() {
        MultiResourcePartitioner multiResourcePartitioner = new MultiResourcePartitioner();
    
        return (int gridSize) -> {
            try {
                Resource[] resources = resourcePatternResolver.getResources(splitFilePattern);
                multiResourcePartitioner.setResources(resources);
                return multiResourcePartitioner.partition(gridSize);
    
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Bean
    public Step csvToDbMasterStep() throws IOException {        
        return new StepBuilder("csvReaderMasterStep", jobRepository)
                .partitioner("csvReaderMasterStep", partitioner())
                .gridSize(10)
                .step(csvToDbSlaveStep())
                .taskExecutor(splitTaskExecutor())
                .build();
    }

    @Bean
    public Job splitFileJob() throws IOException {
        return new JobBuilder("splitFileJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(splitFileStep())
                .next(csvToDbMasterStep())
                .end()
                .build();
    }
    
}
