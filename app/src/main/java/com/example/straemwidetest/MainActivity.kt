package com.example.straemwidetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController

/**
 * This class will be the holder of all the [Fragment] in this app
 * and the first destination is SetUpFragment
 */
class MainActivity : AppCompatActivity() {

    //NavController instance to manage fragment navigation
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get the NavHostFragment instance from activity_main.xml
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        //Instantiate nhe navController from the navHostFragment object
        navController = navHostFragment.navController

        //Specify the Start destination for this app so whenever user navigate to one of these the back button
        //in the action bar will disappear
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.setUpFragment,
                R.id.homeFragment
            )
        )

        // Make sure actions in the ActionBar get propagated, and allow the navController control the
        //appBarConfiguration
        setupActionBarWithNavController(navController,appBarConfiguration)
    }

    /**
     * Enables back button support. Simply navigates one element up on the stack.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}