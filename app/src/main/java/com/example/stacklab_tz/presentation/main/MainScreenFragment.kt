package com.example.stacklab_tz.presentation.main

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stacklab_tz.R
import com.example.stacklab_tz.databinding.MainFragmentBinding
import com.example.stacklab_tz.databinding.PopupLayoutBinding
import com.example.stacklab_tz.db.entities.Task
import com.example.stacklab_tz.presentation.main.adapter.MainScreenAdapter
import com.example.stacklab_tz.utils.SaveLoadData
import com.example.stacklab_tz.utils.pushes.RemindersManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainScreenFragment: Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by viewModel<MainScreenViewModel>()

    private val mainScreenAdapter: MainScreenAdapter by lazy {
        MainScreenAdapter()
    }

    private var permissions = arrayOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            checkNotificationPermission()
            mainViewModel.getAllTasks()
        }
        setToolbar()
        setView()
        observeState()
    }

    private fun setToolbar() = with(binding.toolbarMain) {
        btnAdd.apply {
            isVisible = true
            setOnClickListener {
                val action = MainScreenFragmentDirections.mainFragmentToAddTaskFragment(taskId = 0, isEdit = 0)
                findNavController().navigate(action)
            }
        }
        addText.isVisible = true
        btnAccept.isGone = true
        btnDenied.isGone = true
    }

    private fun setView() = with(binding){
        taskRv.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = mainScreenAdapter
        }

        mainScreenAdapter.menuClickListener = { task, point ->
            showPopup(requireContext(), point, task)
        }
    }

    private fun observeState() = with(binding) {
        lifecycleScope.launch {
            mainViewModel.state.collectLatest { state ->
                when (state) {
                    MainScreenViewModel.Empty -> {
                        taskRv.isGone = true
                        progressBarAllTasks.isGone = true
                        emptyTaskIv.isVisible = true
                        emptyTaskTv.isVisible = true
                        toolbarMain.addText.isVisible = true
                    }
                    MainScreenViewModel.Initial -> {
                        progressBarAllTasks.isVisible = true
                        emptyTaskTv.isGone = true
                        emptyTaskIv.isGone = true
                        taskRv.isGone = true
                    }
                    is MainScreenViewModel.NotEmpty -> {
                        taskRv.isGone = false
                        progressBarAllTasks.isGone = true
                        emptyTaskIv.isVisible = false
                        emptyTaskTv.isVisible = false
                        toolbarMain.addText.isVisible = false
                        mainScreenAdapter.setItems(state.tasks)
                    }
                }
            }
        }
    }

    private fun showPopup(context: Context, p: Point, task: Task) {

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val binding = PopupLayoutBinding.inflate(layoutInflater)
        val popUp = PopupWindow(context)
        popUp.contentView = binding.root
        popUp.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popUp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popUp.isFocusable = true

        val x = 60
        val y = 60
        popUp.setBackgroundDrawable(ColorDrawable())
        popUp.animationStyle = R.style.popup_window_animation
        popUp.showAtLocation(binding.root, Gravity.NO_GRAVITY, p.x - x, p.y - y)

        binding.btnPopupEdit.setOnClickListener {
            popUp.dismiss()
            val action = MainScreenFragmentDirections.mainFragmentToAddTaskFragment(taskId = task.id, isEdit = 1)
            findNavController().navigate(action)
        }

        binding.btnPopupDelete.setOnClickListener {
            popUp.dismiss()
            lifecycleScope.launch {
                RemindersManager.stopReminder(requireActivity(), task, task.id)
                mainViewModel.deleteTask(task)
                mainViewModel.getAllTasks()
            }
        }

    }

    private fun checkNotificationPermission() {
        try {
            when {
                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {
                    Log.d("tag_main", "checkNotificationPermission sdk: ${Build.VERSION.SDK_INT}")
                }

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                    checkPermissions(false)
                }

            }
        } catch (e: Exception) {
            Log.d("tag_main", "$e")
        }
    }

    private fun checkPermissions(shouldBack: Boolean) {
        val missingPermissions = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                missingPermissions.add(permission)
            }
        }
        if (missingPermissions.isNotEmpty()) {
            if (!shouldBack) {
                permissionsLauncher.launch(missingPermissions.toTypedArray())
            } else {
                negativePermissionCallback(requireContext().contentResolver)
            }
        } else {
            positivePermissionCallback(requireContext().contentResolver)
        }
    }

    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissionDialogCallback()
            Log.d("test_tag", "permissions = $permissions")
            checkIfFragmentAttached {
                val grantedPermissions = ArrayList<String>()
                val deniedPermissions = ArrayList<String>()
                for (entry in permissions.entries) {
                    if (entry.value) {
                        grantedPermissions.add(entry.key)
                    } else {
                        deniedPermissions.add(entry.key)
                    }
                }
                if (grantedPermissions.size == permissions.size) {
                    Log.d("test_tag", "80")
                    positivePermissionCallback(requireContext().contentResolver)
                } else {
                    var permissionConst = SYS_PERM_TIMES_OPENED_KEY
                    var messageText =
                        getString(R.string.grant_our_app_permission_to_notifications_to_work_correctly)


                    val sld = SaveLoadData(requireContext())
                    var permissionsRequestedCount = sld.loadInt(permissionConst)
                    permissionsRequestedCount++
                    sld.saveInt(permissionConst, permissionsRequestedCount)
                    if (permissionsRequestedCount > 2) {
                        AlertDialog.Builder(requireContext())
                            .setTitle(getString(R.string.permissions_needed))
                            .setMessage(messageText)
                            .setPositiveButton(getString(R.string.grant)) { _, _ ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri =
                                    Uri.fromParts("package", requireContext().packageName, null)

                                intent.data = uri
                                openSettingsLauncher.launch(intent)
                            }
                            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                                Log.d("test_tag", "setNegativeButton")
                                negativePermissionCallback(
                                    requireContext().contentResolver
                                )
                            }
                            .setCancelable(false)
                            .show()
                    }
                }
            }
        }

    private fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }

    private val openSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkPermissions(true)
    }

    private fun permissionDialogCallback() {
        Log.d("test_tag", "permissionDialogCallback")
    }

    private fun positivePermissionCallback(contentResolver: ContentResolver) {
        Log.d("test_tag", "positivePermissionCallback")
        createNotificationsChannels()
    }

    private fun negativePermissionCallback(contentResolver: ContentResolver) {
        Log.d("test_tag", "negativePermissionCallback")
    }


    private fun createNotificationsChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.reminders_notification_channel_id),
                getString(R.string.reminders_notification_channel_id),
                NotificationManager.IMPORTANCE_HIGH
            )
            ContextCompat.getSystemService(requireActivity(), NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PERMISSIONS_REQUESTED_COUNT = "PermissionsRequestedCount"
        private const val SYS_PERM_TIMES_OPENED_KEY = "SYS_PERM_TIMES_OPENED_KEY"
    }
}