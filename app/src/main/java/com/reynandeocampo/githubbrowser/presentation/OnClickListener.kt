package com.reynandeocampo.githubbrowser.presentation

class OnClickListener<T>(val clickListener: (type: T) -> Unit) {
    fun onClick(type: T) = clickListener(type)
}
