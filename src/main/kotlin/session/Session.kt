package session

import SIMULATE_MODE
import database.DatabaseHandler
import database.SqlApi
import memscan.MemHandler
import memscan.MemRandomizer
import memscan.PlayerData
import memscan.XrdApi
import tornadofx.Controller
import utils.Duo
import utils.getIdString
import kotlin.math.max


const val LOBBY_MODE = 0
const val LOADING_MODE = 1
const val MATCH_MODE = 2
const val SLASH_MODE = 3
const val VICTORY_MODE = 4

class Session: Controller() {

    val api = ApiHandler()
    val xrdApi: XrdApi = if (SIMULATE_MODE) MemRandomizer() else MemHandler()
    val dataApi: SqlApi = DatabaseHandler()
    val matchHandler = MatchHandler()

    private var clientId: Long = -1
    val players: HashMap<Long, Player> = HashMap()

    var consoleLog = arrayListOf("C: GearNet started")
    var randomValues = false

    fun updatePlayers(): Boolean {
        var somethingChanged = false
        val playerData = xrdApi.getPlayerData().filter { it.steamUserId != 0L }

        playerData.forEach { data ->
            if (data.steamUserId != 0L) {
                // Add player if they aren't already stored
                if (!players.containsKey(data.steamUserId)) {
                    players[data.steamUserId] = Player(data); somethingChanged = true
                    log("S: New player ${getIdString(data.steamUserId)} found ... (${data.displayName})")
                }

                // The present is now the past, and the future is now the present
                val player = players[data.steamUserId] ?: Player()
                if (!player.getData().equals(data)) { somethingChanged = true }
                player.updatePlayerData(data, getActivePlayerCount())

                // Resolve if a game occured and what the reward will be
                if (matchHandler.resolveEveryone(players, this, data)) somethingChanged = true
            }
        }

        // Define the GearNet client player
        if (clientId == -1L && playerData.isNotEmpty()) {
            clientId = xrdApi.getClientSteamId()
            log("C: GearNet client defined ${getIdString(clientId)} ... (${getClient().getNameString()})")
        }

        // New match underway?
        // TODO: MAKE CABINETS TO HOUSE THESE
        val lobbyMatchPlayers = Duo(PlayerData(), PlayerData())
        val clientMatchPlayers = Duo(PlayerData(), PlayerData())
        playerData.forEach { data ->
            if (data.loadingPct in 1..99) {

                // Lobby Match stuff --------
                if (data.playerSide.toInt() == 0) lobbyMatchPlayers.p1 = data else lobbyMatchPlayers.p1 = PlayerData()
                if (data.playerSide.toInt() == 1) lobbyMatchPlayers.p2 = data else lobbyMatchPlayers.p2 = PlayerData()

                if (lobbyMatchPlayers.p1.steamUserId != -1L && lobbyMatchPlayers.p2.steamUserId != -1L && lobbyMatchPlayers.p1.cabinetLoc == lobbyMatchPlayers.p2.cabinetLoc) {
                    val newMatch = Match(matchHandler.archiveMatches.size.toLong(), lobbyMatchPlayers.p1.cabinetLoc, lobbyMatchPlayers)
                    matchHandler.lobbyMatches[newMatch.getCabinet().toInt()] = Pair(newMatch.matchId, newMatch)
                }

                // Client Match stuff --------
                if (data.cabinetLoc == getClient().getCabinet() && data.playerSide.toInt() == 0) clientMatchPlayers.p1 = data else clientMatchPlayers.p1 = PlayerData()
                if (data.cabinetLoc == getClient().getCabinet() && data.playerSide.toInt() == 1) clientMatchPlayers.p2 = data else clientMatchPlayers.p2 = PlayerData()

                if (sessionMode == MATCH_MODE && clientMatchPlayers.p1.steamUserId == -1L && clientMatchPlayers.p2.steamUserId == -1L) {
                    players.values.forEach {
                        if (it.getCabinet() == getClient().getCabinet() && it.getPlaySide().toInt() == 0) clientMatchPlayers.p1 = it.getData()
                        if (it.getCabinet() == getClient().getCabinet() && it.getPlaySide().toInt() == 1) clientMatchPlayers.p2 = it.getData()
                    }
                }
                if (matchHandler.clientMatch.matchId == -1L && clientMatchPlayers.p1.steamUserId > 0L && clientMatchPlayers.p2.steamUserId > 0L) {
                    matchHandler.clientMatch = Match(matchHandler.archiveMatches.size.toLong(), getClient().getCabinet(), clientMatchPlayers)
                    log("S: Generated Match ${getIdString(matchHandler.archiveMatches.size.toLong())}")
                    somethingChanged = true
                    setMode(LOADING_MODE)
                }
                if (sessionMode != LOBBY_MODE && sessionMode != LOADING_MODE && matchHandler.clientMatch.getHealth(0)<0 && matchHandler.clientMatch.getHealth(1)<0 && matchHandler.clientMatch.getRisc(0)<0 && matchHandler.clientMatch.getRisc(1)<0 && matchHandler.clientMatch.getTension(0)<0 && matchHandler.clientMatch.getTension(1)<0) {
                    matchHandler.clientMatch = Match()
                    somethingChanged = true
                    setMode(LOBBY_MODE)
                }

            }
        }





        return somethingChanged
    }

    fun updateClientMatch(): Boolean {
        return matchHandler.updateClientMatch(xrdApi.getMatchData(), this)
    }

    fun getActivePlayerCount() = max(players.values.filter { !it.isIdle() }.size, 1)

    private fun getClient(): Player {
        return if (clientId == -1L) Player()
        else players[clientId] ?: Player()
    }

    var sessionMode: Int = 0

    fun setMode(mode:Int) {
        sessionMode = mode
        when (mode) {
            LOBBY_MODE -> log("S: sessionMode = LOBBY_MODE")
            LOADING_MODE -> log("S: sessionMode = LOADING_MODE")
            MATCH_MODE -> log("S: sessionMode = MATCH_MODE")
            SLASH_MODE -> log("S: sessionMode = SLASH_MODE")
            VICTORY_MODE -> log("S: sessionMode = VICTORY_MODE")
        }
    }

    fun getPlayersList():List<Player> = players.values.toList()
        .sortedByDescending { item -> item.getRating() }
        .sortedByDescending { item -> item.getBounty() }
        .sortedByDescending { item -> if (!item.isIdle()) 1 else 0 }


    fun log(text:String) {
        if (consoleLog.size>50) consoleLog.removeAt(0)
        consoleLog.add(text)
        println(text)
    }

}

