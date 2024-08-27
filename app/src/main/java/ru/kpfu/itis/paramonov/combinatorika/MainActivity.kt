package ru.kpfu.itis.paramonov.combinatorika

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.kpfu.itis.paramonov.combinatorika.databinding.ActivityMainBinding
import ru.kpfu.itis.paramonov.combinatorika.fragments.MainFragment

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportFragmentManager.beginTransaction().add(
            binding.mainActivityContainer.id,
            MainFragment(),
            MainFragment.START_FRAGMENT_TAG
        ).commit()
    }

}