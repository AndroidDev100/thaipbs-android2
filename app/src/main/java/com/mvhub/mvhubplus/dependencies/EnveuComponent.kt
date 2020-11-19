package com.mvhub.mvhubplus.dependencies

import com.mvhub.mvhubplus.dependencies.modules.UserPreferencesModule
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [UserPreferencesModule::class])
interface EnveuComponent {
    fun inject (mvHubPlusApplication: com.mvhub.mvhubplus.MvHubPlusApplication)
    fun inject(splash: com.mvhub.mvhubplus.activities.splash.ui.ActivitySplash)
    fun inject(homeActivity: com.mvhub.mvhubplus.activities.homeactivity.ui.HomeActivity)

}