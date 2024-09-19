package com.example.fcgithub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private var page = 0
    private var hasMore: Boolean = true

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRepoBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val userName = intent.getStringExtra("username") ?: return

        repoAdapter = RepoAdapter {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.htmlUrl))
            startActivity(intent)
        }
        binding.usernameTextView.text = userName

        val linearLayoutManager = LinearLayoutManager(this@RepoActivity)

        binding.repoRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = repoAdapter
        }

        binding.repoRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalCount = linearLayoutManager.itemCount
                val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()

                if (lastVisiblePosition >= (totalCount - 1) && hasMore) {
                    page += 1
                    listRepo(userName, page)
                }

            }
        })

        listRepo(userName, 0)

    }

    private fun listRepo(userName: String, page: Int) {

        val githubService = retrofit.create(GithubService::class.java)

        githubService.listRepos(userName, page).enqueue(object: Callback<List<Repo>> {
            override fun onResponse(p0: Call<List<Repo>>, p1: Response<List<Repo>>) {
                Log.e("MainActivity", p1.body().toString())

                hasMore = p1.body()?.count() == 30
                repoAdapter.submitList(repoAdapter.currentList + p1.body().orEmpty())
            }

            override fun onFailure(p0: Call<List<Repo>>, p1: Throwable) {
                Log.e("MainActivity", p1.toString())
            }
        })
    }
}