package application.stream

import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import session.Player
import session.Session
import tornadofx.*
import utils.getRes

class StreamView(override val root: Parent) : Fragment() {

    var showHud = true
    var lockHud = -1
    private val bountiesGui: MutableList<BountyView> = ArrayList()
    lateinit var lobbyView: StackPane
    lateinit var matchView: StackPane
    var streamView: StackPane

    fun updateStreamLeaderboard(allPlayers: List<Player>, s: Session) {
        val players = allPlayers.filter { it.getBounty() > 0 }
        if (s.sessionMode == lockHud) {
            lobbyView.isVisible = showHud
            matchView.isVisible = !showHud
        }
        else {
            lockHud = -1
            when (s.sessionMode) {
                s.LOBBY_MODE -> {
                    lobbyView.isVisible = true
                    matchView.isVisible = false
                }
                s.LOADING_MODE -> {
                    lobbyView.isVisible = true
                    matchView.isVisible = false
                }
                s.MATCH_MODE -> {
                    lobbyView.isVisible = false
                    matchView.isVisible = true
                }
                s.SLASH_MODE -> {
                    lobbyView.isVisible = false
                    matchView.isVisible = true
                }
                s.VICTORY_MODE -> {
                    lobbyView.isVisible = true
                    matchView.isVisible = false
                }

            }
        }

        for (i in 0..3) {
            if (players.size > i) {
                bountiesGui[i].applyData(players[i], s)
                bountiesGui[i].setVisibility(showHud)
            } else {
                bountiesGui[i].applyData(Player(), s)
                bountiesGui[i].setVisibility(false)
            }
        }
    }

    fun toggleScoreboardMode(session: Session) {
        lockHud = session.sessionMode
        showHud = !showHud
        session.log("C: Scoreboard Toggle = $showHud")
        updateStreamLeaderboard(session.getPlayersList(), session)
    }

    fun toggleStreamerMode(session: Session) {
        streamView.isVisible = !streamView.isVisible
        session.log("C: Streaming Toggle = ${streamView.isVisible}")
        updateStreamLeaderboard(session.getPlayersList(), session)
    }

    init {
        with(root) {
            streamView = stackpane {
                addClass(StreamStyle.streamContainer)
                translateY -= 8
                lobbyView = stackpane {
                    maxWidth = 1280.0
                    minWidth = 1280.0
                    maxHeight = 720.0
                    minHeight = 720.0
                    imageview(getRes("gn_stream.png").toString()) {
                        viewport = Rectangle2D(0.0, 704.0, 1024.0, 320.0)
                        translateY += 380
                        fitWidth = 1280.0
                        fitHeight = 400.0
                    }
                    imageview(getRes("gn_stream.png").toString()) {
                        viewport = Rectangle2D(0.0, 704.0, 1024.0, 320.0)
                        rotate += 180
                        translateY -= 410
                        fitWidth = 1280.0
                        fitHeight = 400.0
                    }
                    vbox { translateY += 88
                        // BOUNTY VIEWS
                        for (i in 0..3) {
                            hbox {
                                bountiesGui.add(BountyView(parent, i))
                            }
                        }
                    }
                }
                matchView = stackpane {
                    maxWidth = 1280.0
                    minWidth = 1280.0
                    maxHeight = 720.0
                    minHeight = 720.0
                    isVisible = false
                    hbox {
                        alignment = Pos.TOP_CENTER
                        hbox {
                            imageview(getRes("gn_stream.png").toString()) {
                                viewport = Rectangle2D(448.0, 192.0, 576.0, 128.0)
                                fitWidth = 225.0
                                fitHeight = 50.0
                                translateY += 58
                                translateX -= 300
                            }
                        }
                        hbox {
                            imageview(getRes("gn_stream.png").toString()) {
                                viewport = Rectangle2D(448.0, 192.0, 576.0, 128.0)
                                fitWidth = 225.0
                                fitHeight = 50.0
                                translateY += 58
                                translateX += 300
                                rotate += 180.0
                            }
                        }
                    }
                }
            }
        }
    }
}