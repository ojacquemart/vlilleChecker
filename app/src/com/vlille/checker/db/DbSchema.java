package com.vlille.checker.db;

import java.util.ArrayList;
import java.util.List;

import com.vlille.checker.db.metadata.MetadataTable;
import com.vlille.checker.db.station.StationTable;

public class DbSchema {
	
	public static final int VERSION = 1;
	public static final String DB_NAME = "vlille_checker.db";
	
	private final List<Table> tables = new ArrayList<Table>();
	
	public DbSchema() {
		tables.add(new StationTable());
		tables.add(new MetadataTable());
	}
	
	public List<Table> getTables() {
		return tables;
	}

}
