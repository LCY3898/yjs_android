package com.cy.wbs;

import android.content.Context;

public class WSLibInitializer {
	private static Context sContext;

	public static void init(Context context) {
		sContext = context;
	}

	synchronized public static Context getAppContext() {
		return sContext;
	}

	public static final boolean IS_DEBUG = true;
}
