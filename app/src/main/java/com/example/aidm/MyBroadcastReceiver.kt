package com.example.aidm // Or your app's package name

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.util.Log

class MyBroadcastReceiver : BroadcastReceiver() {

    private val TAG = "MyBroadcastReceiver"
    companion object {
        // Define the custom action string. It's good practice to make it unique,
        // often by prefixing with your app's package name.
        const val CUSTOM_ACTION = "com.example.aidm.action.YOUR_CUSTOM_ACTION"
        const val EXTRA_DATA = "com.example.aidm.extra.DATA"
    }

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
     * It runs on the main thread, so any long-running operations should be offloaded
     * to a background thread, coroutine, or by starting a Service/WorkManager job.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            Log.w(TAG, "Received null context or intent.")
            return
        }

        val action = intent.action
        Log.d(TAG, "Received broadcast with action: $action")

        when (action) {
            CUSTOM_ACTION -> {
                val receivedData = intent.getStringExtra(EXTRA_DATA)
                val message = "Custom Broadcast Received!\nData: ${receivedData ?: "No data"}"

                Log.d(TAG, message)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()

                // --- What to do next? ---
                // You can:
                // 1. Update UI (if the app is in the foreground, though direct UI updates
                //    from a receiver are tricky and often better handled via ViewModel/LiveData/StateFlow).
                // 2. Start an Activity:
                //    val activityIntent = Intent(context, MainActivity::class.java).apply {
                //        flags = Intent.FLAG_ACTIVITY_NEW_TASK // Required when starting Activity from outside an Activity
                //        putExtra("BROADCAST_MESSAGE", message)
                //    }
                //    context.startActivity(activityIntent)
                // 3. Start a Service to perform background work:
                //    val serviceIntent = Intent(context, MyCustomService::class.java).apply {
                //        putExtra(MyCustomService.EXTRA_DATA_FROM_RECEIVER, receivedData)
                //    }
                //    context.startService(serviceIntent)
                // 4. Enqueue work with WorkManager for more robust background tasks.
                // 5. Update a notification.
                // ... and more, depending on your app's needs.
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                // Example: Handling system broadcast for device boot
                // Note: Your manifest would need <action android:name="android.intent.action.BOOT_COMPLETED" />
                // and <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
                Log.d(TAG, "Device has finished booting.")
                // Perhaps schedule some recurring task or initialize something.
            }
            // Add more 'when' cases for other actions this receiver might listen to.
            else -> {
                Log.w(TAG, "Received an unhandled action: $action")
            }
        }
    }
}

