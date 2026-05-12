package com.tenko.app.data.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tenko.app.data.model.MedicineRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class MedicineSearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MedicineRepository(application)
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _results = MutableStateFlow<List<String>>(emptyList())
    val results = _results.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    init {
        viewModelScope.launch {
            _query
                .debounce(250)
                .distinctUntilChanged()
                .collectLatest { query ->
                    _results.value =
                        repository.searchMedicines(query)
                }
        }
    }
}