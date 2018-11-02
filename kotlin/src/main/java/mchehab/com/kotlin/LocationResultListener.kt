package mchehab.com.kotlin

import android.location.Location

interface LocationResultListener {
    fun getLocation(location: Location)
}