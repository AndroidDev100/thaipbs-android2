package me.vipa.app.dependencies

import me.vipa.app.dependencies.modules.UserPreferencesModule
import dagger.Component
import me.vipa.app.MvHubPlusApplication
import me.vipa.app.activities.homeactivity.ui.HomeActivity
import me.vipa.app.activities.splash.ui.ActivitySplash
import javax.inject.Singleton


@Singleton
@Component(modules = [UserPreferencesModule::class])
interface EnveuComponent {
    fun inject (mvHubPlusApplication: MvHubPlusApplication)
    fun inject(splash: ActivitySplash)
    fun inject(homeActivity: HomeActivity)

}