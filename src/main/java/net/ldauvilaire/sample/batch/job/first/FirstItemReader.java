package net.ldauvilaire.sample.batch.job.first;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import net.ldauvilaire.sample.batch.domain.dto.PersonDTO;
import net.ldauvilaire.sample.batch.job.JobConstants;

@Component(JobConstants.FIRST_JOB_ITEM_READER_ID)
public class FirstItemReader extends FlatFileItemReader<PersonDTO> implements StepExecutionListener {

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

	@Override
	public void beforeStep(StepExecution stepExecution) {

		JobParameters jobParameters = stepExecution.getJobParameters();
		String filePath = jobParameters.getString("filePath");
		LOGGER.info("filePath = [{}].", filePath);

		FileSystemResource resource = new FileSystemResource(filePath);
		setResource(resource);
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return null;
	}
}
