package com.aar.app.wifinetanalyzer.ouilookup

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aar.app.wifinetanalyzer.data.VendorFinder
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OuiLookupViewModel(private val vendorFinder: VendorFinder): ViewModel() {

    enum class SearchBy {
        MAC_ADDRESS, MANUFACTURER
    }

    var currentKeyword: String = ""
    val searchResult: MutableLiveData<List<OuiLookupResult>> = MutableLiveData()
    var resultLimit: Int = 32
    var searchBy: SearchBy = SearchBy.MAC_ADDRESS
        set(value) {
            if (value != field) {
                field = value
                search(currentKeyword)
            }
        }

    fun search(keyword: String) {
        if (keyword.isEmpty()) {
            currentKeyword = ""
            searchResult.value = ArrayList()
            return
        }

        currentKeyword = if (searchBy == SearchBy.MAC_ADDRESS) {
            keyword.substring(0, Math.min(8, keyword.length))
        } else {
            keyword
        }

        Observable.create(ObservableOnSubscribe<List<OuiLookupResult>> {
            val results = if (searchBy == SearchBy.MAC_ADDRESS) {
                vendorFinder.searchByMac(currentKeyword, resultLimit)
            } else {
                vendorFinder.searchByManufacturer(currentKeyword, resultLimit)
            }
            it.onNext(results)
            it.onComplete()
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    searchResult.value = it
                })
    }

}