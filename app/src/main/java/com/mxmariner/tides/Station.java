package com.mxmariner.tides;

public class Station {
	public static final String TYPE_CURRENT = "Current Station";
	public static final String TYPE_TIDE = "Tide Station";
	private String name;
	private int latE6;
	private int lngE6;
	private float distance;
	private String about;
	private String time;
	private String[] data;
	private String precition;

	private String[] rawData;

	public Station() {
		
	}
	
	/**
	 * 
	 * @param xtideStr ex "some station name;45.243829;-122.193847"
	 */
	public Station(String xtideStr) {
		String[] data = xtideStr.split(";");
		setName(data[0]);
		setLatE6(data[1]);
		setLngE6(data[2]);
	}
	
	public void setAbout(final String about) {
		this.about = about;
	}
	
	public String getAbout() {
		return this.about;
	}
	
	public String getName() {
		return name;
	}
	
	public String getStationType() {
		if (name.contains("Current")) {
			return TYPE_CURRENT;
		}
		return TYPE_TIDE;
	}
	
	public void setName(String name) {
		this.name = name.trim();
	}
	
	public int getLatE6() {
		return latE6;
	}
	
	public void setLatE6(String lat) {
		this.latE6 = (int) (Double.parseDouble(lat) * 1E6);
	}
	
	public int getLngE6() {
		return lngE6;
	}
	
	public void setLngE6(String lng) {
		this.lngE6 = (int) (Double.parseDouble(lng) * 1E6);
	}
	
	public float getDistance() {
		return distance;
	}

	public void setDistance(final float distance) {
		this.distance = distance;
	}
	
	public String[] getRawData() {
		return rawData;
	}
	
	public void setRawData(String data) {
		rawData = data.split("\n");
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time.trim();
	}

	public String[] getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data.split("\n");
	}

	public String getPrecition() {
		return precition;
	}

	public void setPrecition(String precition) {
		this.precition = precition.trim();
	}
}
