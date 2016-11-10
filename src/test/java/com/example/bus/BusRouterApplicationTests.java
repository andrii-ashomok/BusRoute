package com.example.bus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BusRouterApplicationTests {

	@Test
	public void testGenerateBusRouteData() {

		BusRouteGenerator.generateFileWithBusRoute(null, 51_000, 600);

		assert Files.exists(Paths.get(BusRouteGenerator.FILE_NAME));
	}

}
