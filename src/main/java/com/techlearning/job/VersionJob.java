package com.techlearning.job;

import com.techlearning.dto.VersionResponse;
import com.techlearning.service.VersionService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class VersionJob extends QuartzJobBean {

    Logger logger = org.slf4j.LoggerFactory.getLogger(VersionJob.class);

    private VersionService versionService;

    @Autowired
    public void setVersionService(VersionService versionService) {
        this.versionService = versionService;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // Job logic goes here
        VersionResponse response = versionService.getVersionInfo();
        // For demo purposes we just log the result; in real code you might persist or send it somewhere
        logger.info("Quartz VersionJob executed - version: " + response.getVersion() + ", timestamp: " + response.getCurrentDate());
    }
}
