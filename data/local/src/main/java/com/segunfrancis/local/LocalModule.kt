package com.segunfrancis.local

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val localModule = module {
    single<WDDao> {
        WDDatabase.getDatabase(context = androidApplication().applicationContext).getDao()
    }
}
