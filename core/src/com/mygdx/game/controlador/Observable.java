package com.mygdx.game.controlador;

import java.util.LinkedList;
import java.util.List;

public class Observable<O> {
	public Observable() {
		obs = new LinkedList<O>();
	}
	public void addObserver(O o) {
		obs.add(o);
	}
	
	public void removeObserver(O o) {
		obs.remove(o);
	}
	
	protected List<O> obs;
}
