package com.example.dominik.trainings;

import android.location.Location;

import com.example.dominik.trainings.entities.Movepoint;

import java.util.List;

/**
 * Created by Dominik on 2015-10-23.
 */
public class TrainingHelper {

    public static double calculateDistance(List<Movepoint> movepointList) {

        int index = 0;

        double totalDistance = 0;

        Movepoint movepoint2 = null;

        for (Movepoint movepoint : movepointList) {

            if (index < movepointList.size()-1) {
                movepoint2 = movepointList.get(index + 1);
            }

            if (movepoint2 != null) {

                double lat1 = movepoint.getLatitude();
                double lon1 = movepoint.getLongitude();

                double lat2 = movepoint2.getLatitude();
                double lon2 = movepoint2.getLongitude();

                double el1 = 0.0;
                double el2 = 0.0;

                if ((movepoint.getAltitude() != 0) && (movepoint2.getAltitude() != 0)) {
                    el1 = movepoint.getAltitude();
                    el2 = movepoint2.getAltitude();
                }

                final int R = 6371;

                Double latDistance = Math.toRadians(lat2 - lat1);
                Double lonDistance = Math.toRadians(lon2 - lon1);
                Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
                Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                double distance = R * c * 1000;

//                double height = 0;
//
//                distance = Math.pow(distance, 2) + Math.pow(height, 2);

                totalDistance += distance;
            }
            index++;
        }
        return totalDistance;
    }

    public static double calculateDistance(Location lastLocation, Location location) {

        if (lastLocation == null) return 0;
        double lat1 = lastLocation.getLatitude();
        double lon1 = lastLocation.getLongitude();

        double lat2 = location.getLatitude();
        double lon2 = location.getLongitude();

        double el1 = 0.0;
        double el2 = 0.0;

        if ((lastLocation.getAltitude() != 0) && (location.getAltitude() != 0)) {
            el1 = lastLocation.getAltitude();
            el2 = location.getAltitude();
        }

        final int R = 6371;

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000;

//        double height = 0;
//
//        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return distance;
    }
}
