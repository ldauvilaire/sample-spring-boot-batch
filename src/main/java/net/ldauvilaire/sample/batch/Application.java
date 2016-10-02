package net.ldauvilaire.sample.batch;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import net.ldauvilaire.sample.batch.job.JobConstants;

@SpringBootApplication
public class Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
	private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext context) {
		return args -> {

			CommandLineParser parser = new DefaultParser();
			Options options = new Options();
			options.addOption(Option.builder("first")
					.argName("file")
					.hasArg()
					.desc("use given file for first job")
					.build());

			CommandLine line = parser.parse(options, args);

			if (line.hasOption("first")) {
				String param = line.getOptionValue("first");

				File firstFile = new File(param);
				if (firstFile.exists()) {
					first(context, firstFile);
					SpringApplication.exit(
							context,
							() -> 0);
				} else {
					LOGGER.error("File [{}] does not exists", firstFile);
					HelpFormatter formatter = new HelpFormatter();
					formatter.printHelp("sample-spring-boot-batch", options);
				}

			} else {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("sample-spring-boot-batch", options);
			}
		};
	}

	public void first(ApplicationContext context, File inputFile) {

		LOGGER.debug("First - Start ...");

		String inputFilePath = inputFile.getPath();
		LOGGER.debug("First - Input Path = [{}].", inputFilePath);

		String filePath = null;
		try {
			filePath = inputFile.getPath();
		} catch (Exception ex) {
			LOGGER.error("An Exception has occurred", ex);
		}

		LOGGER.info("filePath = [{}].", filePath);

		JobLauncher jobLauncher = (JobLauncher) context.getBean(JobLauncher.class);

		Job firstJob = null;
		try {
			firstJob = (Job) context.getBean(JobConstants.FIRST_JOB_ID);
		} catch (BeansException ex) {
			LOGGER.error("A BeansException has occurred", ex);
		}

		if (firstJob != null) {
			for (int i=0; i<2; i++) {

				JobParametersBuilder jpBuilder = new JobParametersBuilder();
				{
					jpBuilder.addString("time", DF.format(new Date()));
					jpBuilder.addString("filePath", filePath);
				}
				JobParameters jobParameters = jpBuilder.toJobParameters();

				try {
					LOGGER.info("-- Run {} - Debut -----------------------------------------------", i+1);
					jobLauncher.run(firstJob, jobParameters);
					LOGGER.info("-- Run {} - Fin -------------------------------------------------", i+1);
				} catch (JobExecutionAlreadyRunningException ex) {
					LOGGER.error("A JobExecutionAlreadyRunningException has occurred", ex);
				} catch (JobRestartException ex) {
					LOGGER.error("A JobRestartException has occurred", ex);
				} catch (JobInstanceAlreadyCompleteException ex) {
					LOGGER.error("A JobInstanceAlreadyCompleteException has occurred", ex);
				} catch (JobParametersInvalidException ex) {
					LOGGER.error("A JobParametersInvalidException has occurred", ex);
				} catch (Exception ex) {
					LOGGER.error("An Exception has occurred", ex);
				}
			}
		}

		LOGGER.debug("First Init - End.");
	}
}
