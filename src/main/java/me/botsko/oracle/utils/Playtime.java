package me.botsko.oracle.utils;

public class Playtime {
	
	public short days;
	public int hours;
	public int minutes;
	public int seconds;

	/**
	 * 
	 * @param playtime
	 */
	public Playtime( int playtime ){
		splitToComponentTimes( playtime );
	}
	
	/**
	 * @return the hours
	 */
	public int getHours() {
		return hours;
	}

	/**
	 * @return the minutes
	 */
	public int getMinutes() {
		return minutes;
	}

	/**
	 * @return the seconds
	 */
	public int getSeconds() {
		return seconds;
	}
	
	/**
     * Convert seconds into hours/mins/secs
     * 
     * @param playtime
     * @return
     */
    private void splitToComponentTimes(int playtime){
        this.hours = (int) playtime / 3600;
        int remainder = (int) playtime - hours * 3600;
        this.minutes = remainder / 60;
        remainder = remainder - this.minutes * 60;
        this.seconds = remainder;
    }
}