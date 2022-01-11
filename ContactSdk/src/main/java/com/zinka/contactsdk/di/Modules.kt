package com.zinka.contactsdk.di

import com.zinka.contactsdk.data.Repository
import com.zinka.contactsdk.data.local.LocalDataSource
import com.zinka.contactsdk.data.remote.RemoteDataSource
import org.koin.dsl.module


/**
 * Created by AbhayPratap on 3,January,2022
 */

internal val applicationModule = module {
    single { Repository(get(), get()) }
    single { LocalDataSource.getInstance(get()) }
    single { RemoteDataSource.getInstance(get()) }
}