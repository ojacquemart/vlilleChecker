package com.vlille.checker.db;

public interface DB extends org.droidparts.contract.DB {

    int VERSION = 2;
    String FILE = "vlille_checker.db";

    interface Table extends org.droidparts.contract.DB.Table {
        String STATION = "station";
        String METADATA = "vlille_metadata";
    }

}
