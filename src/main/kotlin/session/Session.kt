package session

import memscan.XrdListener
import tornadofx.Controller
import twitch.BetListener


class Session : Controller() {

    companion object {
        const val LOBBY_MODE = 0
        const val LOADING_MODE = 1
        const val MATCH_MODE = 2
        const val SLASH_MODE = 3
        const val VICTORY_MODE = 4
    }

    private val xrdListener = XrdListener()
    private val betListener = BetListener()

}
//
//    fun updatePlayers(): Boolean {
//
//        // New match underway?
//        // TODO: MAKE CABINETS TO HOUSE THESE
//        // NOTE: THIS IS WEHRE YOU LEFT OFF
//        val lobbyMatchPlayers = Duo(PlayerData(), PlayerData())
//        val clientMatchPlayers = Duo(PlayerData(), PlayerData())
//
//        lobbyHandler.getPlayers().filter { it.isLoading() }.forEach { p ->
//
//            // Lobby Match stuff --------
//            if (p.getPlaySide() == 0) lobbyMatchPlayers.p1 = p.getData()
//            else lobbyMatchPlayers.p1 = PlayerData()
//            if (p.getPlaySide() == 1) lobbyMatchPlayers.p2 = p.getData()
//            else lobbyMatchPlayers.p2 = PlayerData()
//
//            if (lobbyMatchPlayers.p1.steamId != -1L
//                && lobbyMatchPlayers.p2.steamId != -1L
//                && lobbyMatchPlayers.p1.cabinetId == lobbyMatchPlayers.p2.cabinetId) {
//                val newMatch = Match(matchHandler.archiveMatches.size.toLong(), lobbyMatchPlayers.p1.cabinetId, lobbyMatchPlayers)
//                matchHandler.lobbyMatches[newMatch.getCabinet().toInt()] = Pair(newMatch.matchId, newMatch)
//            }
//
//            // Client Match stuff --------
//            if (p.getCabinet() == getClient().getCabinet() && p.getPlaySide() == 0)
//                clientMatchPlayers.p1 = p.getData() else clientMatchPlayers.p1 = PlayerData()
//            if (p.getCabinet() == getClient().getCabinet() && p.getPlaySide() == 1)
//                clientMatchPlayers.p2 = p.getData() else clientMatchPlayers.p2 = PlayerData()
//            matchHandler.updateClientMatch(lobbyHandler.getMatchData(), this)
//
//            // Set sessionMode to MATCH_MODE
//            if (sessionMode == MATCH_MODE
//                && clientMatchPlayers.p1.steamId == -1L
//                && clientMatchPlayers.p2.steamId == -1L) {
//                players.values.forEach {
//                    if (it.getCabinet() == getClient().getCabinet()
//                        && it.getPlaySide().toInt() == 0)
//                        clientMatchPlayers.p1 = it.getData()
//                    if (it.getCabinet() == getClient().getCabinet()
//                        && it.getPlaySide().toInt() == 1)
//                        clientMatchPlayers.p2 = it.getData()
//                }
//            }
//            // Set sessionMode to LOADING_MODE
//            if (matchHandler.clientMatch.matchId == -1L
//                && clientMatchPlayers.p1.steamId > 0L
//                && clientMatchPlayers.p2.steamId > 0L) {
//                matchHandler.clientMatch =
//                    Match(matchHandler.archiveMatches.size.toLong(), getClient().getCabinet(), clientMatchPlayers)
//                log("[SESS] Generated Match ${getIdString(matchHandler.archiveMatches.size.toLong())}")
//                somethingChanged = true
//                setMode(LOADING_MODE)
//            }
//            // Set sessionMode to LOBBY_MODE
//            if (sessionMode != LOBBY_MODE && sessionMode != LOADING_MODE
//                && matchHandler.clientMatch.getHealth(0) < 0
//                && matchHandler.clientMatch.getHealth(1) < 0
//                && matchHandler.clientMatch.getRisc(0) < 0
//                && matchHandler.clientMatch.getRisc(1) < 0
//                && matchHandler.clientMatch.getTension(0) < 0
//                && matchHandler.clientMatch.getTension(1) < 0
//            ) {
//                matchHandler.clientMatch = Match()
//                somethingChanged = true
//                setMode(LOBBY_MODE)
//            }
//
//        }
//
//        // Filter Twitch messages for valid commands to execute
//        bettingHandler.updateGamblers()
//
//        return somethingChanged
//    }
//
//    fun updateClientMatch(xrdApi: XrdApi): Boolean {
//        return matchHandler.updateClientMatch(xrdApi.getMatchData(), this)
//    }
//
//    fun getActivePlayerCount() = max(players.values.filter { !it.isIdle() }.size, 1)
//
//
//    var sessionMode: Int = 0
//
//    fun setMode(mode: Int) {
//        sessionMode = mode
//        when (mode) {
//            LOBBY_MODE -> log("[SESS] sessionMode = LOBBY_MODE")
//            LOADING_MODE -> log("[SESS] sessionMode = LOADING_MODE")
//            MATCH_MODE -> log("[SESS] sessionMode = MATCH_MODE")
//            SLASH_MODE -> log("[SESS] sessionMode = SLASH_MODE")
//            VICTORY_MODE -> log("[SESS] sessionMode = VICTORY_MODE")
//        }
//    }
//
//    fun getPlayersList(): List<Player> = players.values.toList()
//        .sortedByDescending { item -> item.getRating() }
//        .sortedByDescending { item -> item.getBounty() }
//        .sortedByDescending { item -> if (!item.isIdle()) 1 else 0 }
//
//    // CLIENT
//    fun getClient(): Player {
//        if (!players.isEmpty()) {
//            val clientId = xrdApi.getClientSteamId()
//            return players.values.first { it.getSteamId() == clientId }
//        } else return Player()
//    }
//}

var consoleLog = arrayListOf("[CLIE] GearNet // Bounty Bets 0.6.3")
fun log(text: String) {
    if (consoleLog.size > 15) consoleLog.removeAt(0)
    consoleLog.add(text)
    println(text)
}

