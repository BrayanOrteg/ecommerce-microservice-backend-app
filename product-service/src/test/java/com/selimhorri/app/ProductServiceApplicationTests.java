package com.selimhorri.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceApplicationTests {
	
	@Test
	@DisplayName("Context Loads")
	void contextLoads() {
		// Simplemente verifica que el contexto de Spring se carga correctamente
	}
	
}






