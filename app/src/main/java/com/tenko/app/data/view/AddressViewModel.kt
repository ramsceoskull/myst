package com.tenko.app.data.view

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.tenko.app.data.model.Address

class AddressViewModel : ViewModel() {
    private var _addresses = mutableStateListOf<Address>()
    val addresses: List<Address> = _addresses

    private var nextId = 0

    fun addAddress(address: Address) {
        _addresses.add(address.copy(id = nextId++))
    }

    fun updateAddress(updated: Address) {
        val index = _addresses.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            _addresses[index] = updated
        }
    }

    fun deleteAddress(id: Int) {
        _addresses.removeAll { it.id == id }
    }

    fun selectAddress(id: Int) {
        _addresses.replaceAll {
            it.copy(isSelected = it.id == id)
        }
    }
}