package com.example.fcgithub

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val githubService =  retrofit.create(GithubService::class.java)
        githubService.listRepos("square").enqueue(object: Callback<List<Repo>> {
            override fun onResponse(p0: Call<List<Repo>>, p1: Response<List<Repo>>) {
                Log.e("MainActivity", p1.body().toString())
            }

            override fun onFailure(p0: Call<List<Repo>>, p1: Throwable) {
                Log.e("MainActivity", p1.toString())
            }
        })

        val userAdapter = UserAdapter()

        binding.userRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter

        }

        githubService.searchUsers("squar").enqueue(object: Callback<UserDto> {
            override fun onResponse(p0: Call<UserDto>, p1: Response<UserDto>) {
                Log.e("MainActivity", p1.body().toString())
                userAdapter.submitList(p1.body()?.items)
            }

            override fun onFailure(p0: Call<UserDto>, p1: Throwable) {
                Log.e("MainActivity", p1.toString())
            }
        })

    }
}