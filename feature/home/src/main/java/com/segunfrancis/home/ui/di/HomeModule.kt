package com.segunfrancis.home.ui.di

import com.segunfrancis.home.ui.domain.HomeApi
import com.segunfrancis.home.ui.domain.HomeRepository
import com.segunfrancis.home.ui.domain.HomeRepositoryImpl
import com.segunfrancis.home.ui.ui.HomeViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val homeModule = module {

    single<HomeApi> {
        get<Retrofit>().create(HomeApi::class.java)
    }

    single<CoroutineDispatcher> {
        Dispatchers.IO
    }

    singleOf(::HomeRepositoryImpl) bind HomeRepository::class

    viewModelOf(::HomeViewModel)
}
