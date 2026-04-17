package com.localhelp.app.di

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.localhelp.app.data.remote.AuthInterceptor
import com.localhelp.app.data.remote.AuthService
import com.localhelp.app.data.remote.CategoryService
import com.localhelp.app.data.remote.CloudinaryService
import com.localhelp.app.data.remote.ConversationService
import com.localhelp.app.data.remote.FinanceService
import com.localhelp.app.data.remote.JobDetailApiService
import com.localhelp.app.data.remote.JobService
import com.localhelp.app.data.remote.ReviewService
import com.localhelp.app.data.remote.TokenAuthenticator
import com.localhelp.app.data.remote.TrackAsiaApiService
import com.localhelp.app.data.remote.UserService
import com.localhelp.app.model.constant.ApiConstants
import com.localhelp.app.utils.ManeuverModifierDeserializer
import com.localhelp.app.utils.ManeuverTypeDeserializer
import com.localhelp.app.utils.PointDeserializer
import com.trackasia.geojson.Point
import com.trackasia.navigation.android.navigation.v5.models.DirectionsRoute
import com.trackasia.navigation.android.navigation.v5.models.ManeuverModifier
import com.trackasia.navigation.android.navigation.v5.models.StepManeuver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @Named("main")
    fun provideRetrofit(
        @Named("main") okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    @Singleton
    @Named("trackasia")
    fun provideTrackAsiaRetrofit(
        @Named("trackasia") okHttpClient: OkHttpClient
    ): Retrofit {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(StepManeuver.Type::class.java, ManeuverTypeDeserializer())
            .registerTypeAdapter(ManeuverModifier.Type::class.java, ManeuverModifierDeserializer())
            .registerTypeAdapter(Point::class.java, PointDeserializer())
            .create()

        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL_MAP_VN)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("main")
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Add Auth first
            .addInterceptor(loggingInterceptor) // Then Logging so we see the headers
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    @Named("trackasia")
    fun provideTrackAsiaOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .connectTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(@Named("main") retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(@Named("main") retrofit: Retrofit): UserService{
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideJobService(@Named("main") retrofit: Retrofit): JobService {
        return retrofit.create(JobService::class.java)
    }

    @Provides
    @Singleton
    fun provideReviewService(@Named("main") retrofit: Retrofit): ReviewService {
        return retrofit.create(ReviewService::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoryService(@Named("main") retrofit: Retrofit): CategoryService{
        return retrofit.create(CategoryService::class.java)
    }


    @Provides
    @Singleton
    fun provideConversationService(@Named("main") retrofit: Retrofit): ConversationService {
        return retrofit.create(ConversationService::class.java)
    }

    @Provides
    @Singleton
    fun provideFinanceService(@Named("main") retrofit: Retrofit): FinanceService {
        return retrofit.create(FinanceService::class.java)
    }

    @Provides
    @Singleton
    fun provideJobDetailApiService(@Named("main") retrofit: Retrofit) : JobDetailApiService {
        return retrofit.create(JobDetailApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTrackAsiaApiService(@Named("trackasia") retrofit: Retrofit): TrackAsiaApiService {
        return retrofit.create(TrackAsiaApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCloudinaryService(): CloudinaryService {
        return Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/v1_1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build())
            .build()
            .create(CloudinaryService::class.java)
    }
}