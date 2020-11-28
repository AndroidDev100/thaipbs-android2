package me.vipa.app.dependencies.modules

import android.content.Context
import me.vipa.app.dependencies.providers.DTGPrefrencesProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UserPreferencesModule(context: Context) {
    val mContext = context
    @Singleton
    @Provides
     fun provideUserPrefrences(): DTGPrefrencesProvider {
        return DTGPrefrencesProvider(mContext)
    }
}