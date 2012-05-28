package com.vlille.checker.xml;

import java.io.File;
import java.io.FilenameFilter;

public class StationsFileNameFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String filename) {
		return filename.equals("vlille_stations.xml");
	}

}
