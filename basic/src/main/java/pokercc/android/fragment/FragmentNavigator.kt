package pokercc.android.fragment

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentNavigator(
    private val fragmentManager: FragmentManager,
    @IdRes private val containerId: Int,
    private val startFragment: Fragment
) {
    companion object {
        private const val START_POINT = "start_point"
    }

    init {
        if (fragmentManager.findFragmentByTag(START_POINT) == null) {
            fragmentManager.beginTransaction()
                .replace(containerId, startFragment, START_POINT)
                .commit()
        }
    }

    fun navigateTo(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .addToBackStack(null)
            .commit()

    }


    fun popBack() {
        fragmentManager.popBackStack()
    }
}

fun Fragment.getNavigator(): FragmentNavigator {
    if (this is FragmentNavigatorHost) {
        return this.getFragmentNavigator()
    }
    val activity = requireActivity()
    if (activity is FragmentNavigatorHost) {
        return activity.getFragmentNavigator()
    }
    throw IllegalStateException("navigator Not found")
}

interface FragmentNavigatorHost {
    fun getFragmentNavigator(): FragmentNavigator
}