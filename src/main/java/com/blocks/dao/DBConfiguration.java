package com.blocks.dao;

public class DBConfiguration {

	private final String jdbcDriver;
	private final String dbUrl;
	private final String dbUserid;
	private final String dbPassword;
	private final boolean kerberised;
	private final String principal;
	private final String keytab;

	public DBConfiguration(String jdbcDriver, String dbUrl, String dbUserid, String dbPassword, boolean kerberised,
			String principal, String keytab) {
		super();
		this.jdbcDriver = jdbcDriver;
		this.dbUrl = dbUrl;
		this.dbUserid = dbUserid;
		this.dbPassword = dbPassword;
		this.kerberised = kerberised;
		this.principal = principal;
		this.keytab = keytab;
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

	public boolean isKerberised() {
		return kerberised;
	}

	public String getPrincipal() {
		return principal;
	}

	public String getKeytab() {
		return keytab;
	}
}
