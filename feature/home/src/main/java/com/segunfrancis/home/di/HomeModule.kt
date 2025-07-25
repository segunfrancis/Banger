package com.segunfrancis.home.di

import com.segunfrancis.home.domain.HomeApi
import com.segunfrancis.home.domain.HomeRepository
import com.segunfrancis.home.domain.HomeRepositoryImpl
import com.segunfrancis.home.domain.HomeUseCase
import com.segunfrancis.home.ui.CategoriesDetailsViewModel
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

    singleOf(::HomeUseCase)

    viewModelOf(::CategoriesDetailsViewModel)
}
