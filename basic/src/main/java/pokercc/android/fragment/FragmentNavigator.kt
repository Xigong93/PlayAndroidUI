package pokercc.android.fragmentnavigator

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentNavigator(
    private val fragmentManager: FragmentManager,
    @IdRes private val containerId: Int
) {
    companion object {
        private const val CONSTRUCTOR_BUNDLE = "constructor_bundle"
    }

    fun navigateTo(fragment: Fragment) {
        // 关键问题是怎么重建fragment
        saveConstructorArgs(fragment)
        fragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .addToBackStack(null)
            .commit()

    }

    private fun saveConstructorArgs(fragment: Fragment) {
        val constructorBundle = Bundle()

        fragment.javaClass.fields.forEach { field ->
            val arg = field.getAnnotation(Page.Arg::class.java)
            arg?.let { arg ->
                if (field.type == Int.javaClass) {
                    constructorBundle.putInt(arg.name, field.getInt(fragment))
                } else if (field.type == String.javaClass) {
                    constructorBundle.putString(arg.name, field.get(fragment) as String)
                }
            }
        }
        val args = fragment.arguments ?: Bundle()
        args.putBundle(CONSTRUCTOR_BUNDLE, constructorBundle)
        fragment.arguments = args

    }

    fun popBack() {
        fragmentManager.popBackStack()
    }
}