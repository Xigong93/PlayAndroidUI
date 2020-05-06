package pokercc.android.playrecyclerview.itemdecorator.letter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.ui.recyclerview.R
import pokercc.android.ui.recyclerview.databinding.RecyLetterFragmentBinding

private val names = listOf(
    "Aadalbert",
    "Aadasch",
    "Aalbano",
    "Aannecke",
    "Aansel",
    "Aansorge",
    "Aansprenger",
    "Aant",
    "Aapelt",
    "Baaden",
    "Baa",
    "Baaken",
    "Baal",
    "Baare",
    "Baas",
    "Baasker",
    "Baasner",
    "Cajetan",
    "Calvisius",
    "Campe",
    "Camphausen",
    "Canaris",
    "Cantor",
    "Caprivi",
    "Carl",
    "Carla",
    "Carmen",
    "Carmer"
)

internal class LetterFragment : Fragment() {


    private lateinit var letterFragmentBinding: RecyLetterFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        letterFragmentBinding= RecyLetterFragmentBinding.inflate(inflater,container,false)

        return letterFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        letterFragmentBinding.apply {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.addItemDecoration(LetterDecorator(requireContext(), names))
            recyclerView.adapter = NameAdapter(names)
        }

    }
}

class NameViewHolder(viewGroup: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(viewGroup.context).inflate(R.layout.recy_name_item, viewGroup, false)
    ) {

    val tvName by lazy { itemView.findViewById<TextView>(R.id.tv_name) }
}

class NameAdapter(val names: List<String>) : RecyclerView.Adapter<NameViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NameViewHolder {
        return NameViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return names.size
    }

    override fun onBindViewHolder(holder: NameViewHolder, position: Int) {
        holder.tvName.text = names[position]
    }

}
