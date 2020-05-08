package pokercc.android.ui.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pokercc.android.fragment.getNavigator
import pokercc.android.ui.views.databinding.ViewsViewsFragmentBinding
import pokercc.android.ui.views.rotate3d.LotteryFragment

/**
 * 自定义View
 */
class ViewsFragment : Fragment() {

    private lateinit var viewsViewsFragmentBinding: ViewsViewsFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewsViewsFragmentBinding = ViewsViewsFragmentBinding.inflate(inflater, container, false)
        return viewsViewsFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewsViewsFragmentBinding.scoreBarButton.setOnClickListener {
            getNavigator().navigateTo(ScoreBarFragment())
        }
        viewsViewsFragmentBinding.scoreChatButton.setOnClickListener {
            getNavigator().navigateTo(ScoreTrendChartFragment())
        }
        viewsViewsFragmentBinding.lotteryButton.setOnClickListener {
            getNavigator().navigateTo(LotteryFragment())
        }

    }

}
