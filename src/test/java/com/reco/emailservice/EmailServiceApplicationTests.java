package com.reco.emailservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Disabled;


@Disabled("Skipping contextLoads test temporarily in CI")
@SpringBootTest
@ActiveProfiles("test")
class EmailServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
