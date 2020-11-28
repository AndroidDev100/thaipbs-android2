package me.vipa.app.dependencies

import com.vipa.app.dependencies.modules.UserPreferencesModule
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [UserPreferencesModule::class])
interface EnveuComponent {
    fun inject (mvHubPlusApplication: _root_ide_package_.me.vipa.app.MvHubPlusApplication)
    fun inject(splash: _root_ide_package_.me.vipa.app.activities.splash.ui.ActivitySplash)
    fun inject(homeActivity: _root_ide_package_.me.vipa.app.activities.homeactivity.ui.HomeActivity)

}