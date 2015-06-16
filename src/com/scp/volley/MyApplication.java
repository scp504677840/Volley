package com.scp.volley;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.app.Application;

public class MyApplication extends Application {
	public static RequestQueue requestQueue;

	@Override
	public void onCreate() {
		super.onCreate();
		requestQueue = Volley.newRequestQueue(getApplicationContext());
	}

	public static RequestQueue getHttpQueue() {
		return requestQueue;
	}
}
