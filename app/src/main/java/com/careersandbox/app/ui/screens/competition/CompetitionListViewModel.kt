package com.careersandbox.app.ui.screens.competition

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careersandbox.app.data.model.Competition
import com.careersandbox.app.data.model.CompetitionCategory
import com.careersandbox.app.data.repository.CompetitionRepository
import com.careersandbox.app.data.repository.RemoteCompetitionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 競賽組隊媒合 list: loads PAGE_SIZE competitions at a time and appends the next
 * page when the screen reaches the bottom. It lives as long as the list's back
 * stack entry, so opening a competition and coming back keeps what was loaded.
 */
class CompetitionListViewModel(
    private val repo: CompetitionRepository = RemoteCompetitionRepository,
) : ViewModel() {

    /** null = 全部 */
    var category by mutableStateOf<CompetitionCategory?>(null)
        private set

    val items = mutableStateListOf<Competition>()

    var hasMore by mutableStateOf(true)
        private set

    var loading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    private var job: Job? = null

    fun selectCategory(c: CompetitionCategory?) {
        if (c == category) return
        job?.cancel()               // a page still loading for the old category is dropped
        category = c
        items.clear()
        hasMore = true
        error = null
        loading = false
        loadMore()
    }

    /** Appends the next page. Does nothing while loading, after the last page, or after an error. */
    fun loadMore() {
        if (loading || !hasMore || error != null) return
        val cat = category
        loading = true
        job = viewModelScope.launch {
            repo.page(cat, offset = items.size, limit = PAGE_SIZE)
                .onSuccess { page ->
                    val known = items.mapTo(HashSet()) { it.id }
                    val fresh = page.items.filter { it.id !in known }
                    items.addAll(fresh)
                    // A page with nothing new also ends the list, so a wrong hasMore cannot loop.
                    hasMore = page.hasMore && fresh.isNotEmpty()
                }
                .onFailure { error = it.message ?: "載入失敗，請稍後再試" }
            loading = false
        }
    }

    fun retry() {
        error = null
        loadMore()
    }

    companion object {
        const val PAGE_SIZE = 5
    }
}