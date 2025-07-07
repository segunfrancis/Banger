package com.segunfrancis.author_details.di

import com.segunfrancis.author_details.domain.AuthorDetailsRepository
import com.segunfrancis.author_details.domain.AuthorDetailsRepositoryImpl
import com.segunfrancis.author_details.domain.data.AuthorDetailsApi
import com.segunfrancis.author_details.ui.AuthorDetailsViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val authorDetailsModule = module {

    single<AuthorDetailsApi> {
        val retrofit = get<Retrofit>()
        retrofit.create(AuthorDetailsApi::class.java)
    }

    single<CoroutineDispatcher> {
        Dispatchers.IO
    }

    singleOf(::AuthorDetailsRepositoryImpl) bind AuthorDetailsRepository::class

    viewModelOf(::AuthorDetailsViewModel)
}
