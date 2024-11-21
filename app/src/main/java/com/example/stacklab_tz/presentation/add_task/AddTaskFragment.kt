package com.example.stacklab_tz.presentation.add_task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.stacklab_tz.R
import com.example.stacklab_tz.databinding.AddTaskFragmentBinding
import com.example.stacklab_tz.db.entities.RepeatMode.*
import com.example.stacklab_tz.db.entities.Task
import com.example.stacklab_tz.utils.pushes.RemindersManager
import com.example.stacklab_tz.utils.showToast
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar
import java.util.Locale

class AddTaskFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: AddTaskFragmentBinding? = null
    private val binding get() = _binding!!

    private val addTaskViewModel by viewModel<AddTaskViewModel>()

    private var task: Task = Task(
        title = "",
        hour = 0,
        minute = 0,
        day = 0,
        month = 0,
        year = 0,
        repeatMode = ONCE
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddTaskFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setAddTask()
    }

    private fun setToolbar() = with(binding.toolbarAddTask) {
        btnAdd.isGone = true
        addText.isGone = true
        btnAccept.apply {
            isVisible = true
            setOnClickListener {
                if ((task.title) == "" || ((task.day == 0 || ((task.month.plus(1))) == 0 || task.year == 0) && task.repeatMode == ONCE))
                    showToast(requireContext(), "Add some information")
                else
                    lifecycleScope.launch {
                        when (task.repeatMode) {
                            ONCE -> RemindersManager.startOnceReminder(
                                requireActivity(),
                                task,
                                task.id
                            )

                            DAILY -> RemindersManager.startDailyReminder(
                                requireActivity(),
                                task,
                                task.id
                            )

                            MONTOFRI -> RemindersManager.startMonToFriReminder(
                                requireActivity(),
                                task,
                                task.id
                            )

                        }
                        addTaskViewModel.insertTask(task)
                        findNavController().navigate(R.id.mainFragment)
                    }
            }
        }
        btnDenied.apply {
            isVisible = true
            setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setAddTask() = with(binding) {
        val isEdit = AddTaskFragmentArgs.fromBundle(requireArguments()).isEdit
        val taskId = AddTaskFragmentArgs.fromBundle(requireArguments()).taskId
        lifecycleScope.launch {
            if (isEdit == 1) {
                task = addTaskViewModel.getTask(taskId)
            }
        }
        timeEt.setOnClickListener {
            selectTime()
        }

        calendarEt.setOnClickListener {
            selectDate()
        }

        if (isEdit == 1) {
            titleEt.append(task.title)
            val formattedMinute = if ((task.minute.div(10)) == 0) {
                "0${task.minute}"
            } else {
                "" + task.minute
            }
            timeEt.text = "${task.hour}:$formattedMinute"
            if (task.repeatMode == ONCE) {
                calendarEt.text =
                    "${task.day}:${task.month.plus(1)}:${task.year}"
            } else {
                calendarEt.isGone = true
                calendarTv.isGone = true
            }
        }


        titleEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                task = task.copy(title = s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                task = task.copy(title = s.toString())
            }
        })

        val adapterRepeat = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.repeat_mode,
            R.layout.list_repeat_item
        )
        adapterRepeat.setDropDownViewResource(R.layout.list_repeat_item)
        spinnerRepeat.setAdapter(adapterRepeat)
        spinnerRepeat.onItemSelectedListener = this@AddTaskFragment

        when (task.repeatMode) {
            ONCE -> spinnerRepeat.setSelection(0)
            DAILY -> spinnerRepeat.setSelection(1)
            MONTOFRI -> spinnerRepeat.setSelection(2)
        }
    }

    private fun selectTime() {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourPick, minutePick ->
                val formattedMinute = if (minutePick / 10 == 0) {
                    "0$minutePick"
                } else {
                    "" + minutePick
                }
                binding.timeEt.text = "$hourPick:$formattedMinute"
                task = task.copy(hour = hourPick, minute = minutePick)
            }, hour, minute, false
        )
        timePickerDialog.show()
    }

    private fun selectDate() {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, yearPick, monthPick, dayPick ->
                binding.calendarEt.text =
                    "$dayPick:${monthPick + 1}:$yearPick"
                task = task.copy(day = dayPick, month = monthPick, year = yearPick)
            }, year, month, day
        )
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                binding.calendarEt.isGone = false
                binding.calendarTv.isGone = false
                task = task.copy(repeatMode = ONCE)
            }

            1 -> {
                binding.calendarEt.isGone = true
                binding.calendarTv.isGone = true
                task = task.copy(repeatMode = DAILY)
            }

            2 -> {
                binding.calendarEt.isGone = true
                binding.calendarTv.isGone = true
                task = task.copy(repeatMode = MONTOFRI)
            }
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }


}