package bk.github.camerax

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import bk.github.camerax.databinding.FragmentPermissionsBinding

class PermissionsFragment : Fragment() {

    interface PermissionsPreferences {
        var isFirstPermissionsRequest: Boolean
    }

    private val preferences: PermissionsPreferences by activityViewModels<Preferences>()

    private var _bindig: FragmentPermissionsBinding? = null
    val binding: FragmentPermissionsBinding get() = _bindig!!

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            preferences.isFirstPermissionsRequest = false

            when (perms.asSequence().filter { it.key in REQUIRED_PERMISSIONS }.all { it.value }) {
                true -> popBackStack()
                else -> setupViews()
            }
        }

    private val permissionsAdapter by lazy {
        SimpleListAdapter<String>(R.layout.view_permission) { holder, item ->
            (holder.itemView as TextView).apply {
                text = context.getText(REQUEST_PERMISSIONS_RATIONALE[item]!!)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (allPermissionGranted(requireContext().applicationContext)) {
            popBackStack()
            return null
        }

        // Inflate the layout for this fragment
        _bindig = FragmentPermissionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.permissionsRecycleView.adapter = permissionsAdapter
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionGranted(requireContext().applicationContext)) {
            popBackStack()
        }
        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindig = null
    }

    private fun setupViews() {
        with(binding) {
            val deniedPermissions =
                root.context.applicationContext.let { context ->
                    REQUIRED_PERMISSIONS.filterNot { isPermissionGranted(context, it) }
                }
            val canRequirePermission = preferences.isFirstPermissionsRequest ||
                    deniedPermissions.all { shouldShowRequestPermissionRationale(it) }

            permissionsAdapter.submitList(deniedPermissions)

            if (canRequirePermission) {
                actionButton.setOnClickListener {
                    requirePermissions()
                }
            } else {
                actionButton.setOnClickListener {
                    openPermissionSettings()
                }
            }
        }
    }

    private fun openPermissionSettings() {
        startActivity(Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK +
                    Intent.FLAG_ACTIVITY_NO_HISTORY +
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        })
    }

    private fun requirePermissions() {
        requestPermissionsLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    companion object {
        val REQUIRED_PERMISSIONS: Array<String> = arrayOf(
            android.Manifest.permission.CAMERA
        )

        private val REQUEST_PERMISSIONS_RATIONALE = mapOf(
            android.Manifest.permission.CAMERA to R.string.text_camera_permission_rationale
        )

        fun allPermissionGranted(context: Context): Boolean = REQUIRED_PERMISSIONS.all {
            isPermissionGranted(context, it)
        }

        fun isPermissionGranted(context: Context, permission: String): Boolean = ContextCompat
            .checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    }
}

