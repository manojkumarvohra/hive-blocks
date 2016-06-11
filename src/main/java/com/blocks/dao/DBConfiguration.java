package com.blocks.dao;

public class DBConfiguration {

	private final String jdbcDriver;
	private final String dbUrl;
	private final String dbUserid;
	private final String dbPassword;

	public DBConfiguration(String jdbcDriver, String dbUrl, String dbUserid, String dbPassword) {
		super();
		this.jdbcDriver = jdbcDriver;
		this.dbUrl = dbUrl;
		this.dbUserid = dbUserid;
		this.dbPassword = dbPassword;
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public String getDbUserid() {
		return dbUserid;
	}

	public String getDbPassword() {
		return dbPassword;
	}

}
