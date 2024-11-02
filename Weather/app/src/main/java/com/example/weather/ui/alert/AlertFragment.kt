package com.example.weather.ui.alert

import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.DialogAlertLayoutBinding
import com.example.weather.databinding.FragmentAlertBinding
import com.example.weather.db.WeatherDataBase
import com.example.weather.db.WeatherLocalDataSourceImpl
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import com.example.weather.model.AlertsData
import com.example.weather.model.ShowSnackbar
import com.example.weather.model.WeatherRepositoryImpl
import com.example.weather.network.ApiService
import com.example.weather.network.NetworkConnectionStatusImpl
import com.example.weather.network.RetrofitHelper
import com.example.weather.network.WeatherRemoteDataSourceImpl
import com.example.weather.sharedpreference.SharedPreferenceDataSourceImpl
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch


private const val NOTIFICATION_REQUEST_CODE = 200

class AlertFragment : Fragment() {
    private var _binding: FragmentAlertBinding? = null
    private val binding get() = _binding!!
    private val alertViewModel: AlertViewModel by viewModels {
        AlertViewModelFactory(
            WeatherRepositoryImpl(
                WeatherRemoteDataSourceImpl(RetrofitHelper.getInstance().create(ApiService::class.java)),
                WeatherLocalDataSourceImpl(WeatherDataBase.getInstance(requireContext()).weatherDao()),
                SharedPreferenceDataSourceImpl.getInstance(requireContext()),
                NetworkConnectionStatusImpl.getInstance(requireContext())

            ),
            AlarmHandlerImpl(requireContext())
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.floatingAlertButton.setOnClickListener {
            handleAlertFAB()
        }


        // Initialize the RecyclerView adapter
        binding.recyclerViewAlert.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ListAdapterAlert(
                myListener = {
                },
                removeListener = { alert ->
                    handleDeleteAlertButton(alert)
                }
            )
        }


        // Observe weatherFavourites from ViewModel
        lifecycleScope.launch {
            alertViewModel.weatherAlerts.collect { alerts ->
                (binding.recyclerViewAlert.adapter as ListAdapterAlert).submitList(alerts)
                binding.recyclerViewAlert.visibility = if (alerts.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        return root
    }


    private fun handleAlertFAB() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
            if (notificationManager.areNotificationsEnabled())
                handleAddingAlert()
            else {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_REQUEST_CODE
                )
            }
        } else {
            handleAddingAlert()
        }
    }



    private fun handleAddingAlert() {
        // Create and show the dialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogAlertLayoutBinding.inflate(LayoutInflater.from(context))
        builder.setView(dialogBinding.root)

        val dialog: Dialog = builder.create()
        dialog.show()

        // Show Date Picker Dialog
        dialogBinding.selectDateBtn.setOnClickListener {
            openDateDialog { date -> dialogBinding.selectDateBtn.text = date }
        }

        // Show Time Picker Dialog
        dialogBinding.selectTimeBtn.setOnClickListener {
            openClockDialog { time -> dialogBinding.selectTimeBtn.text = time }
        }

        // Save the alert when the save button is clicked
        dialogBinding.alertSaveBtn.setOnClickListener {
            // Ensure that both date and time have been selected
            if (dialogBinding.selectDateBtn.text == getString(R.string.select_date) ||
                dialogBinding.selectTimeBtn.text == getString(R.string.select_time)
            ) {
                ShowSnackbar.customSnackbar(
                    context = requireContext(),
                    view = requireView(),
                    message = getString(R.string.please_select),
                    actionText = "",
                    iconResId = R.drawable.dangerous_24px,
                    action = { view ->

                    }
                )
                return@setOnClickListener
            }

            // Retrieve the selected date and time
            val selectedDate = dialogBinding.selectDateBtn.text.toString()
            val selectedTime = dialogBinding.selectTimeBtn.text.toString()

            // Combine selected date and time into a single date-time string
            val combinedDateTimeString = "$selectedDate $selectedTime"

            // Convert the combined date-time into milliseconds using the updated SimpleDateFormat
            val formatter = SimpleDateFormat("MMM dd, yyyy hh:mm a",Locale.ENGLISH)
            val date = formatter.parse(combinedDateTimeString)
            val alertTimeInMillis = date?.time ?: 0L


            // Check if the selected time is in the future before scheduling and saving
            if (alertTimeInMillis > System.currentTimeMillis()) {
                // Schedule the alert and save it
                Log.i("alert", "handleAddingAlert: ")
                alertViewModel.insertAlert(alertTimeInMillis)

                ShowSnackbar.customSnackbar(
                    context = requireContext(),
                    view = requireView(),
                    message = getString(R.string.alert_set_successfully),
                    actionText = "",
                    iconResId = R.drawable.notifications_24px,
                    action = { view ->

                    }
                )
            } else {
                ShowSnackbar.customSnackbar(
                    context = requireContext(),
                    view = requireView(),
                    message = getString(R.string.time_error),
                    actionText = "",
                    iconResId = R.drawable.timer_off_24px,
                    action = { view ->

                    }
                )
            }

            dialog.dismiss()
        }
    }







    private fun openDateDialog(onDateSelected: (String) -> Unit) {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder)
            .build()

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = Date(it)
            val formattedDate = dateFormat.format(selectedDate)
            onDateSelected(formattedDate)
        }

        datePicker.show(requireActivity().supportFragmentManager, "date_dialog")
    }

    private fun openClockDialog(onTimeSelected: (String) -> Unit) {
        val currentTime = Calendar.getInstance()
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(currentTime.get(Calendar.HOUR_OF_DAY))
            .setMinute(currentTime.get(Calendar.MINUTE))
            .setTitleText("Select Time")
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val hour = if (timePicker.hour == 0) 12 else if (timePicker.hour > 12) timePicker.hour - 12 else timePicker.hour
            val minute = timePicker.minute
            val amPm = if (timePicker.hour >= 12) "PM" else "AM"
            val formattedTime = String.format(Locale.US, "%02d:%02d %s", hour, minute, amPm)
            onTimeSelected(formattedTime)
        }

        timePicker.show(requireActivity().supportFragmentManager, "clock_dialog")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            handleAddingAlert()
        }
    }

    private fun handleDeleteAlertButton(alarmItem: AlertsData) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_alert))
            .setMessage(getString(R.string.delete_alert_message))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                alertViewModel.deleteAlert(alarmItem)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
