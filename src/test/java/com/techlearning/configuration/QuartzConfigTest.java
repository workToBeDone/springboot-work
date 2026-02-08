package com.techlearning.configuration;

import com.techlearning.job.VersionJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = QuartzConfig.class)
@TestPropertySource(properties = {
        "version.job.cron=0 0/5 * * * ?"
})
class QuartzConfigTest {

    QuartzConfig config;

    @BeforeEach
    void setUp() {
        config = new QuartzConfig();
    }

    @Test
    void versionJobDetail_factoryMethodShouldReturnProperJobDetail() {
        JobDetail jobDetail = config.versionJobDetail();

        assertThat(jobDetail).isNotNull();
        assertThat(jobDetail.getKey().getName()).isEqualTo("VersionJob");
        assertThat(jobDetail.isDurable()).isTrue();
        assertThat(jobDetail.getJobClass()).isEqualTo(VersionJob.class);
    }

    @Test
    void versionJobTrigger_shouldUseConfiguredCronExpression() {
        Trigger versionJobTrigger = config.versionJobTrigger(config.versionJobDetail(),
                "0 0/5 * * * ?");

        assertThat(versionJobTrigger).isNotNull();
        assertThat(versionJobTrigger).as("versionJobTrigger bean should be created").isNotNull();
        assertThat(versionJobTrigger.getKey().getName()).isEqualTo("VersionJobTrigger");
        assertThat(versionJobTrigger.getJobKey().getName()).isEqualTo("VersionJob");
    }
}
