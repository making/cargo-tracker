package se.citerus.dddsample.interfaces;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.interfaces.handling.file.UploadDirectoryScanner;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.Locale;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Value("${uploadDirectory}")
	public String uploadDirectory;

	@Value("${parseFailureDirectory}")
	public String parseFailureDirectory;

	@Autowired
	public EntityManager entityManager;

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		return messageSource;
	}

	@Bean
	public FixedLocaleResolver localeResolver() {
		FixedLocaleResolver fixedLocaleResolver = new FixedLocaleResolver();
		fixedLocaleResolver.setDefaultLocale(Locale.ENGLISH);
		return fixedLocaleResolver;
	}

	@Bean
	public UploadDirectoryScanner uploadDirectoryScanner(ApplicationEvents applicationEvents) {
		File uploadDirectoryFile = new File(uploadDirectory);
		File parseFailureDirectoryFile = new File(parseFailureDirectory);
		return new UploadDirectoryScanner(uploadDirectoryFile, parseFailureDirectoryFile, applicationEvents);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		OpenEntityManagerInViewInterceptor openSessionInViewInterceptor = new OpenEntityManagerInViewInterceptor();
		openSessionInViewInterceptor.setEntityManagerFactory(entityManager.getEntityManagerFactory());
		registry.addWebRequestInterceptor(openSessionInViewInterceptor);
	}

	@Bean
	public ThreadPoolTaskScheduler myScheduler(@Nullable UploadDirectoryScanner scanner) {
		if (scanner == null) {
			log.info("No UploadDirectoryScannerBean found, skipping creation of scheduler.");
			return null;
		}
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(10);
		threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
		threadPoolTaskScheduler.initialize();
		threadPoolTaskScheduler.scheduleAtFixedRate(scanner, 5000);
		return threadPoolTaskScheduler;
	}

}
