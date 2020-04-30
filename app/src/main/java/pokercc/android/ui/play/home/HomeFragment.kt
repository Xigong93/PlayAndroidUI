package pokercc.android.ui.play.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pokercc.android.fragment.FragmentNavigator
import pokercc.android.ui.coordinatelayout.CoordinateLayoutFragment
import pokercc.android.ui.performance.PerformanceFragment
import pokercc.android.ui.play.PlaceHolderFragment
import pokercc.android.ui.play.R
import pokercc.android.ui.play.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {
    private lateinit var homeFragmentBinding: HomeFragmentBinding
    private var homeFragmentNavigator: FragmentNavigator? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeFragmentBinding = HomeFragmentBinding.inflate(inflater, container, false)
        if (homeFragmentNavigator == null) {
            homeFragmentNavigator = FragmentNavigator(
                childFragmentManager,
                homeFragmentBinding.homeContainer.id,
                PlaceHolderFragment.newInstance("Views")
            )
        }
        return homeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeFragmentBinding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_views -> {
                    homeFragmentNavigator?.navigateTo(PlaceHolderFragment.newInstance("Views"))
                }
                R.id.navigation_recyclerview -> {
                    homeFragmentNavigator?.navigateTo(PlaceHolderFragment.newInstance("RecyclerView"))

                }
                R.id.navigation_coordinatelayout -> {
                    homeFragmentNavigator?.navigateTo(CoordinateLayoutFragment())
                }
                R.id.navigation_performance -> {
                    homeFragmentNavigator?.navigateTo(PerformanceFragment())
                }
            }
            true
        }
    }

}
