package com.batch.demo.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ResourceUtils;

import com.batch.demo.batch.listener.JobListener;
import com.batch.demo.batch.listener.RetryAndSkipListener;
import com.batch.demo.batch.listener.StepListener;
import com.batch.demo.batch.util.processor.ProcessorUtil;
import com.batch.demo.batch.util.reader.FlatFileItemReaderUtil;
import com.batch.demo.batch.util.reader.settings.EmpReaderSetting;
import com.batch.demo.batch.util.writer.RepoWriterUtil;
import com.batch.demo.repository.BaseRepository;
import com.batch.demo.vo.vo.Emp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {
    
	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private PlatformTransactionManager platformTransactionManager;
	@Autowired
    private BaseRepository<Emp, UUID> empRepo;
	@Value("${file.input.path3}") 
	private String filePath;

	// showing error log example
	// @Bean("empWriter")
	// public ItemWriter<Emp> crWriter(){
	// 	return new ItemWriter<Emp>() {
	// 		@Override
	// 		public void write(Chunk<? extends Emp> chunk) {
	// 			for(Emp p:chunk){
	// 				if(p.getEmpName().equals("errorData")){
	// 					throw new IllegalArgumentException();
	// 				}
	// 			}
	// 			empRepo.saveAll(chunk);
	// 		}			
	// 	};
	// }
	@Bean
	public Tasklet deleteOldDataTasklet(){
		return (StepContribution contribution, ChunkContext chunkContext) -> {
			log.info("=====>>> before delete has {} rows: ", empRepo.getCountAllEmp());
			empRepo.deleteAllEmp();
			log.info("=====>>> after delete has {} rows: ", empRepo.getCountAllEmp());
			return RepeatStatus.FINISHED;
		};
	}
    // Create Step1
	@Bean
    public Step deleteAllStep(){
		return new StepBuilder("deleteAllStep", jobRepository)
			.tasklet(deleteOldDataTasklet(), platformTransactionManager).build();
	}

    // Create Step2
    @Bean
    public Step empToDbStep(
							ProcessorUtil<Emp> processor,
							RepoWriterUtil<Emp, UUID> writer,
							StepListener listener,
							RetryAndSkipListener faultListener
							) throws FileNotFoundException {
		FlatFileItemReaderUtil<Emp> util = new FlatFileItemReaderUtil<>();

        return new StepBuilder("empToDbStep",jobRepository)
                .<Emp, Emp>chunk(2, platformTransactionManager) // 設定 輸入和輸出的Type、多少筆資料為一塊
                .reader(util.createFlatFileItemReader(new EmpReaderSetting(filePath)))
				.processor(processor)
                .writer(writer)
				.listener(listener)			// 加入監聽器 監聽READER & WRITER 的錯誤
				.faultTolerant() 		   	// 使用默認的retry_policy 故不再另外設定
				.listener(faultListener)	// 監聽SKIP時的READ & WRITE
				.retryLimit(3) 	// 當出Exception 時允許Spring 重試3次
				.retry(Exception.class)
				.skipLimit(3)	// 重試次數超過後開始skip 時允許Spring 跳過3次 -> 若超過skip次數這個job會停止並記錄狀態為FAILED
				.skip(Exception.class)
                .build();
    }
	
	@Bean
	public JobExecutionDecider emptyFileCheckDecider() {
		return (jobExecution, stepExecution) -> {
			try {
				Resource resources = new UrlResource(ResourceUtils.getURL(filePath));
				if(resources != null && resources.exists() && resources.isReadable() && 
					resources.isFile() && resources.contentLength() > 0 )
					return FlowExecutionStatus.COMPLETED;
			} catch (IOException e) {
				log.error("file has unknown problem");
				return FlowExecutionStatus.UNKNOWN;
			}
			log.error("file not exist or is empty");
			return FlowExecutionStatus.FAILED;
		};
	}

	 // Create Flow
    @Bean("insertFlow")
    public Flow jobFlow(@Qualifier("emptyFileCheckDecider") JobExecutionDecider decider,
		@Qualifier("deleteAllStep") Step step1, @Qualifier("empToDbStep") Step step2) {
    	return new FlowBuilder<Flow>("jobFlow")				
				.start(decider)
					.on(FlowExecutionStatus.COMPLETED.getName()).to(step1).next(step2)
				.from(decider)
					.on(FlowExecutionStatus.FAILED.getName()).fail()
				.from(decider)
					.on(FlowExecutionStatus.UNKNOWN.getName()).fail()
				.end();
    }
    
    // Create Job
	@Bean("example")
    public Job empToDbJob(@Qualifier("insertFlow") Flow flow, JobListener listener) {
    	return new JobBuilder("empToDbJob",jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(flow)
				.build()
				.listener(listener)
				.build();
	}	

}