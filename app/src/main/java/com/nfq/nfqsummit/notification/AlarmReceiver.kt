package com.nfq.nfqsummit.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.nfq.nfqsummit.entry.MainActivity
import com.nfq.nfqsummit.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        showNotification(
            context,
            intent.getStringExtra("title") ?: "",
            intent.getStringExtra("body") ?: "",
            intent.getStringExtra("eventId") ?: ""
        )
    }

    private fun showNotification(context: Context, title: String, body: String, eventId: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notifyIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("eventId", eventId)
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            context, System.currentTimeMillis().toInt(), notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "reminder")
            .setContentIntent(notifyPendingIntent)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_stat_nfq)
            .setAutoCancel(true)

        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}