package jesus.net.ejemplomapaskotlin.fragments


import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import jesus.net.ejemplomapaskotlin.R
import java.io.IOException
import java.util.*


class MapsFragment : Fragment(), OnMapReadyCallback {

    var miVista: View? = null
    var geocoder: Geocoder? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        miVista = inflater.inflate(R.layout.fragment_maps, container, false)
        return miVista
    }

    // Cargamos la vista
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapView: MapView?
        mapView = miVista?.findViewById(R.id.mapView) as MapView
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
        val gMap: GoogleMap? = googleMap
        val sydney = LatLng(-34.0, 151.0)
        gMap!!.addMarker(MarkerOptions().position(sydney).title("Estoy en sydney").draggable(true))
        gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        // Evento que se produce al arrastrar y soltar el marcador
        gMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {

            override fun onMarkerDragEnd(marker: Marker?) {
                var direccion: List<Address>? = null
                var latitud = marker!!.position.latitude
                var longitud = marker!!.position.longitude

                try {
                    direccion = geocoder!!.getFromLocation(latitud, longitud, 1)

                }catch (ex: IOException) {
                    ex.stackTrace
                }

                var pais = ""
                var estado = ""
                var ciudad = ""
                var calle = ""
                var codigoPostal = ""
                if(direccion!!.size > 0) {
                    pais = direccion!![0].countryName
                    estado = direccion[0].adminArea
                    ciudad = direccion[0].locality
                    calle = direccion[0].getAddressLine(0)
                    codigoPostal = direccion[0].postalCode

                    marker.title = ciudad
                    marker.snippet = calle

                    Toast.makeText(context, "La locacion es: \n" +
                            "pais: " + pais + "\n" +
                            "estado" + estado + "\n" +
                            "ciudad: " + ciudad + "\n" +
                            "calle: " + calle + "\n" +
                            "CP: " + codigoPostal, Toast.LENGTH_LONG).show()
                } else {
                    marker.title = "Sin ubicacion"
                    marker.snippet = "Sin ubicacion"

                    Toast.makeText(context, "Esta direccion no tiene informacion, elige otra", Toast.LENGTH_LONG).show()
                }
            }

            override fun onMarkerDragStart(marker: Marker?) {
                marker?.hideInfoWindow()
            }

            override fun onMarkerDrag(marker: Marker?) {

            }
        })
        // Obtener localizacion del mapa
        geocoder = Geocoder(context, Locale.getDefault())
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

}
