package com.reynandeocampo.githubbrowser.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.reynandeocampo.data.api.Status
import com.reynandeocampo.domain.models.GitRepo
import com.reynandeocampo.githubbrowser.R
import com.reynandeocampo.githubbrowser.databinding.ListItemFooterBinding
import com.reynandeocampo.githubbrowser.databinding.ListItemRepoBinding
import com.reynandeocampo.githubbrowser.presentation.OnClickListener
import com.reynandeocampo.githubbrowser.presentation.adapter.viewholders.FooterViewHolder
import com.reynandeocampo.githubbrowser.presentation.adapter.viewholders.RepoViewHolder
import java.text.SimpleDateFormat
import java.util.*

class RepoListAdapter(
    private val onClickListener: OnClickListener<GitRepo>,
    private val retry: () -> Unit
) : PagedListAdapter<GitRepo, RecyclerView.ViewHolder>(GitRepoDiffCallback) {

    private var status = Status.LOADING

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
                    val updatedAtPrefix = holder.itemView.context.getString(R.string.txt_updated_at)
                    val updatedAtDate = formatDate(gitRepo.updatedAt)

                    binding.txtName.text = gitRepo.title
                    binding.txtDescription.text = gitRepo.description
                    binding.txtUpdatedAt.text = String.format(updatedAtPrefix, updatedAtDate)
                    binding.root.setOnClickListener { onClickListener.onClick(gitRepo) }
                }
            }
            else -> {
                val footerViewHolder = holder as FooterViewHolder
                footerViewHolder.viewDataBinding.also { binding ->
                    binding.progressBar.visibility =
                        if (status == Status.LOADING) View.VISIBLE else View.INVISIBLE
                    binding.txtError.visibility =
                        if (status == Status.ERROR) View.VISIBLE else View.INVISIBLE
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
        return super.getItemCount() != 0 && (status == Status.LOADING || status == Status.ERROR)
    }

    private fun formatDate(date: String): String {
        val newFormat = SimpleDateFormat("MMM dd, yyy", Locale.getDefault())
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.getDefault())
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        val utcDate = utcFormat.parse(date)
        return newFormat.format(utcDate!!)
    }

    fun setStatus(state: Status) {
        this.status = state
        notifyItemChanged(super.getItemCount())
    }

    companion object {
        val GitRepoDiffCallback = object : DiffUtil.ItemCallback<GitRepo>() {
            override fun areItemsTheSame(oldItem: GitRepo, newItem: GitRepo): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: GitRepo, newItem: GitRepo): Boolean {
                return oldItem == newItem
            }
        }
    }
}

private const val DATA_VIEW_TYPE = 1
private const val FOOTER_VIEW_TYPE = 2
