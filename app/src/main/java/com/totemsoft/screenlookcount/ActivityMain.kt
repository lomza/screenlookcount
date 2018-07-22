package com.totemsoft.screenlookcount

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.totemsoft.screenlookcount.background.LookCounterService
import com.totemsoft.screenlookcount.db.ScreenCounterDb
import com.totemsoft.screenlookcount.fragment.about.FragmentAbout
import com.totemsoft.screenlookcount.fragment.main.FragmentMain
import com.totemsoft.screenlookcount.utils.*
import kotlinx.android.synthetic.main.activity_main.*

/**
 * This is the main activity which acts as a holder for [FragmentMain] and [FragmentAbout] fragments.
 * Handles Toolbar actions as well.
 *
 * @author Antonina
 */
class ActivityMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    override fun onStart() {
        super.onStart()

        if (AppPreferences.shouldRunCountingService) {
            startService()
        }

        if (!isFragmentInBackStack(C.FRAGMENT_TAG_MAIN)) {
            addFragment(FragmentMain::class.java, C.FRAGMENT_TAG_MAIN, sv_container, false, null)
        }
    }

    private fun initView() {
        b_about.setOnClickListener {
            showOrHideAbout()
        }
    }

    private fun updateMainView() {
        val mainFragment = getFragmentByTag(C.FRAGMENT_TAG_MAIN)
        if (mainFragment is FragmentMain) {
            mainFragment.getMainPresenter().setCountersValues(this)
        }
    }

    private fun showOrHideAbout() {
        if (isFragmentVisible(C.FRAGMENT_TAG_MAIN)) {
            addFragment(FragmentAbout::class.java, C.FRAGMENT_TAG_ABOUT, sv_container.id, true, null)
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        menu.findItem(R.id.action_switch_service).title =
                if (AppPreferences.shouldRunCountingService)
                    getString(R.string.action_stop_counting)
                else
                    getString(R.string.action_start_counting)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_service -> {
                switchService(item)
                true
            }
            R.id.action_clear_data -> {
                clearDataAction()
                true
            }
            R.id.action_share -> {
                shareWithFriendsAction()
                true
            }
            R.id.action_rate_app -> {
                rateAppAction()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startService() {
        startService(Intent(this, LookCounterService::class.java))
    }

    private fun stopService() {
        stopService(Intent(this, LookCounterService::class.java))
    }

    private fun clearAllData() {
        ScreenCounterDb.getDatabase(this).dropDb()
    }

    private fun showClearAllDataUndoSnackbar() {
        val undoSnackbar = Snackbar.make(sv_container, getString(R.string.snack_bar_clear_text), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.snack_bar_clear_action_undo)) {
                    ScreenCounterDb.getDatabase(this).setDbClearFlag(false)
                    updateMainView()
                }
                .addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar?, event: Int) {
                        super.onDismissed(snackbar, event)
                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                            clearAllData()
                        }
                    }
                })

        // styling
        undoSnackbar.setActionTextColor(ContextCompat.getColor(this, R.color.snack_bar_action))
        val snackbarView = undoSnackbar.view
        val snackbarTextId = android.support.design.R.id.snackbar_text
        val textView = snackbarView.findViewById(snackbarTextId) as TextView
        textView.setTextColor(ContextCompat.getColor(this, R.color.snack_bar_text))
        snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snack_bar_background))

        undoSnackbar.show()
    }

    private fun switchService(menuItem: MenuItem) {
        val startCountingTitle = getString(R.string.action_start_counting)
        val stopCountingTitle = getString(R.string.action_stop_counting)
        if (menuItem.title == startCountingTitle) {
            AppPreferences.shouldRunCountingService = true
            startService()
            menuItem.title = stopCountingTitle
        } else {
            AppPreferences.shouldRunCountingService = false
            stopService()
            menuItem.title = startCountingTitle
        }
    }

    private fun clearDataAction() {
        ScreenCounterDb.getDatabase(this).setDbClearFlag(true)
        updateMainView()
        showClearAllDataUndoSnackbar()
    }

    private fun rateAppAction() {
        val appPackageName = packageName
        try {
            val googlePlayIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.rate_google_play_uri, appPackageName)))
            startActivity(googlePlayIntent)
        } catch (exception: ActivityNotFoundException) {
            val googlePlayWebPageIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.rate_google_play_uri_browser, appPackageName)))
            startActivity(googlePlayWebPageIntent)
        }
    }

    private fun shareWithFriendsAction() {
        val shareIntent = Intent()
        with(shareIntent) {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text, packageName))
            type = "text/plain"
            startActivity(Intent.createChooser(this, resources.getText(R.string.share_chooser)))
        }
    }
}