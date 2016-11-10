package com.example.bus;

import com.example.bus.route.RouteSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Objects;

@SpringBootApplication
public class BusRouterApplication {
	private static final Logger log = LoggerFactory.getLogger(BusRouterApplication.class);

	public static void main(String[] args) {

		if (args.length < 1) {
			log.error("Please input absolute file path with Bus Route Data! For example: /home/username/BusRoute.txt");

			return;
		}

		Thread.setDefaultUncaughtExceptionHandler(
				(t, e) -> log.error("ERR001 - Uncaught exception occurred - {} {}",
						e.getMessage(), e.getStackTrace()));

		ConfigurableApplicationContext context = SpringApplication.run(BusRouterApplication.class, args);

		if (Objects.nonNull(context)) {

			context.registerShutdownHook();

			RouteSearchService searchService = context.getBean(RouteSearchService.class);

			if (!searchService.readBusRouteData(args[0])) {
				context.stop();
				context.close();
			}
		}


	}
}
