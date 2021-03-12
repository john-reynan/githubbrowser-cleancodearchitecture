package com.reynandeocampo.githubbrowser.di.modules

import com.reynandeocampo.data.UseCases
import com.reynandeocampo.domain.repositories.GitRepoRepository
import com.reynandeocampo.domain.usecases.SearchGitRepo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UseCasesModule {

    @Singleton
    @Provides
    fun getUseCases(gitRepoRepository: GitRepoRepository) = UseCases(SearchGitRepo(gitRepoRepository))
}
