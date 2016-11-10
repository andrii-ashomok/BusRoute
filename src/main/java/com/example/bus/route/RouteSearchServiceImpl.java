package com.example.bus.route;

import com.example.bus.message.BusRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by rado on 11/8/16.
 */
@Component
public class RouteSearchServiceImpl implements RouteSearchService {
    private static final Logger log = LoggerFactory.getLogger(RouteSearchServiceImpl.class);

    private static final String SPACE_SYMBOL = " ";

    @Value("${executor.parse.line.core.pool.size}")
    private int parserCorePoolSize;

    @Value("${executor.parse.line.max.pool.size}")
    private int parserMaxPoolSize;

    @Value("${executor.search.station.core.pool.size}")
    private int searchCorePoolSize;

    @Value("${executor.parse.line.core.pool.size}")
    private int searchMaxPoolSize;

    private ThreadPoolExecutor executor;
    private static CopyOnWriteArrayList<BusRoute> busRouteCopyOnWriteArrayList;
    private BlockingQueue queue;

    @Override
    public boolean readBusRouteData(String uri) {
        List<String> lines = new ArrayList<>();

        StopWatch watch = new StopWatch();
        watch.start();

        if (!Paths.get(uri).toFile().exists()) {
            log.error("File {} not exists", uri);

            return false;
        }

        try (Stream<String> stream = Files.lines(Paths.get(uri))) {

            lines = stream
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error while reading file using Files.lines: {}", e.getMessage());
        } finally {
            watch.stop();
        }


        log.info("File {} read , duration {} sec", uri, watch.getTotalTimeSeconds());

        if (!lines.isEmpty()) {

            mapLinesIntoBusRoute(lines);

        } else {

            log.warn("File {} is empty!", uri);
        }

        return true;
    }



    @Override
    public List<BusRoute> mapLinesIntoBusRoute(List<String> lines) {

        if (Objects.isNull(lines) || lines.isEmpty()) {
            log.warn("Nothing to convert from input array of lines, array is empty");
            return Collections.EMPTY_LIST;
        }

        int size = Integer.valueOf(lines.get(0));

        if (size < 0) {
            return Collections.EMPTY_LIST;
        }

        int queueSize = size;
        if (size >= 20_000) {

            queueSize = size/2;
        }

        initConcurrency(parserCorePoolSize, parserMaxPoolSize, size, queueSize);

        StopWatch watch = new StopWatch();
        watch.start();
        busRouteCopyOnWriteArrayList = new CopyOnWriteArrayList<>();

        lines.stream()
                .skip(1)
                .forEach(line ->
                        executor.submit(
                                new ConverterLineToObject(line)));

        turnOffExecutor(size, "Lines parsing process interrupted");

        watch.stop();

        log.info("Lines (size: {}) parsing process duration {} sec", size, watch.getTotalTimeSeconds());

        return busRouteCopyOnWriteArrayList;
    }





   /* public List<BusRoute> splitLineArrayToMap(List<String> lines, int size) {

        Spliterator<String> spliterator = lines.spliterator().trySplit();
        int estimateSize = (int) spliterator.estimateSize();

        log.info("Size: {} , estimateSize: {}", size, estimateSize);

        CompletableFuture<List<BusRoute>> completableFuture1 =
                CompletableFuture.completedFuture(
                        mapLinesIntoBusRoute(lines.subList(0, estimateSize)));

        CompletableFuture<List<BusRoute>> completableFuture2 =
                CompletableFuture.completedFuture(
                        mapLinesIntoBusRoute(lines.subList(estimateSize, size)));

        CompletableFuture<List<BusRoute>> combineCF = completableFuture1.thenCombine(completableFuture2,
                (c1, c2) ->  Stream.concat(c1.stream(),c2.stream()).collect(Collectors.toList()));

        try {
            return combineCF.get(size, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }


        return Collections.EMPTY_LIST;
    }*/



    @Override
    public boolean asyncSearchStationInRoute(final int departureId, final int arrivalId) {

        if (Objects.isNull(busRouteCopyOnWriteArrayList) )
            return false;

        int size = busRouteCopyOnWriteArrayList.size();

        if (size == 0)
            return false;

        initConcurrency(searchCorePoolSize, searchMaxPoolSize, size, size);

        List<Future<Boolean>> futures = busRouteCopyOnWriteArrayList.stream()
                .map(o ->
                    executor.submit(() -> isStationInRoute(departureId, arrivalId, o)))
                .collect(Collectors.toList());

        StopWatch watch = new StopWatch();
        watch.start();

        boolean result = false;

        for (Future<Boolean> future : futures) {

            try {
                Boolean b = future.get(500, TimeUnit.MILLISECONDS);

                if (b) {
                    result = true;

                    break;
                }

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("Some result of the search was lost, cause {}", e.getMessage());
            }

        }

        watch.stop();
        log.info("Search result duration {} sec", watch.getTotalTimeSeconds());

        turnOffExecutor(size, "Find route for station: departure - " + departureId + ", arrival - " + arrivalId);

        return result;
    }


    private boolean isStationInRoute(int departureId, int arrivalId, BusRoute busRoute) {

        if (busRoute.getStationList().contains(departureId)
                && busRoute.getStationList().contains(arrivalId)) {

            log.info("Bus route {} includes departure {} and arrival {} stations",
                    busRoute.getId(), departureId, arrivalId);

            return true;
        }

        return false;

        /*return busRoute.getStationList().contains(departureId)
                && busRoute.getStationList().contains(arrivalId);*/

    }



    private void initConcurrency(int corePoolSize, int maxPoolSize, int ttl, int queueSize) {


        queue = new ArrayBlockingQueue(queueSize);

        executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
                ttl, TimeUnit.MILLISECONDS, queue);


        executor.setRejectedExecutionHandler(new BlockingQueueRejection());
    }

    private void turnOffExecutor(int ttl, String message) {

        executor.shutdown();

        try {

            executor.awaitTermination(ttl, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {

            log.error("{}, cause {}", message, e.getMessage());

        } finally {

            if (!executor.isShutdown())
                executor.shutdownNow();
        }

    }


    private static final class ConverterLineToObject implements Runnable {

        private String line;

        ConverterLineToObject(String line) {
            this.line = line;
        }

        @Override
        public void run() {
            BusRoute busRoute = new BusRoute();
            String[] arrLines = line.split(SPACE_SYMBOL);

            busRoute.setId(Integer.valueOf(arrLines[0]));

            busRoute.setStationList(Arrays.stream(arrLines)
                    .skip(1)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList()));

            busRouteCopyOnWriteArrayList.add(busRoute);
        }
    }

}
