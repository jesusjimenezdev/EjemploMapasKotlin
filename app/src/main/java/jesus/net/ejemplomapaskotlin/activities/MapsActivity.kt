package jesus.net.ejemplomapaskotlin.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import jesus.net.ejemplomapaskotlin.R

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val sydney = LatLng(-34.0, 151.0)

        // Personalizando el zoom maximo y minimo
        //mMap.setMinZoomPreference(6.0f)
        //mMap.setMaxZoomPreference(14.0f)

        // personalizando el marcador
        /*val marcador: MarkerOptions? = null
        marcador?.position(sydney)
        marcador?.title("Este es mi marcador")
        marcador?.snippet("Aqui puedes poner algun dato de localizacion")
        marcador?.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.btn_star_big_off))
        marcador?.draggable(true)
        mMap.addMarker(marcador)*/


        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        val camara = CameraPosition.Builder()
                .target(sydney)
                .zoom(10f)
                .bearing(0f)
                .tilt(0f)
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camara)) // movimiento de la camara animado
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney).draggable(true))

        // Obtenemos las coordenadas de un punto especifico con pulsacion corta
        mMap.setOnMapClickListener { latLng ->
            Toast.makeText(this, "PC: Las coordenadas son: \n Latitud: ${latLng.latitude} - Longitud: ${latLng.longitude}", Toast.LENGTH_LONG).show()
        }

        // Obtenemos las coordenadas de un punto especifico con pulsacion larga
        mMap.setOnMapLongClickListener { latLng ->
            Toast.makeText(this, "PL: Las coordenadas son: \n Latitud: ${latLng.latitude} - Longitud: ${latLng.longitude}", Toast.LENGTH_LONG).show()
        }

        // Evento que se produce al arrastrar y soltar el marcador
        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {

            override fun onMarkerDragEnd(marker: Marker?) {
                Toast.makeText(this@MapsActivity, "PL: Las coordenadas son: \n Latitud: ${marker!!.position.latitude} - Longitud: ${marker!!.position.longitude}", Toast.LENGTH_LONG).show()
            }

            override fun onMarkerDragStart(marker: Marker?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onMarkerDrag(marker: Marker?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}
