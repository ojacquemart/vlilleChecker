package com.vlille.checker.db;

import java.util.ArrayList;
import java.util.List;

public class DbSchema {
	
	public static final int VERSION = 1;
	public static final String DB_NAME = "db_vlille_checker";
	
	private final List<Table> tables = new ArrayList<Table>();
	
	public DbSchema() {
		tables.add(new StationTable());
		tables.add(new MapInfosTable());
	}
	
	public List<Table> getTables() {
		return tables;
	}

}
