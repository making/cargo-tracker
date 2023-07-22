package se.citerus.dddsample.interfaces;

import am.ik.accesslogger.AccessLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import se.citerus.dddsample.interfaces.handling.file.UploadDirectoryScanner;

import java.time.Duration;

@Configuration
public class InterfaceConfig {
    @Bean
    public AccessLogger accessLogger() {
        return new AccessLogger(httpExchange -> {
            final String uri = httpExchange.getRequest().getUri().getPath();
            return uri != null && !(uri.equals("/readyz") || uri.equals("/livez") || uri.startsWith("/actuator"));
        });
    }

    @Bean
    public ThreadPoolTaskScheduler myScheduler(UploadDirectoryScanner scanner) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        threadPoolTaskScheduler.initialize();
        threadPoolTaskScheduler.scheduleAtFixedRate(scanner, Duration.ofMillis(5000));
        return threadPoolTaskScheduler;
    }

}
