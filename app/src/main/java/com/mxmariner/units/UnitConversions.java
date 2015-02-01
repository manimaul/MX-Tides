package com.mxmariner.units;

public class UnitConversions {
    public static final double mpsKts = 1.94384449d;
    public static final double mpsMph = 2.23693629d;
    public static final double mpsKph = 3.6d;
    
    public static final double meterNauticalMile = 1852d;
    public static final double meterSatuteMile = 1609.344d;
    public static final double meterFoot = 0.3048d;
    public static final double meterKilometer = 1000d;

    /*
     * SPEED
     */
    
    // KNOTS ... METERS PER SECOND
    public static double mpsToKts(double mps) {
        return (double) Math.round((mps * mpsKts) * 10) / 10;
    }
    
    public static double ktsToMps(double kts) {
        return (double) Math.round((kts / mpsKts) * 1000000) / 1000000;
    }
    
    // MILES PER HOUR ... METERS PER SECOND
    public static double mpsToMph(double mps) {
        return (double) Math.round((mps * mpsMph) * 10) / 10;
    }
    
    public static double mphToMps(double mph) {
        return (double) Math.round((mph / mpsMph) * 1000000) / 1000000;
    }
    
    // KILOMETERS PER HOUR ... METERS PER SECOND
    public static double mpsToKph(double mps) {
        return (double) Math.round((mps * mpsKph) * 10) / 10;
    }
    
    public static double kphToMps(double kph) {
        return (double) Math.round((kph / mpsKph) * 1000000) / 1000000;
    }
    
    /*
     * DISTANCE
     */
    
    // NAUTICAL MILES ... METERS
    public static double meToNm(double meters) {
        return (double) Math.round((meters / meterNauticalMile) * 100) / 100;
    }
    
    public static double nmToMe(double nm) {
        return (double) Math.round((nm * meterNauticalMile) * 10) / 10;
    }
    
    // STATUTE MILES ... METERS
    public static double meToMi(double meters) {
        return (double) Math.round((meters / meterSatuteMile) * 100) / 100;
    }
    
    public static double miToMe(double mi) {
        return (double) Math.round((mi * meterSatuteMile) * 10) / 10;
    }
    
    // FEET ... METERS
    public static double meToFt(double meters) {
        return (double) Math.round((meters / meterFoot) * 100) / 100;
    }
    
    public static double ftToMe(double ft) {
        return (double) Math.round((ft * meterFoot) * 10) / 10;
    }
    
    // KILOMETERS ... METERS
    public static double meToKm(double meters) {
        return (double) Math.round((meters / meterKilometer) * 100) / 100;
    }
    
    public static double kmToMe(double km) {
        return (double) Math.round((km * meterKilometer) * 10) / 10;
    }
}
