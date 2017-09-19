package com.wdziemia.githubtimes.dagger

import android.content.Context

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wdziemia.githubtimes.BuildConfig
import com.wdziemia.githubtimes.retrofit.GithubApi

import javax.inject.Named
import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


@Module
open class NetworkModule {

    @Provides
    @Singleton
    internal fun provideOkHttpCache(context: Context): Cache {
        return Cache(context.cacheDir, CACHE_SIZE.toLong())
    }

    @Provides
    @Singleton
    @Named("interceptor_logging")
    internal fun provideLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BASIC
        else
            HttpLoggingInterceptor.Level.NONE)
    }

    @Provides
    @Singleton
    @Named("interceptor_header")
    internal fun provideHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                    .addHeader("User-Agent", "GithubTimes-App")
                    .addHeader("Accept", "application/vnd.github.v3+json")
                    .build()
            chain.proceed(newRequest)
        }
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        return GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(cache: Cache, @Named("interceptor_logging") loggingInterceptor: Interceptor,
                                     @Named("interceptor_header") headerInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder().cache(cache)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(headerInterceptor)
                .build()
    }

    @Provides
    @Singleton
    open fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient, provider: SchedulerProvider): GithubApi {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(provider.io()))
                .build()
                .create<GithubApi>(GithubApi::class.java!!)
    }

    @Provides
    @Singleton
    open fun provideSchedulerProvider(): SchedulerProvider {
        return AppSchedulerProvider()
    }

    companion object {
        private val CACHE_SIZE = 10 * 1024 * 1024 // 10MB
    }
}
