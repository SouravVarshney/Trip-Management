package com.app.tripmanagementapp

import android.Manifest
import android.R
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.app.tripmanagementapp.data.entities.TripDTO
import com.app.tripmanagementapp.databinding.ActivityMainBinding
import com.app.tripmanagementapp.model.LocationModel
import com.app.tripmanagementapp.model.TripModel
import com.app.tripmanagementapp.utility.*
import com.app.tripmanagementapp.utility.AppConstants.PERMISSIONS.Companion.REQUEST_CHECK_SETTINGS
import com.app.tripmanagementapp.viewmodel.TripViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<TripViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tripId: MutableList<String> = mutableListOf()
        tripId.add("Select Trip")
        tripId.add("All Trip Data")
        viewModel.getAllTripId().observe(this, Observer {
            if (it.isNotEmpty()) {
                tripId.addAll(it)
            }
        })
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0 && position != 1) {
                    val item: String = parent?.getItemAtPosition(position).toString()
                    viewModel.getLocationTripById(item).observe(this@MainActivity, Observer {
                        dataExport(it, binding)
                    })

                } else if (position == 1) {
                    /*viewModel.getAllTripId().observe(this@MainActivity, Observer {
                        if (it.isNotEmpty()) {
                            allTripdataExportNew(it, binding)
                        }
                    })*/
                    val tripIdNew: MutableList<String> = mutableListOf()
                    tripIdNew.clear()
                    tripIdNew.addAll(tripId)
                    tripIdNew.removeFirst()
                    tripIdNew.removeFirst()
                    allTripdataExportNew(tripIdNew, binding);
                    /*viewModel.getAllSavedLocation().observe(this@MainActivity, Observer {
                        dataExport(it, binding)
                    })*/
                } else {
                    binding.txtDataExport.text = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

        }
        val dataAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, tripId)
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = dataAdapter
        binding.btnStart.setOnClickListener {
            checkLocationPermission()
        }
        binding.btnStop.setOnClickListener {
            /*viewModel.getAllSavedLocation().observe(this, Observer {
                it
            })*/
            viewModel.getAllTripId().observe(this, {
                if (it.isNotEmpty()) {
                    tripId.clear()
                    tripId.add("Select Trip")
                    tripId.add("All Trip Data")
                    tripId.addAll(it)
                }
            })
            viewModel.stopTrackLocation()
        }

        viewModel.enableLocation.observe(this) { response ->
            when (response) {
                is Result.Success -> {
                    response.data?.let {
                        getLocation()
                    }
                }
                is Result.Error -> {
                    response.error ?: return@observe
                    if (response.error is ResolvableApiException) {
                        try {
                            response.error.startResolutionForResult(
                                this@MainActivity,
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (sendEx: IntentSender.SendIntentException) {

                        }
                    }
                }

                is Result.NoInternet -> {
                    response.message ?: return@observe
                    toast(response.message)
                }

                is Result.Loading -> {
                    //show loader, shimmer effect etc
                }

            }
        }

    }

    /**
     * This method is used for location runtime permission
     */
    private fun checkLocationPermission() {
        if (!AppUtils.isLocationPermissionEnabled()) {
            val permissions: Array<String> = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(
                this,
                permissions,
                AppConstants.PERMISSIONS.LOCATION_PERMISSION
            )
        } else {
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppConstants.PERMISSIONS.LOCATION_PERMISSION) {
            if (AppUtils.isLocationPermissionEnabled()) {
                getLocation()
            }
        }
    }

    /**
     * Check whether GPS is enabled or not , If enable then go for track location using wm
     */
    private fun getLocation() =
        if (isLocationEnabled().not()) viewModel.locationSetup() else viewModel.trackLocation()

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == -1) {
                getLocation()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun dataExport(list: List<TripDTO>, binding: ActivityMainBinding) {
        val locationList = mutableListOf<LocationModel>()
        for (item in list.indices) {
            val locationModel = LocationModel(
                list[item].latitude,
                list[item].longitude,
                AppUtils.millisTodate(list[item].timestamp),
                list[item].accuracy
            )
            locationList.add(locationModel)
        }
        val tripModel = TripModel(
            list[0].trip_id,
            AppUtils.millisTodate(list[0].timestamp),
            locationList[locationList.size - 1].timestamp,
            locationList
        )
        val gson: Gson = GsonBuilder().create()
        val json: String = gson.toJson(tripModel)

        binding.txtDataExport.text = json
    }

    private fun allTripdataExportNew(list: List<String>, binding: ActivityMainBinding) {
        val allTripList = mutableListOf<TripModel>()
        for (tripIdNew in list.indices) {
            var tripModel: TripModel? = null
            val data: List<TripDTO?> = viewModel.getLocationTripByIdWithoutLiveData(list[tripIdNew])
            tripModel = allTripdataExport(data, binding)
            tripModel?.let { allTripList.add(it) }
        }
        val gson: Gson = GsonBuilder().create()
        val json: String = gson.toJson(allTripList)
        binding.txtDataExport.text = json
    }

    private fun allTripdataExport(list: List<TripDTO?>, binding: ActivityMainBinding): TripModel {
        val locationList = mutableListOf<LocationModel>()
        for (item in list.indices) {
            val locationModel = LocationModel(
                list[item]!!.latitude,
                list[item]!!.longitude,
                AppUtils.millisTodate(list[item]!!.timestamp),
                list[item]!!.accuracy
            )
            locationList.add(locationModel)
        }
        val tripModel = TripModel(
            list[0]!!.trip_id,
            AppUtils.millisTodate(list[0]!!.timestamp),
            locationList[locationList.size - 1].timestamp,
            locationList
        )
        return tripModel
    }

}