package com.example.bus.message;

import java.util.List;

/**
 * Created by rado on 11/8/16.
 */
public class BusRoute {

    private int id;
    private List<Integer> stationList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getStationList() {
        return stationList;
    }

    public void setStationList(List<Integer> stationList) {
        this.stationList = stationList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusRoute busRoute = (BusRoute) o;

        if (id != busRoute.id) return false;
        return stationList != null ? stationList.equals(busRoute.stationList) : busRoute.stationList == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (stationList != null ? stationList.hashCode() : 0);
        return result;
    }
}
