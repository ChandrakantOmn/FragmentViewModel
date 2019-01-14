package com.omniwyse.fragmentviewmodel

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.player_details.*


/**
 * Created by Chandra Kant on 14/1/19.
 */
class PlayerDetailsFragment : Fragment() {
    private var viewModel: PlayerViewModel? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this.activity!!).get(PlayerViewModel::class.java)
        viewModel!!.selectedPlayer.observe(this, Observer {
            displayDetails(viewModel!!.getPlayerDetails(it))
        })
    }

    @SuppressLint("SetTextI18n")
    fun displayDetails(player: Player?) {

        if (player != null) {
            Log.d("DATA", player.name+" "+player.country)
           /* name.text = player.name+""
            age.text = "" + player.age
            country.text = player.country
            titles.text = "" + player.titles
            rank.text = "" + player.rank*/
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

        return view
    }
}