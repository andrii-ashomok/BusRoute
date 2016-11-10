package com.example.bus.route;

import com.example.bus.message.BusRoute;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.bus.BusRouteGenerator.FILE_NAME;

/**
 * Created by rado on 11/8/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RouteSearchServiceTest {
    private static final Logger log = LoggerFactory.getLogger(RouteSearchServiceTest.class);

    @Autowired
    private RouteSearchService routeSearchService;


    @Test
    public void testMapLinesIntoBusRoute() {

        List<String> lines;

        StopWatch watch = new StopWatch();
        watch.start("Timer test parsing lines");

        try (Stream<String> stream = Files.lines(Paths.get(FILE_NAME))) {

            lines = stream
                    .collect(Collectors.toList());

            assert !lines.isEmpty();

            List<BusRoute> busRouteList = routeSearchService.mapLinesIntoBusRoute(lines);

            assert !busRouteList.isEmpty();
            assert busRouteList.size() == lines.size() - 1;
            assert busRouteList.stream().allMatch(busRoute -> busRoute.getStationList().size() > 0);

        } catch (IOException e) {
            log.error("Error while reading file using Files.lines: {}", e.getMessage());
        } finally {
            watch.stop();
        }

        log.info("File read using Files.lines, duration {} sec", watch.prettyPrint());
    }


    @Test
    public void testAsyncSearchStationInRoute() {

        List<String> lines = read();

        routeSearchService.mapLinesIntoBusRoute(
                lines
        );

        StopWatch watch = new StopWatch();
        watch.start();

        String s = lines.get(10);
        int d = Integer.parseInt(s.split(" ")[5]);
        int a = Integer.parseInt(s.split(" ")[10]);

        // really exist
        assert routeSearchService.asyncSearchStationInRoute(d, a);

        watch.stop();
        log.info("Search of exist, duration: {}", watch.prettyPrint());


        watch = new StopWatch();
        watch.start();

        // not exist
        assert !routeSearchService.asyncSearchStationInRoute(0, 99999);

        watch.stop();
        log.info("Search of not exist, duration:{}", watch.prettyPrint());

    }

    private List<String> read() {
        List<String> lines = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(FILE_NAME))) {

            lines = stream
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error while reading file using Files.lines: {}", e.getMessage());
        }

        return lines;
    }

 }
