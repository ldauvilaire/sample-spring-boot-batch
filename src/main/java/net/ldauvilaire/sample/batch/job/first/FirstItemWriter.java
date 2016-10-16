package net.ldauvilaire.sample.batch.job.first;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import net.ldauvilaire.sample.batch.domain.model.Person;
import net.ldauvilaire.sample.batch.job.JobConstants;

@StepScope
@Component(JobConstants.FIRST_JOB_ITEM_WRITER_ID)
public class FirstItemWriter implements ItemWriter<Person> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FirstItemWriter.class);

	@PersistenceContext
	protected EntityManager entityManager;

	public void write(List<? extends Person> items) throws Exception {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Writing to JPA with {} items.", items.size());
		}

		if (! items.isEmpty()) {

			long persistCount = 0;
			long mergeCount = 0;

			for (Person item : items) {
				if (item.getId() == null) {
					entityManager.persist(item);
					persistCount++;
				} else {
					entityManager.merge(item);
					mergeCount++;
				}
			}
			entityManager.flush();
			entityManager.clear();

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("{} entities persisted.", persistCount);
				LOGGER.info("{} entities merged.", mergeCount);
			}
		}
	}
}
