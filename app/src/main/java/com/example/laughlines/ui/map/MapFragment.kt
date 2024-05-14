package com.example.laughlines.ui.map

import android.app.AlertDialog
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.net.Uri
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.laughlines.R
import com.example.laughlines.base.BaseFragment
import com.example.laughlines.databinding.FragmentMapBinding
import com.example.laughlines.utils.Constant
import com.example.laughlines.utils.PermissionUtils
import com.example.laughlines.utils.extensions.vectorToBitmap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>(), OnMapReadyCallback, SensorEventListener {
    override val layoutId: Int = R.layout.fragment_map

    private var sensorManager: SensorManager? = null
    private var compassSensor: Sensor? = null
    private var floatGravity = FloatArray(3)
    private var floatGeoMagnetic = FloatArray(3)
    private var floatOrientation = FloatArray(3)
    private var azimuth = 0f
    private var correctAzimuth = 0f
    private var zoom = 17f

    private var mMap: GoogleMap? = null
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var originalLongitude: Double = 0.0
    private var originalLatitude: Double = 0.0
    private var originalLatLng: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var iconMarker: BitmapDescriptor
    private lateinit var iconSelectedMarker: BitmapDescriptor
    private lateinit var myLocationMarker: MarkerOptions
    private lateinit var myFriendLocationMarker: MarkerOptions
    private var geocoder: Geocoder? = null

    private var isDialogShowing = false
    private val arg: MapFragmentArgs by navArgs()

    override fun initData() {
        super.initData()
        val latLng = arg.location
        val field = latLng.split(" ")
        longitude = field[0].toDouble()
        latitude = field[1].toDouble()


    }

    override fun initView() {
        super.initView()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        iconMarker = vectorToBitmap(requireContext(), R.drawable.ic_vector_direction)
        iconSelectedMarker = vectorToBitmap(requireContext(), R.drawable.ic_curent_location_marker_2)

        geocoder = Geocoder(requireContext())
        val addresses = geocoder!!.getFromLocation(latitude, longitude, 1)
        binding.tvAddress.text = addresses?.get(0)?.getAddressLine(0).toString()

        if (PermissionUtils.checkPermission(Constant.PER_FINE_LOCATION, requireContext())) {
            sensorManager = requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager
            compassSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION)
            compassSensor?.let { sensor ->
                sensorManager!!.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
            initSensor()
            initLocation()
        } else {
            requestPermissions(arrayOf(Constant.PER_FINE_LOCATION), Constant.PER_LOCATION_CODE)
        }
    }

    override fun initAction() {
        super.initAction()
        binding.btnBack.setOnClickListener { requireView().findNavController().popBackStack() }
        binding.btnSeeTheWay.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=${originalLatitude},${originalLongitude}&daddr=${latitude},${longitude}"))
            startActivity(intent)
        }
    }

    override fun onStop() {
        super.onStop()
        destroySensor()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap!!.uiSettings.isCompassEnabled = false
        mMap!!.mapType = GoogleMap.MAP_TYPE_TERRAIN

        myLocationMarker = MarkerOptions().position(LatLng(originalLatitude, originalLongitude)).icon(iconMarker)
        mMap!!.addMarker(myLocationMarker)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))
        myFriendLocationMarker = MarkerOptions().position(LatLng(latitude, longitude)).icon(iconSelectedMarker)
        mMap!!.addMarker(myFriendLocationMarker)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val alpha = 0.97f
        synchronized(this) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                floatGravity[0] = alpha * floatGravity[0] + (1 - alpha) * event.values[0]
                floatGravity[1] = alpha * floatGravity[1] + (1 - alpha) * event.values[1]
                floatGravity[2] = alpha * floatGravity[2] + (1 - alpha) * event.values[2]
            }
            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                floatGeoMagnetic[0] = alpha * floatGeoMagnetic[0] + (1 - alpha) * event.values[0]
                floatGeoMagnetic[1] = alpha * floatGeoMagnetic[1] + (1 - alpha) * event.values[1]
                floatGeoMagnetic[2] = alpha * floatGeoMagnetic[2] + (1 - alpha) * event.values[2]
            }
            val rotationFloatArray = FloatArray(9)
            val inclinationFloatArray = FloatArray(9)

            if (SensorManager.getRotationMatrix(rotationFloatArray, inclinationFloatArray, floatGravity, floatGeoMagnetic)) {
                SensorManager.getOrientation(rotationFloatArray, floatOrientation)
                startCompass()
            }

            if (mMap != null) {
                if (event.sensor?.type == Sensor.TYPE_ORIENTATION) {
                    val degree = event.values[0]
//                    mMap!!.addMarker(marker)
                    rotateMap(degree)
                }
            }
        }
    }

    private fun rotateMap(degree: Float) {
        val cameraPosition = mMap?.cameraPosition

        val cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.builder(cameraPosition).bearing(degree).build())

        mMap?.animateCamera(cameraUpdate, 200, null)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constant.PER_LOCATION_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val mapFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                    initSensor()
                    initLocation()
                    if (sensorManager != null) {
                        compassSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION)
                        compassSensor?.let { sensor ->
                            sensorManager!!.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
                        }
                    }
                } else {
                    if (!isDialogShowing) {
                        isDialogShowing = true
                        showGoToSettingDialog()
                    }
                }
            }
        }
    }

    private fun startCompass() {
        azimuth = Math.toDegrees(floatOrientation[0].toDouble()).toFloat()
        azimuth = (azimuth + 360) % 360
        val anim: Animation = RotateAnimation(-correctAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        correctAzimuth = azimuth
        anim.duration = 100
        anim.repeatCount = 0
        anim.fillAfter = true
        binding.imgCompass.startAnimation(anim)
    }

    private fun initSensor() {
        if (sensorManager != null) {
            val sensorAccelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val sensorMagneticField = sensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            if (sensorAccelerometer != null && sensorMagneticField != null) {
                sensorManager!!.registerListener(this, sensorMagneticField, SensorManager.SENSOR_DELAY_GAME)
                sensorManager!!.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_GAME)
            } else {
                notify(getString(R.string.text_no_compass))
            }
        }
    }

    private fun initLocation() {
        if (!PermissionUtils.checkPermission(Constant.PER_FINE_LOCATION, requireContext())) {
            return
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    originalLongitude = location.longitude
                    originalLatitude = location.latitude
                    originalLatLng = LatLng(originalLatitude, originalLongitude)
                    myLocationMarker = MarkerOptions().position(originalLatLng!!).icon(iconMarker)
                    if (mMap != null) {
                        mMap?.clear()
                        mMap?.addMarker(myLocationMarker)
                        onMapReady(mMap!!)
                    }
                }
            }.addOnFailureListener {
                Log.e("Dunno", it.message.toString())
            }
        }
    }

    private fun destroySensor() {
        if (sensorManager != null) {
            sensorManager!!.unregisterListener(this)
        }
    }

    private fun showGoToSettingDialog() {
        isDialogShowing = true
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.requested_permission_message)).setTitle(getString(R.string.requested_permission_title)).setCancelable(false).setPositiveButton(Html.fromHtml("<font color='#21A884'>${getString(R.string.permission_setting)}</font>")) { p0, _ ->
            p0.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setOnDismissListener {
            isDialogShowing = false
            onResume()
        }
        builder.show()
    }
}