package me.botsko.oracle.utils;

import java.util.Date;

public class Warning {
	
	public final int id;
	public final Date dateFiled;
	public final String username;
	public final String moderator;
	public final String reason;
	
	/**
	 * 
	 * @param id
	 * @param epoch
	 * @param username
	 * @param reason
	 * @param moderator
	 */
	public Warning( int id, Long epoch, String username, String reason, String moderator ){
	    this.dateFiled = new Date(epoch * 1000);
		this.id = id;
		this.username = username;
		this.moderator = (moderator == null ? "console" : moderator);
		this.reason = reason;
	}
}