package com.caseapp.socialengine.config;

import com.caseapp.socialengine.job.PostPublisherJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail postPublisherJobDetail() {
        return JobBuilder.newJob(PostPublisherJob.class)
                .withIdentity("postPublisherJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger postPublisherJobTrigger(JobDetail postPublisherJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(postPublisherJobDetail)
                .withIdentity("postPublisherTrigger")
                // Run exactly every 60 seconds forever
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(60)
                        .repeatForever())
                .build();
    }
}