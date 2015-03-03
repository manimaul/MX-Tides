package com.mxmariner.units;

public class UnitConversions {

    //region CLASS VARIABLES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static final double MPS_KTS = 1.94384449d;
    public static final double MPS_MPH = 2.23693629d;
    public static final double MPS_KPH = 3.6d;

    public static final double METER_NAUTICAL_MILE = 1852d;
    public static final double METER_SATUTE_MILE = 1609.344d;
    public static final double METER_FOOT = 0.3048d;
    public static final double METER_KILOMETER = 1000d;

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CLASS METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Speed conversion
     *
     * @param mps meters per second
     * @return knots
     */
    public static double mpsToKts(double mps) {
        return (double) Math.round((mps * MPS_KTS) * 10) / 10;
    }

    /**
     * Speed conversion
     *
     * @param kts knots
     * @return meters per second
     */
    public static double ktsToMps(double kts) {
        return (double) Math.round((kts / MPS_KTS) * 1000000) / 1000000;
    }

    /**
     * Speed conversion
     *
     * @param mps meters per second
     * @return miles per hour
     */
    public static double mpsToMph(double mps) {
        return (double) Math.round((mps * MPS_MPH) * 10) / 10;
    }

    /**
     * Speed conversion
     *
     * @param mph miles per hour
     * @return meters per second
     */
    public static double mphToMps(double mph) {
        return (double) Math.round((mph / MPS_MPH) * 1000000) / 1000000;
    }

    /**
     * Speed conversion
     *
     * @param mps meters per second
     * @return kilometers per hour
     */
    public static double mpsToKph(double mps) {
        return (double) Math.round((mps * MPS_KPH) * 10) / 10;
    }

    /**
     * Speed conversion
     *
     * @param kph kilometers per hour
     * @return meters per second
     */
    public static double kphToMps(double kph) {
        return (double) Math.round((kph / MPS_KPH) * 1000000) / 1000000;
    }

    /**
     * Distance conversion
     *
     * @param meters meters
     * @return nautical miles
     */
    public static double meToNm(double meters) {
        return (double) Math.round((meters / METER_NAUTICAL_MILE) * 100) / 100;
    }

    /**
     * Distance conversion
     *
     * @param nm nautical miles
     * @return meters
     */
    public static double nmToMe(double nm) {
        return (double) Math.round((nm * METER_NAUTICAL_MILE) * 10) / 10;
    }

    /**
     * Distance conversion
     *
     * @param meters meters
     * @return statute miles
     */
    public static double meToMi(double meters) {
        return (double) Math.round((meters / METER_SATUTE_MILE) * 100) / 100;
    }

    /**
     * Distance conversion
     *
     * @param mi statute miles
     * @return meters
     */
    public static double miToMe(double mi) {
        return (double) Math.round((mi * METER_SATUTE_MILE) * 10) / 10;
    }

    /**
     * Distance conversion
     *
     * @param meters meters
     * @return feet
     */
    public static double meToFt(double meters) {
        return (double) Math.round((meters / METER_FOOT) * 100) / 100;
    }

    /**
     * Distance conversion
     *
     * @param ft feet
     * @return meters
     */
    public static double ftToMe(double ft) {
        return (double) Math.round((ft * METER_FOOT) * 10) / 10;
    }

    /**
     * Distance conversion
     *
     * @param meters meters
     * @return kilometers
     */
    public static double meToKm(double meters) {
        return (double) Math.round((meters / METER_KILOMETER) * 100) / 100;
    }

    /**
     * Distance conversion
     *
     * @param km kilometers
     * @return meters
     */
    public static double kmToMe(double km) {
        return (double) Math.round((km * METER_KILOMETER) * 10) / 10;
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region FIELDS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CONSTRUCTOR ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region ACCESSORS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region PRIVATE METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region PUBLIC METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region INNER CLASSES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region EVENTS  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    /*~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^
                                               ANDROID
    ~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^*/


    //region LIFE CYCLE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region IMPLEMENTATION  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region LISTENERS  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


}
