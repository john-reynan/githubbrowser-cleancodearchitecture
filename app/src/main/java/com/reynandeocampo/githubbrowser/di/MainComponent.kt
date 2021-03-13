package com.reynandeocampo.githubbrowser.di

import com.reynandeocampo.githubbrowser.di.modules.AppModule
import com.reynandeocampo.githubbrowser.di.modules.NetworkModule
import com.reynandeocampo.githubbrowser.di.modules.RepositoryModule
import com.reynandeocampo.githubbrowser.di.modules.UseCasesModule
import com.reynandeocampo.githubbrowser.presentation.HomeViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, RepositoryModule::class, UseCasesModule::class])
interface MainComponent {
    fun inject(homeViewModel: HomeViewModel)
}
