package com.mrbreak.todo.jobmanager;

import android.content.Context;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;

public class ToDoJobManager {

    public ToDoJobManager() {
    }

    public JobManager getJobManager(Context context) {
        Configuration.Builder builder = null;
        try {
            builder = new Configuration.Builder(context)
                    .minConsumerCount(1) // always keep at least one consumer alive
                    .maxConsumerCount(3) // up to 3 consumers at a time
                    .loadFactor(3) // 3 jobs per consumer
                    .consumerKeepAlive(120) // wait 2 minute
                    .customLogger(new CustomLogger() {
                        private static final String TAG = "JOBS";

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

                        @Override
                        public void v(String text, Object... args) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert builder != null;
        return new JobManager(builder.build());
    }
}
