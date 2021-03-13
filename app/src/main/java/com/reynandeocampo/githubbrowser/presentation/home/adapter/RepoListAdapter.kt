package com.reynandeocampo.githubbrowser.presentation.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.reynandeocampo.data.api.Status
import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.githubbrowser.databinding.ListItemFooterBinding
import com.reynandeocampo.githubbrowser.databinding.ListItemRepoBinding
import com.reynandeocampo.githubbrowser.presentation.OnClickListener

class RepoListAdapter(
    private val onClickListener: OnClickListener<GitRepo>,
    private val retry: () -> Unit
) : PagedListAdapter<GitRepo, RecyclerView.ViewHolder>(GitRepoDiffCallback) {

    private val DATA_VIEW_TYPE = 1
    private val FOOTER_VIEW_TYPE = 2

    private var state = Status.LOADING

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DATA_VIEW_TYPE -> {
                val withDataBinding: ListItemRepoBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    RepoViewHolder.LAYOUT,
                    parent,
                    false
                )
                RepoViewHolder(withDataBinding)
            }
            else -> {
                val withDataBinding: ListItemFooterBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    FooterViewHolder.LAYOUT,
                    parent,
                    false
                )
                FooterViewHolder(withDataBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            DATA_VIEW_TYPE -> {
                val repoViewHolder = holder as RepoViewHolder
                val gitRepo = getItem(position) as GitRepo

                repoViewHolder.viewDataBinding.also { binding ->
                    binding.txtName.text = gitRepo.owner.userName
                    binding.txtDescription.text = gitRepo.description
                    binding.root.setOnClickListener { onClickListener.onClick(gitRepo) }
                }
            }
            else -> {
                val footerViewHolder = holder as FooterViewHolder
                footerViewHolder.viewDataBinding.also { binding ->
                    binding.progressBar.visibility =
                        if (state == Status.LOADING) View.VISIBLE else View.INVISIBLE
                    binding.txtError.visibility =
                        if (state == Status.ERROR) View.VISIBLE else View.INVISIBLE
                    binding.txtError.setOnClickListener { retry() }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) DATA_VIEW_TYPE else FOOTER_VIEW_TYPE
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasFooter()) 1 else 0
    }

    private fun hasFooter(): Boolean {
        return super.getItemCount() != 0 && (state == Status.LOADING || state == Status.ERROR)
    }

    fun setStatus(state: Status) {
        this.state = state
        notifyItemChanged(super.getItemCount())
    }

    companion object {
        val GitRepoDiffCallback = object : DiffUtil.ItemCallback<GitRepo>() {
            override fun areItemsTheSame(oldItem: GitRepo, newItem: GitRepo): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: GitRepo, newItem: GitRepo): Boolean {
                return oldItem == newItem
            }
        }
    }
}
