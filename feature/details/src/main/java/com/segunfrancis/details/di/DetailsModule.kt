package com.segunfrancis.details.di

import com.segunfrancis.details.domain.DetailsRepository
import com.segunfrancis.details.domain.DetailsRepositoryImpl
import com.segunfrancis.details.domain.data.DetailsApi
import com.segunfrancis.details.ui.DetailsViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val detailsModule = module {

    single<DetailsApi> {
        val retrofit = get<Retrofit>()
        retrofit.create(DetailsApi::class.java)
    }

    single<CoroutineDispatcher> {
        Dispatchers.IO
    }

    singleOf(::DetailsRepositoryImpl) bind DetailsRepository::class

    viewModelOf(::DetailsViewModel)
}
