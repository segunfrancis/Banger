package com.segunfrancis.favourites.ui.di

import com.segunfrancis.favourites.ui.domain.FavouriteRepository
import com.segunfrancis.favourites.ui.domain.FavouriteRepositoryImpl
import com.segunfrancis.favourites.ui.ui.FavouriteViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val favouriteModule = module {
    viewModelOf(::FavouriteViewModel)
    singleOf(::FavouriteRepositoryImpl) bind FavouriteRepository::class
}
