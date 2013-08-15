package com.vlille.checker.db;

public interface DB extends org.droidparts.contract.DB {

    int VERSION = 1;
    String FILE = "vlille_checker.db";

    public interface Table extends org.droidparts.contract.DB.Table {
        String STATION = "station";
        String METADATA = "vlille_metadata";
    }

}
