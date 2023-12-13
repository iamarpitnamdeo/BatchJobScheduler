	package com.solum.batchService.config;


import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import com.solum.batchService.entity.BatchUser;
import com.solum.batchService.repository.UserRepository;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class SpringBatchConfig {

	@Autowired
    private JobBuilderFactory jobBuilderFactory;
	@Autowired
    private StepBuilderFactory stepBuilderFactory;
	@Autowired
    private UserRepository userRepository;
	
	 @Autowired
	 private JobLauncher jobLauncher;
	


    @Bean
    public FlatFileItemReader<BatchUser> reader() {
        FlatFileItemReader<BatchUser> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<BatchUser> lineMapper() {
        DefaultLineMapper<BatchUser> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

        BeanWrapperFieldSetMapper<BatchUser> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(BatchUser.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;

    }

    @Bean
    public UserProcessor processor() {
        return new UserProcessor();
    }

    @Bean
    public RepositoryItemWriter<BatchUser> writer() {
        RepositoryItemWriter<BatchUser> writer = new RepositoryItemWriter<>();
        writer.setRepository(userRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("csv-step").<BatchUser, BatchUser>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }
    //code for step2()
    
    @Bean
    public DeleteItemReader deleteReader() {
        return new DeleteItemReader();
    }

    @Bean
    public UsersDeleteProcessor deleteProcessor() {
        return new UsersDeleteProcessor(userRepository);
    }

    @Bean
    public RepositoryItemWriter<BatchUser> deleteWriter() {
        RepositoryItemWriter<BatchUser> writer = new RepositoryItemWriter<>();
        writer.setRepository(userRepository);
        writer.setMethodName("delete");
        return writer;
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("delete-step")
                .<BatchUser, BatchUser>chunk(10)
                .reader(deleteReader())
                .processor(deleteProcessor())
                .writer(deleteWriter())
                .taskExecutor(taskExecutor())
                .build();
    }
    //code for step2 end

    @Bean
    public Job runJob() {
        return jobBuilderFactory.get("importUsers")
                .flow(step1()).end().build();

    }

    @Bean
    public Job deleteJob() {
        return jobBuilderFactory.get("deleteUsers")
            .start(step2())
            .build();
    }
    
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }
    
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5); // Adjust the pool size as needed
        return taskScheduler;
    }

    @Scheduled(fixedRate = 60000) // Run every one minute (in milliseconds)
    public void scheduleDeleteJob() throws Exception {
        JobExecution jobExecution = jobLauncher.run(deleteJob(), new JobParameters());
        // Log or handle the job execution status as needed
    }

}
