package pokercc.android.ui.play

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pokercc.android.fragment.FragmentNavigator
import pokercc.android.fragment.FragmentNavigatorHost


class MainActivity : AppCompatActivity(), FragmentNavigatorHost {
    private lateinit var fragmentNavigator: FragmentNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        fragmentNavigator =
            FragmentNavigator(supportFragmentManager, R.id.app_fragment_container,
                HomeFragment()
            )

    }

    override fun getFragmentNavigator(): FragmentNavigator = fragmentNavigator
}
