package com.mxmariner.fragment;

public enum FragmentId {
    STATION_CARD_RECYCLER_FRAGMENT_TIDES,
    STATION_CARD_RECYCLER_FRAGMENT_CURRENTS,
    MAP_FRAGMENT;

    public static FragmentId defaultId() {
        return STATION_CARD_RECYCLER_FRAGMENT_TIDES;
    }

    public static FragmentId getIdFromString(String name) {
        for (FragmentId id : FragmentId.values()) {
            if (id.name().equalsIgnoreCase(name)) {
                return id;
            }
        }
        return defaultId();
    }
}
