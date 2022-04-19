package ir.am3n.needtool

import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter

fun PagingDataAdapter<*, *>.decideOnState(
    showLoadStates: (showLoading: Boolean, showEmpty: Boolean, showError: Boolean) -> Unit
) {

    addLoadStateListener { loadState ->

        val errorState = loadState.source.append as? LoadState.Error
            ?: loadState.source.prepend as? LoadState.Error
            ?: loadState.source.refresh as? LoadState.Error
            ?: loadState.append as? LoadState.Error
            ?: loadState.prepend as? LoadState.Error
            ?: loadState.refresh as? LoadState.Error

        val loading = loadState.refresh is LoadState.Loading && itemCount == 0

        val empty = loadState.source.append is LoadState.NotLoading && loadState.source.append.endOfPaginationReached && itemCount == 0

        val error = errorState != null && itemCount == 0

        showLoadStates(loading, empty, error)

    }

}