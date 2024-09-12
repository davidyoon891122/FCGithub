package com.example.fcgithub

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fcgithub.adapter.UserAdapter
import com.example.fcgithub.databinding.ActivityMainBinding
import com.example.fcgithub.model.Repo
import com.example.fcgithub.model.UserDto
import com.example.fcgithub.network.GithubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userAdapter: UserAdapter

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var searchFor: String = ""
    private var handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        userAdapter = UserAdapter {
            val intent = Intent(this@MainActivity, RepoActivity::class.java)
            intent.putExtra("username", it.userName)

            startActivity(intent)
        }
        setContentView(binding.root)



        binding.userRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter

        }

        val runnable = Runnable {
            searchUser()
        }

        binding.searchEditText.addTextChangedListener {
            searchFor = it.toString()

            handler.removeCallbacks(runnable)
            handler.postDelayed(
                runnable,
                300,
            )


        }



    }

    private fun searchUser() {

        val githubService =  retrofit.create(GithubService::class.java)

        githubService.searchUsers(searchFor).enqueue(object: Callback<UserDto> {
            override fun onResponse(p0: Call<UserDto>, p1: Response<UserDto>) {
                Log.e("MainActivity", p1.body().toString())
                userAdapter.submitList(p1.body()?.items)
            }

            override fun onFailure(p0: Call<UserDto>, p1: Throwable) {
                Log.e("MainActivity", p1.toString())
                Toast.makeText(this@MainActivity, "에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
                p1.printStackTrace()
            }
        })
    }
}