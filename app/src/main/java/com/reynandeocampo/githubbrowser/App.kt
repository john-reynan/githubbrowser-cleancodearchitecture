package com.reynandeocampo.githubbrowser

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.reynandeocampo.githubbrowser.di.DaggerMainComponent
import com.reynandeocampo.githubbrowser.di.MainComponent
import com.reynandeocampo.githubbrowser.di.modules.AppModule

class App : Application() {

    lateinit var mainComponent: MainComponent

    override fun onCreate() {
        super.onCreate()

        initializeFresco()
        initializeMainComponent()
    }

    private fun initializeFresco() {
        val config = ImagePipelineConfig.newBuilder(this)
            .setDownsampleEnabled(true)
            .build()

        Fresco.initialize(this, config)
    }

    private fun initializeMainComponent() {
        mainComponent = DaggerMainComponent.builder()
            .appModule(AppModule(applicationContext))
            .build()
    }
}
