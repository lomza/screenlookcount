package com.totemsoft.screenlookcount

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.Nullable
import android.view.View

/**
 * Base view interface to implement by all views.
 *
 * @author Antonina
 */
interface BaseView {
    @LayoutRes
    fun getContentResource(): Int

    fun init(view: View, @Nullable state: Bundle?)
}