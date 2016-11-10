package com.example.bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * Created by rado on 11/8/16.
 */
public class BusRouteGenerator {
    private static final Logger log = LoggerFactory.getLogger(BusRouteGenerator.class);

    private static final String SPACE = " ";
    public static final String FILE_NAME = "BusRouteTest";


    static void generateFileWithBusRoute(String fileName,
                                                int maxNumberOfBusRoutes,
                                                int maxNumberOfStations) {

        StopWatch watch = new StopWatch();
        watch.start();

        if (Objects.isNull(fileName) || "".equals(fileName))
            fileName = FILE_NAME;

        List<String> lines = new ArrayList<>();
        lines.add(String.valueOf(maxNumberOfBusRoutes));

        for (int i = 0; i < maxNumberOfBusRoutes; i++) {
            StringBuilder line = new StringBuilder(String.valueOf(i))
                    .append(SPACE);

            Stream.generate(() -> ThreadLocalRandom.current().nextInt(1, 1_000_000))
                        .distinct()
                        .limit(maxNumberOfStations)
                        .forEach(number -> line.append(number).append(SPACE));

            lines.add(line.toString());
        }

        try {
            Files.write(Paths.get(fileName), lines);
        } catch (IOException e) {
            log.error("Could not create file example with file name {}, cause {}", fileName, e.getMessage());
        } finally {
            watch.stop();

            log.info("File example with name {} was created, " +
                    "number of bus routes {}, " +
                    "number of bus stations {}, " +
                    "duration {} sec, {}",
                    fileName, maxNumberOfBusRoutes, maxNumberOfStations,
                    watch.getTotalTimeSeconds(), watch.prettyPrint());
        }

    }


}
