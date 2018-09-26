package com.nmbs;

import android.app.Activity;
import com.robotium.recorder.executor.Executor;

@SuppressWarnings("rawtypes")
public class StartActivityExecutor extends Executor {

	@SuppressWarnings("unchecked")
	public StartActivityExecutor() throws Exception {
		super((Class<? extends Activity>) Class.forName("com.nmbs.activities.StartActivity"),  "com.nmbs.R.id.", new android.R.id(), false, false, "1469007508074");
	}

	public void setUp() throws Exception { 
		super.setUp();
	}
}
