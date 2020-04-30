package pokercc.android.ui.coordinatelayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pokercc.android.fragment.getNavigator
import pokercc.android.ui.coordinatelayout.boss.BossZhiPinFragment
import pokercc.android.ui.coordinatelayout.databinding.CooCoordinateLayoutFragmentBinding

/**
 * A simple [Fragment] subclass.
 */
class CoordinateLayoutFragment : Fragment() {

    private lateinit var coordinateLayoutFragmentBinding: CooCoordinateLayoutFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        coordinateLayoutFragmentBinding =
            CooCoordinateLayoutFragmentBinding.inflate(inflater, container, false)
        return coordinateLayoutFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        coordinateLayoutFragmentBinding.bossButton.setOnClickListener {
            getNavigator().navigateTo(BossZhiPinFragment())
        }
    }

}
