package com.rt.iNavigation.indoor

import android.app.Application
import android.content.res.Configuration
import androidx.annotation.NonNull
import com.mapsindoors.mapssdk.MapsIndoors

class IApplication : Application(){
    private var sInstance: Application? = null

    override fun onCreate() {
        super.onCreate()

        sInstance = this


        // Initialize the MapsIndoors SDK here by providing:
        // - The application context
        // - The MapsIndoors API key
        MapsIndoors.initialize(applicationContext, getString(R.string.mi_api_key))

        // Your Google Maps API key
        MapsIndoors.setGoogleAPIKey(getString(R.string.google_maps_key))
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    override fun onLowMemory() {
        super.onLowMemory()
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        MapsIndoors.onApplicationConfigurationChanged(newConfig)
    }

    @NonNull
    fun getInstance(): Application {
        return sInstance!!
    }


}