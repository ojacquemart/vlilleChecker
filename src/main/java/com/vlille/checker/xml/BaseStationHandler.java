package com.vlille.checker.xml;

import org.xml.sax.helpers.DefaultHandler;

public abstract class BaseStationHandler<T> extends DefaultHandler {

	public abstract T getResult();
	
}
