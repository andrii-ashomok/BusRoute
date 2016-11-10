package com.example.bus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.bus.BusRouteGenerator.FILE_NAME;

/**
 * Created by rado on 11/8/16.
 */
@RunWith(SpringRunner.class)
public class FileReadSpeedTest {
    private static final Logger log = LoggerFactory.getLogger(FileReadSpeedTest.class);


    @Test
    public void testFileReadLines() {
        List<String> lines;

        StopWatch watch = new StopWatch();
        watch.start();

        try (Stream<String> stream = Files.lines(Paths.get(FILE_NAME))) {

            lines = stream
                    .collect(Collectors.toList());

            assert !lines.isEmpty();

        } catch (IOException e) {
            log.error("Error while reading file using Files.lines: {}", e.getMessage());
        } finally {
            watch.stop();
        }

        log.info("File read using Files.lines, duration {} sec", watch.prettyPrint());


    }

    @Test
    public void testFileReadAllLines() {
        List<String> lines;

        StopWatch watch = new StopWatch();
        watch.start();

        try {

            lines = Files.readAllLines(Paths.get(FILE_NAME));
            assert !lines.isEmpty();

        } catch (IOException e) {

            log.error("Error while reading file using Files.readAllLines: {}", e.getMessage());

        } finally {
            watch.stop();
        }

        log.info("File read using Files.readAllLines, duration {} sec", watch.prettyPrint());

    }


    @Test
    public void testFileReadBufferedReader() {
        List<String> lines;

        StopWatch watch = new StopWatch();
        watch.start();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(FILE_NAME))) {

            lines = br.lines().collect(Collectors.toList());
            assert !lines.isEmpty();

        } catch (IOException e) {
            log.error("Error while reading file using Files.newBufferedReader: {}", e.getMessage());
        } finally {
            watch.stop();
        }

        log.info("File read using BufferedReader, duration {} sec", watch.prettyPrint());

    }




}
