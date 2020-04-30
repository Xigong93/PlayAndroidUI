package pokercc.android.ui.play

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

class PlaceHolderFragment : Fragment(R.layout.place_holder_fragment) {

    companion object {
        fun newInstance(text: String) = PlaceHolderFragment().apply {
            arguments = bundleOf(
                "text" to text
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = arguments?.getString("text")
        view.findViewById<TextView>(R.id.text).text = text
    }
}