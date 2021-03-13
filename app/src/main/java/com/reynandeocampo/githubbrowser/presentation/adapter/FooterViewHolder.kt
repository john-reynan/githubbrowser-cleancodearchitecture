package com.reynandeocampo.githubbrowser.presentation.adapter

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.reynandeocampo.githubbrowser.R
import com.reynandeocampo.githubbrowser.databinding.ListItemFooterBinding

class FooterViewHolder(val viewDataBinding: ListItemFooterBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.list_item_footer
    }
}
