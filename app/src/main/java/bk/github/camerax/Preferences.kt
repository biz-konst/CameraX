package bk.github.camerax

import android.app.Application

class Preferences(application: Application) : PreferencesHelper(application),
    PermissionsFragment.PermissionsPreferences {

    override var isFirstPermissionsRequest by preference(true)
}