package com.example.bus.message;

/**
 * Created by rado on 11/8/16.
 */
public class BusRouteResult {

    private int departureStation;
    private int arrivalStation;
    private boolean isRouteAvailable;


    public BusRouteResult() {
    }

    public BusRouteResult(int departureStation, int arrivalStation) {
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
    }

    public BusRouteResult(int departureStation, int arrivalStation, boolean isRouteAvailable) {
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.isRouteAvailable = isRouteAvailable;
    }

    public int getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(int departureStation) {
        this.departureStation = departureStation;
    }

    public int getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(int arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public boolean isRouteAvailable() {
        return isRouteAvailable;
    }

    public void setRouteAvailable(boolean routeAvailable) {
        isRouteAvailable = routeAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusRouteResult busRouteResult = (BusRouteResult) o;

        if (departureStation != busRouteResult.departureStation) return false;
        if (arrivalStation != busRouteResult.arrivalStation) return false;
        return isRouteAvailable == busRouteResult.isRouteAvailable;

    }

    @Override
    public int hashCode() {
        int result = departureStation;
        result = 31 * result + arrivalStation;
        result = 31 * result + (isRouteAvailable ? 1 : 0);
        return result;
    }
}
