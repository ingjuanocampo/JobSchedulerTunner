package com.ingjuanocampo.jstunner.ui.main

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.ingjuanocampo.jstunner.R
import com.ingjuanocampo.jstunner.NotificationJobService
import kotlinx.android.synthetic.main.main_fragment.*


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private val JOB_ID = 0
    private var mScheduler: JobScheduler? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mScheduler = requireContext().getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

        // Updates the TextView with the value from the seekbar.
        // Updates the TextView with the value from the seekbar.
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (i > 0) {
                    seekBarProgress.text = getString(R.string.seconds, i)
                } else {
                    seekBarProgress.setText(R.string.not_set)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        scheduleJob.setOnClickListener { scheduleJob() }
        cancelJob.setOnClickListener { cancelJobs() }
    }

    fun scheduleJob() {
        val selectedNetworkID = networkOptions.checkedRadioButtonId
        var selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE
        val seekBarSet = seekBar.progress > 0
        when (selectedNetworkID) {
            R.id.noNetwork -> selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE
            R.id.anyNetwork -> selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY
            R.id.wifiNetwork -> selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED
        }
        val serviceName = ComponentName(
            activity?.packageName!!,
            NotificationJobService::class.java.name
        )
        val builder = JobInfo.Builder(JOB_ID, serviceName)
            .setRequiredNetworkType(selectedNetworkOption)
            .setRequiresDeviceIdle(idleSwitch.isChecked)
            .setRequiresCharging(chargingSwitch.isChecked)
        if (seekBarSet) {
            builder.setOverrideDeadline(seekBar.progress * 1000.toLong())
        }
        val constraintSet = ((selectedNetworkOption
                != JobInfo.NETWORK_TYPE_NONE) || chargingSwitch.isChecked
                || idleSwitch.isChecked
                || seekBarSet)
        if (constraintSet) {
            val myJobInfo = builder.build()
            mScheduler!!.schedule(myJobInfo)
            Toast.makeText(requireContext(), R.string.job_scheduled, Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                requireContext(), R.string.no_constraint_toast,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * onClick method for cancelling all existing jobs.
     */
    fun cancelJobs() {
        if (mScheduler != null) {
            mScheduler!!.cancelAll()
            mScheduler = null
            Toast.makeText(requireContext(), R.string.jobs_canceled, Toast.LENGTH_SHORT)
                .show()
        }
    }

}
