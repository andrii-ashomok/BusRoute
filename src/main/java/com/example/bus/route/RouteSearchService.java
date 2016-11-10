package com.example.bus.route;

import com.example.bus.message.BusRoute;

import java.util.List;

/**
 * Created by rado on 11/8/16.
 */
public interface RouteSearchService {

    /**
     * Check if file exist follow the input URI
     * and read lines into List
     * @param uri - path to file
     * @return - true - file read successfully, false - no file or file is empty
     */
    boolean readBusRouteData(String uri);

    /**
     * Parse read lines into BusRoute objects
     * @param lines - read lines from Bus Route Data file
     * @return - mapped lines into BusRoute list
     */
    List<BusRoute> mapLinesIntoBusRoute(List<String> lines);

    /**
     * Asynchronized search of stations in BusRoute list
     * @param departureId - departure station
     * @param arrivalId - arrival station
     * @return - true - found departure station and arrival station in ONE route, false - not found
     */
    boolean asyncSearchStationInRoute(final int departureId, final int arrivalId);
}
