package com.zxy.zxydowload

import android.util.Log
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

/**
 * Created by zxy on 2019/10/8-17:37
 * Class functions
 * ******************************************
 * *
 * ******************************************
 */
class Weak<T : Any>(initializer: () -> T?) {
    var weakReference = WeakReference<T?>(initializer())

    constructor() : this({
        null
    })

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        Log.d("Weak Delegate", "-----------getValue")
        return weakReference.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        Log.d("Weak Delegate", "-----------setValue")
        weakReference = WeakReference(value)
    }

}
