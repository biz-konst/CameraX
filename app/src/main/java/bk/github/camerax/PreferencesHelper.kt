@file:Suppress("unused")

package bk.github.camerax

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.*
import java.io.InvalidClassException
import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class PreferencesHelper(application: Application) : AndroidViewModel(application),
    OnSharedPreferenceChangeListener {

    private val keyedPreferences = ConcurrentHashMap<String, MutableStateFlow<*>>()

    private val sharedPreferences: SharedPreferences by lazy {
        with(getApplication<Application>().applicationContext) {
            getSharedPreferences("${packageName}_preferences", MODE_PRIVATE)
        }.apply {
            registerOnSharedPreferenceChangeListener(this@PreferencesHelper)
        }
    }

    override fun onCleared() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onCleared()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        @Suppress("UNCHECKED_CAST")
        ((keyedPreferences[key ?: return] as? MutableStateFlow<Any>)?.apply {
            update { sharedPreferences?.getValue(key, value) ?: return }
        })
    }

    fun <T : Any> preference(defValue: T, key: String? = null) =
        PropertyDelegateProvider<PreferencesHelper, ReadWriteProperty<PreferencesHelper, T>>
        { thisRef, property ->
            val keyName = key ?: property.name
            Preference(keyName, thisRef.sharedPreferences.getValue(keyName, defValue))
        }

    fun <T : Any> flowPreference(defValue: T, key: String? = null) =
        PropertyDelegateProvider<PreferencesHelper, Flow<T>> { thisRef, property ->
            MutableStateFlow(defValue).also { thisRef.keyedPreferences[key ?: property.name] = it }
        }

    fun clear() {
        sharedPreferences.edit { clear() }
    }

    private class Preference<T : Any>(private val key: String, private val defValue: T) :
        ReadWriteProperty<PreferencesHelper, T> {
        override fun getValue(thisRef: PreferencesHelper, property: KProperty<*>): T =
            thisRef.sharedPreferences.getValue(key, defValue)

        override fun setValue(thisRef: PreferencesHelper, property: KProperty<*>, value: T) {
            thisRef.sharedPreferences.setValue(key, value)
        }
    }

}

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> SharedPreferences.getValue(key: String, defValue: T) =
    when (defValue::class) {
        Boolean::class -> getBoolean(key, defValue as Boolean)
        Int::class -> getInt(key, defValue as Int)
        Long::class -> getLong(key, defValue as Long)
        Float::class -> getFloat(key, defValue as Float)
        String::class -> getString(key, defValue as String)
        else -> throw InvalidClassException("Invalid preference property type")
    } as T

internal fun <T : Any> SharedPreferences.setValue(key: String, newValue: T) {
    edit {
        when (newValue::class) {
            Boolean::class -> putBoolean(key, newValue as Boolean)
            Int::class -> putInt(key, newValue as Int)
            Long::class -> putLong(key, newValue as Long)
            Float::class -> putFloat(key, newValue as Float)
            String::class -> putString(key, newValue as String)
        }
    }
}

