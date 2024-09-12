package com.example.fcgithub

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fcgithub.adapter.RepoAdapter
import com.example.fcgithub.databinding.ActivityRepoBinding
import com.example.fcgithub.model.Repo
import com.example.fcgithub.network.GithubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RepoActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRepoBinding
    private lateinit var repoAdapter: RepoAdapter

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRepoBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val userName = intent.getStringExtra("username") ?: return

        repoAdapter = RepoAdapter()
        binding.usernameTextView.text = userName

        binding.repoRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RepoActivity)
            adapter = repoAdapter
        }

        listRepo(userName)

    }

    private fun listRepo(userName: String) {

        val githubService = retrofit.create(GithubService::class.java)

        githubService.listRepos(userName).enqueue(object: Callback<List<Repo>> {
            override fun onResponse(p0: Call<List<Repo>>, p1: Response<List<Repo>>) {
                Log.e("MainActivity", p1.body().toString())

                repoAdapter.submitList(p1.body())
            }

            override fun onFailure(p0: Call<List<Repo>>, p1: Throwable) {
                Log.e("MainActivity", p1.toString())
            }
        })
    }
}