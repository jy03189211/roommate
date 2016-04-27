package fi.aalto_iot.tomato.other;

public class FreeFunctions {
    public static int
    getAirQualityPoints(int roomTemp, int roomCo2, int roomHumidity) {
        int tempPoints = 0;
        // determine temperature points
        if (roomTemp < 19) {
            tempPoints = -2;
        } else if (roomTemp <= 20) {
            tempPoints = -1;
        } else if (roomTemp <= 22) {
            tempPoints = 0;
        } else if (roomTemp <= 24) {
            tempPoints = -1;
        } else {
            tempPoints = -2;
        }

        // determine humidity points
        int humidPoints = 0;
        if (roomHumidity < 25) {
            humidPoints = -2;
        } else if (roomHumidity <= 35) {
            humidPoints = -1;
        } else if (roomHumidity <= 50) {
            humidPoints = 0;
        } else if (roomHumidity <= 70) {
            humidPoints = -1;
        } else {
            humidPoints = -2;
        }

        // determine humidity points
        int co2Points = 0;
        if (roomCo2 < 600) {
            co2Points = 1;
        } else if (roomCo2 <= 700) {
            co2Points = 0;
        } else if (roomCo2 <= 900) {
            co2Points = -1;
        } else if (roomCo2 <= 1200) {
            co2Points = -2;
        } else {
            co2Points = -3;
        }

        int allPoints = tempPoints+humidPoints+co2Points;
        return allPoints;
    }
}
