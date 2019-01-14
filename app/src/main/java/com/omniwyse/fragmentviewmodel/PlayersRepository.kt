package com.omniwyse.fragmentviewmodel



class PlayersRepository {
    private var players: Array<String?> = emptyArray()
    private var playerDetails: HashMap<String, Player>? = null
    fun getPlayers(): Array<String?> {
        players = arrayOfNulls(7)
        players[0] = "Rafael Nadal"
        players[1] = "Roger Federer"
        players[2] = "Juan Martin del Potro"
        players[3] = "Alexander Zverev"
        players[4] = "Grigor Dimitrov"
        players[5] = "Kevin Anderson"
        players[6] = "Marin Cilic"
        return players
    }

    fun getPlayerDetails(name: String?): Player? {
        if (playerDetails == null) {
            createPlayerDetailsMap()
        }
        return playerDetails!![name]
    }

    fun createPlayerDetailsMap() {
        playerDetails = HashMap<String, Player>()

        var player = Player()
        player.name=("Rafael Nadal")
        player.age=(32)
        player.country=("Spain")
        player.rank=(1)
        player.titles=(80)

        playerDetails!!["Rafael Nadal"] = player

        player = Player()
        player.name=("Roger Federer")
        player.age=(37)
        player.country=("Switzerland")
        player.rank=(2)
        player.titles=(98)

        playerDetails!!["Roger Federer"] = player


        player = Player()
        player.name=("Juan Martin del Potro")
        player.age=(29)
        player.country=("Argentina")
        player.rank=(3)
        player.titles=(22)

        playerDetails!!["Juan Martin del Potro"] = player

        player = Player()
        player.name=("Alexander Zverev")
        player.age=(21)
        player.country=("Germany")
        player.rank=(4)
        player.titles=(9)

        playerDetails!!["Alexander Zverev"] = player

        player = Player()
        player.name=("Grigor Dimitrov")
        player.age=(27)
        player.country=("Bulgaria")
        player.rank=(5)
        player.titles=(8)

        playerDetails!!["Grigor Dimitrov"] = player

        player = Player()
        player.name=("Kevin Anderson")
        player.age=(32)
        player.country=("South Africa")
        player.rank=(6)
        player.titles=(4)

        playerDetails!!["Kevin Anderson"] = player
    }
}