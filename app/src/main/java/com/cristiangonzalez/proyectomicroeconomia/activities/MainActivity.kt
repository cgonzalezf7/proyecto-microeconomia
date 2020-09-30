package com.cristiangonzalez.proyectomicroeconomia.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.cristiangonzalez.proyectomicroeconomia.R
import com.cristiangonzalez.proyectomicroeconomia.adapters.PagerAdapter
import com.cristiangonzalez.proyectomicroeconomia.dialogs.CreditsDialog
import com.cristiangonzalez.proyectomicroeconomia.fragments.DictionaryFragment
import com.cristiangonzalez.proyectomicroeconomia.fragments.ProfileFragment
import com.cristiangonzalez.proyectomicroeconomia.utils.goToActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_dictionary.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(mainToolbar)
        setUpViewPager(getPagerAdapter())
        setUpTabLayout()

        fab.setOnClickListener {
            goToActivity<AddWordActivity>()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_credits -> {
                showDialog(true)
            }
            R.id.menu_log_out -> {
                showDialog(false)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    private fun getPagerAdapter(): PagerAdapter {
        val adapter = PagerAdapter(this)
        adapter.addFragment(DictionaryFragment())
        adapter.addFragment(ProfileFragment())

        return adapter
    }

    private fun setUpViewPager(adapter: PagerAdapter?) {
        viewPager.adapter = adapter
    }

    private fun setUpTabLayout() {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.tab_item_words)
                }
                1 -> {
                    tab.text = getString(R.string.tab_item_profile)
                }
            }
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == 0) fab.show() else fab.hide()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (tab!!.position == 0) recyclerView.smoothScrollToPosition(0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun showDialog(fullScreenDialog: Boolean) {
        if (fullScreenDialog) {
            val fragmentManager = supportFragmentManager
            val newFragment = CreditsDialog()
            val transaction = fragmentManager.beginTransaction()

            transaction.add(newFragment, newFragment.tag).addToBackStack(null).commit()
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.logout_dialog_title))
                .setMessage(resources.getString(R.string.logout_dialog_message))
                .setNegativeButton(resources.getString(R.string.logout_dialog_negative)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.logout_dialog_positive)) { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    goToActivity<LoginActivity> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }
                .show()
        }

    }

}
