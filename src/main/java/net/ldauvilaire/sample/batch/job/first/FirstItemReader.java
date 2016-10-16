package net.ldauvilaire.sample.batch.job.first;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import net.ldauvilaire.sample.batch.domain.dto.PersonDTO;
import net.ldauvilaire.sample.batch.job.JobConstants;

@StepScope
@Component(JobConstants.FIRST_JOB_ITEM_READER_ID)
public class FirstItemReader extends FlatFileItemReader<PersonDTO> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FirstItemReader.class);

	public FirstItemReader() {
		super();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames(new String[] { "firstName", "lastName" });

		BeanWrapperFieldSetMapper<PersonDTO> fieldSetMapper = new BeanWrapperFieldSetMapper<PersonDTO>();
		fieldSetMapper.setTargetType(PersonDTO.class);

		DefaultLineMapper<PersonDTO> lineMapper = new DefaultLineMapper<PersonDTO>();
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);

		setLineMapper(lineMapper);
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {

		JobParameters jobParameters = stepExecution.getJobParameters();
		String filePath = jobParameters.getString("filePath");
		LOGGER.info("filePath = [{}].", filePath);

		FileSystemResource resource = new FileSystemResource(filePath);
		setResource(resource);
	}
}
