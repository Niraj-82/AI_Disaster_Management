package com.example.aidm // Or your app's package name

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext // Import coroutineContext

class MyCustomService : Service() {

    private val TAG = "MyCustomService"
    private var serviceJob: Job? = null // Coroutine job for background tasks
    private val serviceScope = CoroutineScope(Dispatchers.IO) // Scope for coroutines

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate: Service is being created.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand: Service has been started.")

        val dataString = intent?.getStringExtra("EXTRA_DATA")
        Log.d(TAG, "Data received: $dataString")

        serviceJob = serviceScope.launch {
            performBackgroundTask(dataString)
        }

        return START_NOT_STICKY
    }

    private suspend fun performBackgroundTask(data: String?) {
        Log.d(TAG, "Background task started with data: $data")
        try {
            // Simulate a long-running operation (e.g., network request, file processing)
            for (i in 1..5) {
                // <<< FIX IS HERE: Be explicit about which context's isActive to check.
                // Inside a suspend function, coroutineContext refers to the current coroutine's context.
                if (!coroutineContext.isActive) { // Check if the coroutine/job is still active
                    Log.d(TAG, "Background task cancelled.")
                    break
                }
                Log.d(TAG, "Background task processing: Step $i/5")
                delay(1000) // Simulate 1 second of work
            }
            Log.d(TAG, "Background task completed.")
        } catch (e: CancellationException) {
            Log.d(TAG, "Background task explicitly cancelled (coroutine).")
            throw e // Re-throw CancellationException
        } catch (e: Exception) {
            Log.e(TAG, "Error in background task", e)
        } finally {
            // Important: Stop the service when the task is done if it's a one-off task
            Log.d(TAG, "Stopping service from background task.")
            stopSelf() // Stops the service after the task is complete
        }
    }

    /**
     * Called when an Activity or other component wants to bind to this service
     * using bindService(). If your service does not support binding, return null.
     */
    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "Service onBind: Binding not supported by this service.")
        return null
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy: Service is being destroyed.")
        // Cancel any ongoing coroutines/background tasks
        serviceJob?.cancel()
        // Clean up resources
        super.onDestroy()
    }
}
