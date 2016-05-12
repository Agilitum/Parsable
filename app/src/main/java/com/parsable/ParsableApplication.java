package com.parsable;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.parsable.services.ParsableGcmJobService;
import com.parsable.services.ParsableJobService;

/**
 * Created by ludwig on 08/05/16.
 */
public class ParsableApplication extends Application {

	private static ParsableApplication instance;
	private JobManager jobManager;

	public ParsableApplication() {
		instance = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		configureJobManager();
	}

	private void configureJobManager() {
		Configuration.Builder builder = new Configuration.Builder(this)
			.customLogger(new CustomLogger() {
				private static final String TAG = "Jobs";
				@Override
				public boolean isDebugEnabled() {
					return true;
				}

				@Override
				public void d(String text, Object... args) {
					Log.d(TAG, String.format(text, args));
				}

				@Override
				public void e(Throwable t, String text, Object... args) {
					Log.e(TAG, String.format(text, args), t);
				}

				@Override
				public void e(String text, Object... args) {
					Log.e(TAG, String.format(text, args));
				}

			})
			.minConsumerCount(1)
			.maxConsumerCount(3)
			.loadFactor(3)
			.consumerKeepAlive(120);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			builder.scheduler(FrameworkJobSchedulerService.createSchedulerFor(this,
				ParsableJobService.class), false);
		} else {
			int enableGcm = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
			if (enableGcm == ConnectionResult.SUCCESS) {
				builder.scheduler(GcmJobSchedulerService.createSchedulerFor(this,
					ParsableGcmJobService.class), false);
			}
		}
		jobManager = new JobManager(builder.build());

	}

	public JobManager getJobManager() {
		return jobManager;
	}

	public static ParsableApplication getInstance() {
		return instance;
	}
}
