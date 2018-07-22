package com.totemsoft.screenlookcount

/**
 * Interface for a base presenter with view-related methods.
 *
 * @author Antonina
 */
interface BaseMvpPresenter<V : BaseView> {
    fun attach(v: V)
    fun detach()
    fun isAttached(): Boolean
}