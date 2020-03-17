package com.ingjuanocampo.jstunner

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.concurrent.TimeUnit

/**
 * The Service that JobScheduler runs once the conditions are met.
 * In this case it posts a notification.
 */
class NotificationJobService : JobService() {
    private var task: AsyncTask<Void, Void, Void>? = null
    // Notification manager.
    var mNotifyManager: NotificationManager? = null

    /**
     * Called by the system once it determines it is time to run the job.
     *
     * @param jobParameters Contains the information about the job.
     * @return Boolean indicating whether or not the job was offloaded to a
     * separate thread.
     * In this case, it is false since the notification can be posted on
     * the main thread.
     */
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        createNotificationAndNotify("Job starting")

        task = object: AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                Thread.sleep(TimeUnit.SECONDS.toMillis(10))
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                createNotificationAndNotify("Job done !")
                task = null
                jobFinished(jobParameters, false)
            }

        }
        task?.execute()
        return true
    }

    private fun createNotificationAndNotify(message: String) {

        // Create the notification channel.
        createNotificationChannel()
        // Set up the notification content intent to launch the app when // clicked.
        val contentPendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder =
            NotificationCompat.Builder(this,
                PRIMARY_CHANNEL_ID
            )
                .setContentTitle(getString(R.string.job_service))
                .setContentText(message)
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
        mNotifyManager!!.notify(0, builder.build())
    }

    /**
     * Called by the system when the job is running but the conditions are no
     * longer met.
     * In this example it is never called since the job is not offloaded to a
     * different thread.
     *
     * @param jobParameters Contains the information about the job.
     * @return Boolean indicating whether the job needs rescheduling.
     */
    override fun onStopJob(jobParameters: JobParameters): Boolean {
        task?.cancel(true)
        createNotificationAndNotify("Job has been cancelled")
        return true
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    fun createNotificationChannel() { // Create a notification manager object.
        mNotifyManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Notification channels are only available in OREO and higher.
// So, add a check on SDK version.
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) { // Create the NotificationChannel with all the parameters.
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                getString(R.string.job_service_notification),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_channel_description)
            mNotifyManager!!.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        // Notification channel ID.
        private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }
}