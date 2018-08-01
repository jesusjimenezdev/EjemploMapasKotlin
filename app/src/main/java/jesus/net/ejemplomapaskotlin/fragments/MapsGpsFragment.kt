package jesus.net.ejemplomapaskotlin.fragments


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import jesus.net.ejemplomapaskotlin.R
import java.io.IOException
import java.util.*

class MapsGpsFragment : Fragment(), OnMapReadyCallback, LocationListener {

    var miVista: View? = null
    //var geocoder: Geocoder? = null
    var gestorDeLocacion: LocationManager? = null
    var marcador: Marker? = null
    var gMap: GoogleMap? = null
    var locationActual: Location? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        miVista = inflater.inflate(R.layout.fragment_maps_gps, container, false)
        var fab: FloatingActionButton = miVista!!.findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener{
            if(ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return@setOnClickListener
            }

            var location: Location? = gestorDeLocacion!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location == null) {
                location = gestorDeLocacion!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }

            locationActual = location
            if (locationActual != null) {
                actualizarOCrearMarcador(location!!)
                val camara = CameraPosition.Builder()
                        .target(LatLng(location.latitude, location.longitude))
                        .zoom(10f)
                        .bearing(0f)
                        .tilt(0f)
                        .build()
                gMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(camara)) // movimiento de la camara animado
            }
        }

        return miVista
    }

    fun comprobarGPS() {
        try {
            val gps = Settings.Secure.getInt(activity?.contentResolver, Settings.Secure.LOCATION_MODE)
            if(gps == 0) {
                Toast.makeText(context, "Tu GPS esta desactivado, para continuar es necesario activar el GPS", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
    }

    // Cargamos la vista
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapView: MapView?
        mapView = miVista?.findViewById(R.id.mapViewGps) as MapView
        mapView.onCreate(null)
        mapView.onResume()
        mapView.getMapAsync(this)
        comprobarGPS()
    }

    override fun onResume() {
        super.onResume()
        comprobarGPS()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        gMap = googleMap
        gestorDeLocacion = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if(ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        gMap!!.isMyLocationEnabled = true
        //gMap.uiSettings.isMyLocationButtonEnabled = true
        gestorDeLocacion!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
        gestorDeLocacion!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0f, this)
    }

    override fun onLocationChanged(location: Location?) {
        Toast.makeText(context, "Actualizado " + location!!.provider, Toast.LENGTH_SHORT).show()
        actualizarOCrearMarcador(location)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {

    }

    fun actualizarOCrearMarcador(location: Location) {
        if (marcador == null) {
            marcador = gMap!!.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)).draggable(true))
        } else {
            marcador!!.position = LatLng(location.latitude, location.longitude)
        }
    }
}
