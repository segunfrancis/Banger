package com.segunfrancis.author.di

import com.segunfrancis.author.domain.AuthorRepository
import com.segunfrancis.author.domain.AuthorRepositoryImpl
import com.segunfrancis.author.ui.AuthorViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authorModule = module {
    single<CoroutineDispatcher> {
        Dispatchers.IO
    }

    singleOf(::AuthorRepositoryImpl) bind AuthorRepository::class

    viewModelOf(::AuthorViewModel)
}
