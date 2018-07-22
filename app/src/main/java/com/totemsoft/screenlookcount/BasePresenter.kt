package com.totemsoft.screenlookcount

/**
 * Base presenter class to use by all presenters.
 * Holds a reference to the view, which can be attached or detached.
 *
 * @author Antonina
 */
open class BasePresenter<V : BaseView> : BaseMvpPresenter<V> {

    var view: V?

    init {
        view = null
    }

    override fun attach(v: V) {
        view = v
    }

    override fun detach() {
        view?.let {
            view = null
        }
    }

    fun getCurrentView() = view

    override fun isAttached() = view != null
}