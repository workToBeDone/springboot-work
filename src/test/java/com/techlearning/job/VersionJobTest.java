package com.techlearning.job;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.techlearning.dto.VersionResponse;
import com.techlearning.service.VersionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.quartz.JobExecutionContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = VersionJob.class)
class VersionJobTest {

    @Autowired
    private VersionJob versionJob;

    @MockitoBean
    private VersionService versionService;

    private JobExecutionContext jobExecutionContext;

    private Logger logger;
    private Appender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        jobExecutionContext = mock(JobExecutionContext.class);

        logger = (Logger) LoggerFactory.getLogger(VersionJob.class);
        appender = mock(Appender.class);
        when(appender.getName()).thenReturn("TEST_APPENDER");
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
    }

    @Test
    void executeInternal_shouldCallVersionServiceAndLogDetails() throws Exception {
        LocalDateTime localDateTime1 = LocalDateTime.of(2026, 02, 02, 14, 33, 48, 123456789);
        VersionResponse response = new VersionResponse("1.0.0", localDateTime1, "DemoApp");
        when(versionService.getVersionInfo()).thenReturn(response);

        versionJob.executeInternal(jobExecutionContext);

        verify(versionService, times(1)).getVersionInfo();

        ArgumentCaptor<ILoggingEvent> captor = ArgumentCaptor.forClass(ILoggingEvent.class);
        verify(appender, atLeastOnce()).doAppend(captor.capture());

        ILoggingEvent event = captor.getAllValues().stream()
                .filter(e -> e.getLevel() == Level.INFO)
                .findFirst()
                .orElseThrow();

        String msg = event.getFormattedMessage();
        assertThat(msg)
                .contains("Quartz VersionJob executed - version: 1.0.0, timestamp: ")
                .contains("timestamp: " + localDateTime1);
    }

    @Test
    void executeInternal_whenServiceThrowsException_shouldPropagate() {
        when(versionService.getVersionInfo()).thenThrow(new RuntimeException("service failed"));

        assertThrows(RuntimeException.class,
                () -> versionJob.executeInternal(jobExecutionContext));
    }
}
