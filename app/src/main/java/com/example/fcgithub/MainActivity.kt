package com.example.fcgithub

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fcgithub.model.Repo
import com.example.fcgithub.network.GithubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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


    }
}