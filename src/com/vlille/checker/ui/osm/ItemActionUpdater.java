package com.vlille.checker.ui.osm;

import com.vlille.checker.model.Station;
import com.vlille.checker.ui.osm.overlay.MaskableOverlayItem;

interface ItemActionUpdater {

	boolean isValid();

	boolean canUpdate(MaskableOverlayItem item, Station station);

	void whenDraw();

	void whenNotDraw();

}