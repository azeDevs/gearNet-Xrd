package models

import memscan.FighterData
import memscan.MatchData
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import session.Session
import session.Session.Companion.MATCH_MODE
import session.Session.Companion.SLASH_MODE
import session.Session.Companion.VICTORY_MODE
import session.log
import utils.Duo
import utils.keepInRange


class Match (val matchId: Long = -1, private val cabinetId: Byte = -0x1, private val players: Duo<FighterData> = Duo(FighterData(), FighterData()), matchData: MatchData = MatchData()) {

    private var winner = -1
    private var roundStarted = false
    private val snaps = arrayListOf(matchData)

    // Gotten from MatchData, else gotten from LobbyData (LOBBY QUALITY DATA)
    private var character = Duo(players.p1.characterId.toInt(), players.p2.characterId.toInt())
    private var handle = Duo(players.p1.displayName, players.p2.displayName)
    private var rounds = Duo(matchData.rounds.first, matchData.rounds.second)
    private var health = Duo(matchData.health.first, matchData.health.second)

    // Gotten from MatchData, else considered useless (MATCH QUALITY DATA)
    private var matchTimer = matchData.timer
    private var tension = Duo(matchData.tension.first, matchData.tension.second)
    private var canBurst = Duo(matchData.canBurst.first, matchData.canBurst.second)
    private var stunProgress = Duo(matchData.stunProgress.first, matchData.stunProgress.second)
    private var maxStun = Duo(matchData.stunMaximum.first, matchData.stunMaximum.second)
    private var strikeStun = Duo(matchData.strikeStun.first, matchData.strikeStun.second)
    private var guardGauge = Duo(matchData.guardGauge.first, matchData.guardGauge.second)

    fun getData() = snaps.last()
    fun allData() = snaps

    fun updateMatchSnap(updatedData: MatchData, session: Session): Boolean {
        if (!getData().equals(updatedData)) {

            snaps.add(updatedData)
            matchTimer = updatedData.timer

            health.p1 = keepInRange(getData().health.first)//, 0, 420)
            stunProgress.p1 = keepInRange(getData().stunProgress.first)//, 0, 8000)
            maxStun.p1 = keepInRange(getData().stunMaximum.first)//, 0, 8000)
            tension.p1 = keepInRange(getData().tension.first)//, 0, 10000)
            guardGauge.p1 = keepInRange(getData().guardGauge.first)//, 0, 12800)
            rounds.p1 = updatedData.rounds.first
            canBurst.p1 = getData().canBurst.first
            strikeStun.p1 = getData().strikeStun.first

            health.p2 = keepInRange(getData().health.second)//, 0, 420)
            stunProgress.p2 = keepInRange(getData().stunProgress.second)//, 0, 8000)
            maxStun.p2 = keepInRange(getData().stunMaximum.second)//, 0, 8000)
            tension.p2 = keepInRange(getData().tension.second)//, 0, 10000)
            guardGauge.p2 = keepInRange(getData().guardGauge.second)//, 0, 12800)
            rounds.p2 = updatedData.rounds.second
            canBurst.p2 = getData().canBurst.second
            strikeStun.p2 = getData().strikeStun.second

            // Has the round started?
            if (!roundStarted && getHealth(PLAYER_1) == 420 && getHealth(PLAYER_2) == 420 && getWinner() == -1) {
                roundStarted = true
                session.setMode(MATCH_MODE)
                log("Round Start, DUEL ${getRounds(PLAYER_1) + getRounds(PLAYER_2) + 1}, LET'S ROCK!")
            }

            // Has the round ended, and did player 1 win?
            if (roundStarted && getWinner()==-1 && getHealth(PLAYER_2) == 0 && getHealth(PLAYER_1) != getHealth(PLAYER_2) ) {
                roundStarted = false
                session.setMode(SLASH_MODE)
                log("Round ${getRounds(PLAYER_2)}/2 End, PLAYER_1 wins (${players.p1.displayName})")
            }

            // Has the round ended, and did player 2 win?
            if (roundStarted && getWinner() ==-1 && getHealth(PLAYER_1) == 0 && getHealth(PLAYER_2) != getHealth(PLAYER_1)) {
                roundStarted = false
                session.setMode(SLASH_MODE)
                log("Round ${getRounds(PLAYER_2)}/2 End, PLAYER_2 wins (${players.p2.displayName})")
            }

            // Did player 1 win the match?
            if (getRounds(PLAYER_1) == 2 && winner == -1) {
                winner = 0
                session.setMode(VICTORY_MODE)
                log("Match End, PLAYER_1 takes the match (${getHandleString(PLAYER_1)})")
            }

            // Did player 2 win the match?
            if (getRounds(PLAYER_2) == 2 && winner == -1) {
                winner = 1
                session.setMode(VICTORY_MODE)
                log(
                    "Match End, PLAYER_2 takes the match (${getHandleString(PLAYER_2)})"
                )
            }

            return true

        } else return false
    }

    fun getWinner():Int = winner
    fun getTimer():Int = matchTimer
    fun getRounds(side:Int):Int = rounds.p(side)
    fun getHealth(side:Int):Int = health.p(side)
    fun getStunProgress(side:Int):Int = stunProgress.p(side)
    fun getMaxStun(side:Int):Int = maxStun.p(side)
    fun getCharacter(side:Int):Int = character.p(side)
    fun getTension(side:Int):Int = tension.p(side)
    fun getRisc(side:Int):Int = guardGauge.p(side)
    fun getBurst(side:Int):Boolean = canBurst.p(side)
    fun getHitStun(side:Int):Boolean = strikeStun.p(side)

    fun getHandleString(side:Int):String = handle.p(side)
    fun getHealthString(side:Int):String = "HP: ${getHealth(side)} / 420"
    fun getStunString(side:Int):String = "Stun: ${getStunProgress(side)} / ${getMaxStun(side)}"
    fun getTensionString(side:Int):String = "Tension: ${getTension(side)} / 10000"
    fun getRiscString(side:Int):String = "   RISC: ${getRisc(side)} / 12800"
    fun getBurstString(side:Int):String = "  Burst: ${getBurst(side)}"
    fun getHitStunString(side:Int):String = "  IsHit: ${getHitStun(side)}"

    fun getCabinet():Byte = cabinetId
    fun getCabinetString(cabId:Int = getCabinet().toInt()): String {
        return when(cabId) {
            0 -> "CABINET Α"
            1 -> "CABINET Β"
            2 -> "CABINET Γ"
            3 -> "CABINET Δ"
            else -> "$cabId"
        }
    }
}