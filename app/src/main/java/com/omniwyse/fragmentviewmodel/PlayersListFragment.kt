package com.omniwyse.fragmentviewmodel


/**
 * Created by Chandra Kant on 14/1/19.
 */
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

class PlayersListFragment : Fragment() {

    private var viewModel: PlayerViewModel? = null
    private var lv: ListView? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this.activity!!).get(PlayerViewModel::class.java)

        lv!!.adapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_list_item_1, viewModel!!.playerList
        )

        lv!!.setOnItemClickListener { adapter, itemView, pos, id ->
            val tv = itemView as TextView
            Toast.makeText(this.context, tv.text.toString(), Toast.LENGTH_SHORT).show()
            viewModel!!.selectPlayer(tv.text.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.palyers,
            container, false
        )
        lv = view.findViewById(R.id.players_lv)
        return view
    }
}