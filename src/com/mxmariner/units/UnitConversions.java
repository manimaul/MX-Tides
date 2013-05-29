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
    public static double MpsToKts(double mps) {
        return (double) Math.round((mps * mpsKts) * 10) / 10;
    }
    
    public static double KtsToMps(double kts) {
        return (double) Math.round((kts / mpsKts) * 1000000) / 1000000;
    }
    
    // MILES PER HOUR ... METERS PER SECOND
    public static double MpsToMph(double mps) {
        return (double) Math.round((mps * mpsMph) * 10) / 10;
    }
    
    public static double MphToMps(double mph) {
        return (double) Math.round((mph / mpsMph) * 1000000) / 1000000;
    }
    
    // KILOMETERS PER HOUR ... METERS PER SECOND
    public static double MpsToKph(double mps) {
        return (double) Math.round((mps * mpsKph) * 10) / 10;
    }
    
    public static double KphToMps(double kph) {
        return (double) Math.round((kph / mpsKph) * 1000000) / 1000000;
    }
    
    /*
     * DISTANCE
     */
    
    // NAUTICAL MILES ... METERS
    public static double MeToNm(double meters) {
        return (double) Math.round((meters / meterNauticalMile) * 100) / 100;
    }
    
    public static double NmToMe(double nm) {
        return (double) Math.round((nm * meterNauticalMile) * 10) / 10;
    }
    
    // STATUTE MILES ... METERS
    public static double MeToMi(double meters) {
        return (double) Math.round((meters / meterSatuteMile) * 100) / 100;
    }
    
    public static double MiToMe(double mi) {
        return (double) Math.round((mi * meterSatuteMile) * 10) / 10;
    }
    
    // FEET ... METERS
    public static double MeToFt(double meters) {
        return (double) Math.round((meters / meterFoot) * 100) / 100;
    }
    
    public static double FtToMe(double ft) {
        return (double) Math.round((ft * meterFoot) * 10) / 10;
    }
    
    // KILOMETERS ... METERS
    public static double MeToKm(double meters) {
        return (double) Math.round((meters / meterKilometer) * 100) / 100;
    }
    
    public static double KmToMe(double km) {
        return (double) Math.round((km * meterKilometer) * 10) / 10;
    }
}
