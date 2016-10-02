package net.ldauvilaire.sample.batch.configuration;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan(basePackages = { "net.ldauvilaire.sample" })
@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
@EnableTransactionManagement
public class ApplicationConfiguration {
}
