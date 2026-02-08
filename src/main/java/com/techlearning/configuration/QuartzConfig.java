package com.techlearning.configuration;

import com.techlearning.job.VersionJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    private static final String VERSION_JOB_NAME = "VersionJob";

    @Bean(name = "versionJobDetail")
    public JobDetail versionJobDetail() {
        return JobBuilder.newJob(VersionJob.class)
                .withIdentity(VERSION_JOB_NAME)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger versionJobTrigger(JobDetail versionJobDetail,
                                     @Value("${version.job.cron:0 0/1 * * * ?}") String cronExpression) {
        return TriggerBuilder.newTrigger()
                .forJob(versionJobDetail)
                .withIdentity(VERSION_JOB_NAME + "Trigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }
}
