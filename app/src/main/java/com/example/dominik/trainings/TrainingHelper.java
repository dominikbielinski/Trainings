package com.example.dominik.trainings;

import java.util.List;

/**
 * Created by Dominik on 2015-10-23.
 */
public class TrainingHelper {

    public static double calculateDistance(List<MovePoint> movePointList) {

        int index = 0;

        double totalDistance = 0;

        for (MovePoint movePoint : movePointList) {

            MovePoint movePoint2 = movePointList.get(index+1);

            if (movePoint2 != null) {

                double lat1 = movePoint.getLatitude();
                double lon1 = movePoint.getLongitude();

                double lat2 = movePoint2.getLatitude();
                double lon2 = movePoint2.getLongitude();

                double el1 = 0.0;
                double el2 = 0.0;

                if ((movePoint.getAltitude() != 0) && (movePoint2.getAltitude() != 0)) {
                    el1 = movePoint.getAltitude();
                    el2 = movePoint2.getAltitude();
                }

                final int R = 6371;

                Double latDistance = Math.toRadians(lat2 - lat1);
                Double lonDistance = Math.toRadians(lon2 - lon1);
                Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
                Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                double distance = R * c * 1000;

                double height = 0;

                distance = Math.pow(distance, 2) + Math.pow(height, 2);

                totalDistance += Math.sqrt(distance);
            }
        }
        return totalDistance;
    }

public static double calculateDuration(List<MovePoint>movePointList){
        return 2000;
        }
        }
