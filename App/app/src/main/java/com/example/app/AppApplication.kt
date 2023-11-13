package com.example.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.app.datalayer.repositories.getCoilImageLoader
import io.paperdb.Paper

class AppApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        Paper.init(applicationContext);
    }

    override fun newImageLoader(): ImageLoader =
        getCoilImageLoader(this)
}