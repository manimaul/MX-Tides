package com.mxmariner.fragment;

public enum MXMainFragmentId {
    STATION_CARD_RECYCLER_FRAGMENT_TIDES,
    STATION_CARD_RECYCLER_FRAGMENT_CURRENTS,
    MAP_FRAGMENT_TIDES,
    MAP_FRAGMENT_CURRENTS;

    public static MXMainFragmentId defaultId() {
        return STATION_CARD_RECYCLER_FRAGMENT_TIDES;
    }

    public static MXMainFragmentId getIdFromString(String name) {
        for (MXMainFragmentId id : MXMainFragmentId.values()) {
            if (id.name().equalsIgnoreCase(name)) {
                return id;
            }
        }
        return defaultId();
    }
}
