package com.rt.iNavigation.indoor

 import android.content.Context
 import android.os.Bundle
 import android.os.Handler
 import android.view.LayoutInflater
 import android.view.View
 import android.view.ViewGroup
 import android.widget.FrameLayout
 import androidx.annotation.NonNull
 import androidx.core.content.ContextCompat.getColor
 import androidx.fragment.app.Fragment
 import com.google.android.gms.maps.CameraUpdateFactory
 import com.google.android.gms.maps.GoogleMap
 import com.google.android.gms.maps.SupportMapFragment
 import com.google.android.gms.maps.model.LatLng
 import com.mapsindoors.mapssdk.*

class IndoorMapFragment : Fragment() {

    lateinit var  mContainer : FrameLayout
    lateinit var  mapFragment: SupportMapFragment
    lateinit var  mGoogleMap: GoogleMap
    private val VENUE_LAT_LNG = LatLng(57.05813067, 9.95058065)
    var mMapControl: MapControl? = null
    lateinit var mRoutingProvider: RoutingProvider
    lateinit var mRoutingRenderer: MPDirectionsRenderer


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        mContainer = inflater.inflate(R.layout.map_container,container,false) as FrameLayout
       // setupMap(mContainer)

        return  mContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap(view)
    }

    private fun setupMap(viewGroup: View) {
        val fm = childFragmentManager
        mapFragment = fm.findFragmentById(R.id.mapfragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VENUE_LAT_LNG,13.0f))
            setupMapsIndoors()
        }
    }

     fun setupMapsIndoors() {
        val context = activity

        if (context == null || mapFragment == null || mapFragment.getView() == null) {
            return
        }

        if (!MapsIndoors.getAPIKey().equals(getString(R.string.mi_api_key), ignoreCase = true)) {
            MapsIndoors.setAPIKey(getString(R.string.mi_api_key))
        }

        mMapControl = MapControl(context)
        mMapControl?.setGoogleMap(mGoogleMap, mapFragment.getView()!!)

        mRoutingProvider = MPRoutingProvider()

        setupRouteRenderer(context)

        mMapControl?.init { miError ->

            if (miError == null) {
                val _context = activity
                if (_context != null) {
                    // Setting the floor level programmatically
                    mMapControl?.selectFloor(1)

                    // Make the route
                    //mGoogleMap.animateCamera( CameraUpdateFactory.newLatLngZoom( VENUE_LAT_LNG, 19f ) );

                    // Wait a bit before create/render the route
                    Handler(_context.mainLooper).postDelayed(Runnable { this.routing() }, 2000)
                }
            }
        }
    }

    internal fun setupRouteRenderer(@NonNull context: Context) {
        mRoutingRenderer = MPDirectionsRenderer(context, mGoogleMap, mMapControl, null)

        mRoutingRenderer.setPrimaryColor(getColor(context, R.color.colorPrimary))
        mRoutingRenderer.setAccentColor(getColor(context, R.color.colorAccent))
        mRoutingRenderer.setTextColor(getColor(context, android.R.color.white))

        mRoutingRenderer.setAnimated(true)
    }

    internal fun routing() {
        mRoutingProvider.setOnRouteResultListener { route, error ->
            if (route != null) {
                mRoutingRenderer.setRoute(route)

                val activity = activity
                activity?.runOnUiThread { mRoutingRenderer.setRouteLegIndex(0) }
            } else {
                // Can't get a route between the given points
            }
        }

        val origin = Point(57.057917, 9.950361, 1.0)
        val destination = Point(57.058038, 9.950509, 1.0)

        mRoutingProvider.setTravelMode(TravelMode.WALKING)
        mRoutingProvider.query(origin, destination)
    }
}