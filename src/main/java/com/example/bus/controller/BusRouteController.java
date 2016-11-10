package com.example.bus.controller;

import com.example.bus.message.BusRouteResult;
import com.example.bus.route.RouteSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by rado on 11/8/16.
 */
@RestController
@RequestMapping("/api")
public class BusRouteController {

    @Autowired
    private RouteSearchService routeSearchService;

    @RequestMapping(value = "/direct", method = RequestMethod.GET)
    public BusRouteResult getBusRouteByDepartureAndArrival(@RequestParam("dep_sid") int departureStation,
                                                           @RequestParam("arr_sid") int arrivalStation) {

        boolean result = routeSearchService.asyncSearchStationInRoute(departureStation, arrivalStation);

        return new BusRouteResult(departureStation, arrivalStation, result);

    }

}
