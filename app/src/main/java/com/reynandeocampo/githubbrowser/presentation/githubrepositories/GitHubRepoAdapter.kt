package com.reynandeocampo.githubbrowser.presentation.githubrepositories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.githubbrowser.R
import com.reynandeocampo.githubbrowser.databinding.ListItemRepoBinding

class GitHubRepoAdapter(private val onClickListener: OnClickListener<GitRepo>) :
    RecyclerView.Adapter<GitHubRepoViewHolder>() {

    var gitRepos: List<GitRepo> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GitHubRepoViewHolder {
        val withDataBinding: ListItemRepoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            GitHubRepoViewHolder.LAYOUT,
            parent,
            false
        )

        return GitHubRepoViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: GitHubRepoViewHolder, position: Int) {
        holder.viewDataBinding.also { view ->
            view.txtName.text = gitRepos[position].owner.userName
            view.txtDescription.text = gitRepos[position].description
            view.root.setOnClickListener { onClickListener.onClick(gitRepos[position]) }
        }
    }

    override fun getItemCount(): Int {
        return gitRepos.size
    }
}

class GitHubRepoViewHolder(val viewDataBinding: ListItemRepoBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.list_item_repo
    }
}
