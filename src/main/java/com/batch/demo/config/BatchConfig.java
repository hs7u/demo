package com.batch.demo.config;

import java.io.FileNotFoundException;
import java.util.UUID;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.batch.demo.batch.listener.JobListener;
import com.batch.demo.batch.listener.RetryAndSkipListener;
import com.batch.demo.batch.listener.StepListener;
import com.batch.demo.batch.util.processor.ProcessorUtil;
import com.batch.demo.batch.util.reader.FlatFileItemReaderUtil;
import com.batch.demo.batch.util.reader.settings.EmpReaderSetting;
import com.batch.demo.batch.util.writer.RepoWriterUtil;
import com.batch.demo.repository.BaseRepository;
import com.batch.demo.vo.vo.Emp;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    
	@Autowired
	private JobRepository jobRepository;
	@Autowired
    private BaseRepository<Emp, UUID> empRepo;

	// showing error log example
	@Bean("empWriter")
	public ItemWriter<Emp> crWriter(){
		return new ItemWriter<Emp>() {
			@Override
			public void write(Chunk<? extends Emp> chunk) throws Exception {
				for(Emp p:chunk){
					if(p.getEmpName().equals("errorData")){
						throw new Exception();
					}
				}
				empRepo.saveAll(chunk);
			}			
		};
	}

    // Create Step
    @Bean
    public Step empToDbStep(
							@Value("${file.input.path1}") String filePath, 
							ProcessorUtil<Emp> processor,
							RepoWriterUtil<Emp, UUID> writer, 
							PlatformTransactionManager platformTransactionManager,
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
    
    // Create Job
    @Bean("example")
    public Job empToDbJob(@Qualifier("empToDbStep") Step step,JobListener listener) {
    	return new JobBuilder("empToDbJob",jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(step)
				.listener(listener)
				.build();
    }
    
    /**
	 * decider and flow job example
	*/
    // @Bean
    // public Step step1() {
	// 	return null;
	// }
	
    // public JobExecutionDecider foo() {
	// 	return (jobExecution, stepExecution) -> 
    //         stepExecution.getExitStatus() == ExitStatus.COMPLETED 
    //             ? new FlowExecutionStatus("COMPLETED") 
    //             : stepExecution.getExitStatus() == ExitStatus.FAILED 
    //             ? new FlowExecutionStatus("FAILED") 
    //             : new FlowExecutionStatus("UNKNOWN");
	// }

	// public Step qux() {
	// 	return null;
	// }
	// public Step quux() {
	// 	return null;
	// }
	// public Step baz() {
	// 	return null;
	// }
	// public Step bar() {
	// 	return null;
	// }

	// @Bean
	// public Job job() {
	// 	return new JobBuilder("empToDbJob",jobRepository)
	// 			.incrementer(new RunIdIncrementer())
	// 			.start(step1())
	// 			.next(foo()).on("UNKNOWN").to(baz())
	// 			.from(foo()).on("FAILED").to(bar())
	// 			.from(foo()).on("COMPLETED").to(qux()).next(quux())
	// 			.end()
	// 			.build();
	// }
}


