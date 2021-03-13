package com.reynandeocampo.githubbrowser.presentation.adapter.viewholders

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.reynandeocampo.githubbrowser.R
import com.reynandeocampo.githubbrowser.databinding.ListItemRepoBinding

class RepoViewHolder(val viewDataBinding: ListItemRepoBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.list_item_repo
    }
}
