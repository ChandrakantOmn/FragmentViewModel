package com.omniwyse.fragmentviewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel


class PlayerViewModel : ViewModel() {
    val selectedPlayer = MutableLiveData<String>()

    private val repository = PlayersRepository()

    val playerList: Array<String?>
        get() = repository.getPlayers()

    fun selectPlayer(playerName: String) {
        selectedPlayer.setValue(playerName)
    }

    fun getPlayerDetails(name: String?): Player? {
        return repository.getPlayerDetails(name)
    }
}