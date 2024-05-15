package com.eteration.simplebanking;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@ContextConfiguration
@RunWith(SpringRunner.class)
class DemoApplicationTests {

	public static PostgreSQLContainer postgreSQLContainer = (PostgreSQLContainer) (new PostgreSQLContainer("postgres:15.1")
			.withDatabaseName("db")
			.withUsername("eteration")
			.withPassword("eteration"))
			.withReuse(true);

	@Test
	void contextLoads() {
	}

}
