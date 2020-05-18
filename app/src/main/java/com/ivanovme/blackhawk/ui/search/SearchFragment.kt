package com.ivanovme.blackhawk.ui.search

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivanovme.blackhawk.R
import com.ivanovme.blackhawk.ui.search.data.SearchViewState
import com.ivanovme.blackhawk.ui.utils.hideKeyboard
import com.ivanovme.blackhawk.ui.utils.showKeyboard
import com.ivanovme.blackhawk.ui.utils.withLifecycle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModel {
        parametersOf(arguments?.getBoolean(IS_DEPARTURE_KEY, true) ?: true)
    }

    private lateinit var searchAdapter: SearchAdapter

    override fun onResume() {
        super.onResume()
        searchEditView.showKeyboard()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViews()
        subscribeToViewModel()
        setupClickListeners()
    }

    private fun setupViews() {
        searchResultRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        searchAdapter = SearchAdapter {
            viewModel.postEvent(CitySelected(it.city, it.airportCode, it.location))
        }
        searchResultRecyclerView.adapter = searchAdapter

        val isDeparture = arguments?.getBoolean(IS_DEPARTURE_KEY, true) ?: true

        searchEditView.hint =
            if (isDeparture) getString(R.string.departure) else getString(R.string.destination)
    }

    private fun subscribeToViewModel() {
        withLifecycle { compositeDisposable ->
            viewModel.state
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe(
                    { render(it) },
                    {
                        throw it
                    }
                )
        }
    }

    private fun setupClickListeners() {
        searchBackView.setOnClickListener {
            viewModel.postEvent(BackArrowPressed)
        }

        searchCloseView.setOnClickListener {
            viewModel.postEvent(EraseQueryClicked)
        }

        searchEditView.doOnTextChanged { text, _, _, _ ->
            searchCloseView.isVisible = !text.isNullOrEmpty()
            if (!text.isNullOrEmpty())
                viewModel.postEvent(QueryTextChanged(text.toString()))
        }

        searchEditView.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    requireActivity().hideKeyboard()
                    true
                }
                else -> false
            }
        }
    }

    private fun render(viewState: SearchViewState) {
        errorView.isVisible = viewState.error.isNotEmpty()
        errorView.text = viewState.error
        searchAdapter.updateData(viewState.searchResults)

        searchProgressView.isVisible = viewState.isLoading

        if (viewState.searchQuery.isEmpty())
            searchEditView.setText(viewState.searchQuery)

    }

    companion object {
        private const val IS_DEPARTURE_KEY = "is_departure_key"
        fun newInstance(isDeparture: Boolean): Fragment {
            return SearchFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_DEPARTURE_KEY, isDeparture)
                }
            }
        }
    }
}