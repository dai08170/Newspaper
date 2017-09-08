package com.github.ayltai.newspaper.net;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import com.github.ayltai.newspaper.BuildConfig;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@Module
public final class HttpModule {
    public static final int TIMEOUT_CONNECT = 10;
    public static final int TIMEOUT_READ    = 30;
    public static final int TIMEOUT_WRITE   = 30;

    private HttpModule() {
    }

    @Singleton
    @Provides
    static OkHttpClient provideHttpClient() {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .connectTimeout(HttpModule.TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(HttpModule.TIMEOUT_READ, TimeUnit.SECONDS)
            .writeTimeout(HttpModule.TIMEOUT_WRITE, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

        return builder.build();
    }

    @Singleton
    @Provides
    static Retrofit provideRetrofit() {
        return new Retrofit.Builder()
            .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .client(DaggerHttpComponent.builder()
                .build()
                .httpClient())
            .build();
    }

    @Singleton
    @Provides
    static ApiService provideApiService() {
        return DaggerHttpComponent.builder()
            .build()
            .retrofit()
            .create(ApiService.class);
    }
}
