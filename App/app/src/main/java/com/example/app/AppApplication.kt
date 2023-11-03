package com.example.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.app.datalayer.repositories.getCoilImageLoader

class AppApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader =
        getCoilImageLoader(this)
}