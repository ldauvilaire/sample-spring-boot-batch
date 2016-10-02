package net.ldauvilaire.sample.batch;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import net.ldauvilaire.sample.batch.Application;

public class ApplicationTest {

	private static final String FIRST_TEST_RESOURCE_PATH = "data/first/sample-data.csv";

	@Test
	public void testApplication() throws Exception {

		Resource resource = new ClassPathResource(FIRST_TEST_RESOURCE_PATH);
		String filePath = resource.getFile().getPath();

		String args[] = new String[] { "-first", filePath };
		Application.main(args);
	}
}
