package net.ldauvilaire.sample.batch.job.first;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import net.ldauvilaire.sample.batch.domain.dto.PersonDTO;
import net.ldauvilaire.sample.batch.domain.model.Person;
import net.ldauvilaire.sample.batch.job.JobConstants;

@StepScope
@Component(JobConstants.FIRST_JOB_ITEM_PROCESSOR_ID)
public class FirstItemProcessor implements ItemProcessor<PersonDTO, Person> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FirstItemProcessor.class);

	public Person process(PersonDTO item) throws Exception {

		final String firstName = item.getFirstName().toUpperCase();
		final String lastName = item.getLastName().toUpperCase();

		final Person transformed = new Person(firstName, lastName);

		LOGGER.info("Converting (" + item + ") into (" + transformed + ")");

		return transformed;
	}
}