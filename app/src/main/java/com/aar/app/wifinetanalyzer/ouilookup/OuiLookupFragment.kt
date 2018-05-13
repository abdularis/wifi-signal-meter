package com.aar.app.wifinetanalyzer.ouilookup

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aar.app.wifinetanalyzer.App
import com.aar.app.wifinetanalyzer.R
import com.aar.app.wifinetanalyzer.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_oui_lookup.*
import kotlinx.android.synthetic.main.partial_search_bar.*
import javax.inject.Inject

class OuiLookupFragment: Fragment() {

    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    lateinit var viewModel: OuiLookupViewModel

    private val adapter: OuiListAdapter = OuiListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_oui_lookup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(divider)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        (activity?.application as App).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, mViewModelFactory).get(OuiLookupViewModel::class.java)
        viewModel.searchResult.observe(this, Observer {
            it?.let {
                if (it.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    textMessage.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    textMessage.visibility = View.GONE
                }
                adapter.data = it
            }
        })

        buttonClear.setOnClickListener { textSearch.setText("") }

        textSearch.setText(viewModel.currentKeyword)
        textSearch.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s.toString())
            }
        })

        searchByMac.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                textSearch.setHint(R.string.lbl_search_mac)
                viewModel.searchBy = OuiLookupViewModel.SearchBy.MAC_ADDRESS
            }
        }
        searchByManufacturer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                textSearch.setHint(R.string.lbl_search_manufact)
                viewModel.searchBy = OuiLookupViewModel.SearchBy.MANUFACTURER
            }
        }
    }
}