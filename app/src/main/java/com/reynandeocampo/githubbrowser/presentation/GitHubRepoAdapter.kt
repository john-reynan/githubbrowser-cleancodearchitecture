package com.reynandeocampo.githubbrowser.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.domain.models.Owner
import com.reynandeocampo.githubbrowser.R
import com.reynandeocampo.githubbrowser.databinding.ListItemLoadingBinding
import com.reynandeocampo.githubbrowser.databinding.ListItemRepoBinding

class GitHubRepoAdapter(private val onClickListener: OnClickListener<GitRepo>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_VIEW_TYPE = 0
    val LOADING_VIEW_TYPE = 1

    private var isLoadingAdded = false

    var gitRepos: MutableList<GitRepo> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE -> {
                val withDataBinding: ListItemRepoBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    GitHubRepoViewHolder.LAYOUT,
                    parent,
                    false
                )

                GitHubRepoViewHolder(withDataBinding)
            }
            else -> {
                val withDataBinding: ListItemLoadingBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    LoadingViewHolder.LAYOUT,
                    parent,
                    false
                )

                LoadingViewHolder(withDataBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_VIEW_TYPE -> {
                val gitHubRepoViewHolder = holder as GitHubRepoViewHolder
                gitHubRepoViewHolder.viewDataBinding.also { binding ->
                    binding.txtName.text = gitRepos[position].owner.userName
                    binding.txtDescription.text = gitRepos[position].description
                    binding.root.setOnClickListener { onClickListener.onClick(gitRepos[position]) }
                }
            } else -> {

            }
        }
    }

    override fun getItemCount(): Int {
        return gitRepos.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == gitRepos.size - 1 && isLoadingAdded) {
            LOADING_VIEW_TYPE
        } else {
            ITEM_VIEW_TYPE
        }
    }

    fun addLoadingView() {
        isLoadingAdded = true
        val owner = Owner("", "", "")
        val gitRepo = GitRepo(owner, "", "", "", "", "")
        add(gitRepo)
    }

    fun addNewItems(newGitRepos: List<GitRepo>) {
        gitRepos.addAll(newGitRepos)
        notifyDataSetChanged()
    }

    fun add(gitRepo: GitRepo) {
        gitRepos.add(gitRepo)
        notifyDataSetChanged()
    }
}

class GitHubRepoViewHolder(val viewDataBinding: ListItemRepoBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.list_item_repo
    }
}

class LoadingViewHolder(val viewDataBinding: ListItemLoadingBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.list_item_loading
    }
}
