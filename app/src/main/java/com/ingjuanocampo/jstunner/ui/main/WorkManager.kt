package com.ingjuanocampo.jstunner.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ingjuanocampo.jstunner.R

class WorkManager : Fragment() {

    companion object {
        fun newInstance() = WorkManager()
    }

    private lateinit var viewModel: WorkManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.work_manager_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WorkManagerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
