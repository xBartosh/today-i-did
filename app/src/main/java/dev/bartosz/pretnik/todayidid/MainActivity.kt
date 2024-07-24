package dev.bartosz.pretnik.todayidid

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import dev.bartosz.pretnik.todayidid.data.model.ActivityRepository
import dev.bartosz.pretnik.todayidid.data.model.ViewModelFactory
import dev.bartosz.pretnik.todayidid.data.model.db.AppDatabase
import dev.bartosz.pretnik.todayidid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val database by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "activity_database")
            .build()
    }
    val repository by lazy { ActivityRepository(database.activityDao()) }
    val viewModelFactory by lazy {
        ViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        
        navView.setupWithNavController(navController)
    }
}