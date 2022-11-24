package com.mukul.jan.reactive.store.lib.feature

import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

interface LifecycleAwareFeature : DefaultLifecycleObserver {

    fun start()

    fun stop()

    override fun onStart(owner: LifecycleOwner) {
        start()
    }

    override fun onStop(owner: LifecycleOwner) {
        stop()
    }
}

class ViewBinding<T : LifecycleAwareFeature>(
    private val wrapper: ViewBoundFeatureWrapper<T>
) : View.OnAttachStateChangeListener {
    override fun onViewAttachedToWindow(p0: View) {}
    override fun onViewDetachedFromWindow(p0: View) {
        wrapper.stop()
    }
}

class LifecycleBinding<T : LifecycleAwareFeature>(
    private val wrapper: ViewBoundFeatureWrapper<T>
) : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        wrapper.start()
    }

    override fun onStop(owner: LifecycleOwner) {
        wrapper.stop()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        wrapper.clear()
    }
}

class ViewBoundFeatureWrapper<T : LifecycleAwareFeature>() {
    private var feature: T? = null
    private var owner: LifecycleOwner? = null
    private var view: View? = null

    private var viewBinding: ViewBinding<T>? = null
    private var lifecycleBinding: LifecycleBinding<T>? = null

    private var isFeatureStarted: Boolean = false

    constructor(feature: T, owner: LifecycleOwner, view: View) : this() {
        set(feature, owner, view)
    }

    @Synchronized
    fun set(feature: T, owner: LifecycleOwner, view: View? = null) {
        if (this.feature != null) {
            clear()
        }

        this.feature = feature
        this.owner = owner
        this.view = view

        viewBinding = ViewBinding(this).also {
            view?.addOnAttachStateChangeListener(it)
        }

        lifecycleBinding = LifecycleBinding(this).also {
            owner.lifecycle.addObserver(it)
        }
    }

    @Synchronized
    fun get(): T? = feature

    @Synchronized
    fun withFeature(block: (T) -> Unit) {
        feature?.let(block)
    }

    @Synchronized
    fun clear() {
        if (isFeatureStarted) {
            feature?.stop()
        }
        feature = null

        view?.removeOnAttachStateChangeListener(viewBinding)
        view = null
        viewBinding = null

        lifecycleBinding?.let {
            owner?.lifecycle?.removeObserver(it)
        }
        owner = null
        lifecycleBinding = null
    }

    @Synchronized
    fun onBackPressed(): Boolean {
        val feature = feature ?: return false
        return true
    }

    @Synchronized
    fun start() {
        feature?.start()
        isFeatureStarted = true
    }

    @Synchronized
    fun stop() {
        feature?.stop()
        isFeatureStarted = false
    }

}










