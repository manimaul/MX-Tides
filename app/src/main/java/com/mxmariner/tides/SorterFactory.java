package com.mxmariner.tides;

import java.util.Comparator;


public class SorterFactory {
	
	public static Comparator<Station> getStationDistanceSorter() {
		return new StationDistanceSorter();
	}
	
	private static class StationDistanceSorter implements Comparator<Station> {
		@Override
        public int compare(final Station o1, final Station o2) {
            return (o2.getDistance() > o1.getDistance() ? -1 
            		: (o2.getDistance() == o1.getDistance() ? 0 : 1));
        }
	}
	
}
