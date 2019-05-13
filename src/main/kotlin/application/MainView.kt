package application

import javafx.geometry.Insets
import javafx.geometry.Pos
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import session.Player
import session.Session
import tornadofx.*


class MainView : View() {
    override val root: Form = Form()
    val modulesGui: MutableList<ModuleView> = ArrayList()
    val playersGui: MutableList<PlayerView> = ArrayList()
    val matchesGui: MutableList<MatchView> = ArrayList()

    val session: Session by inject()

    fun cycleDatabase() { GlobalScope.launch {
        modulesGui.get(2).reset(session.dataApi.isConnected())
        delay(4096); cycleDatabase() } }
    fun cycleMemScan() { GlobalScope.launch {
        modulesGui.get(0).reset(session.xrdApi.isConnected())
        if (session.xrdApi.isConnected() && session.updatePlayers()) redrawAppUi()
        delay(256); cycleMemScan() } }
    fun cycleUi() { GlobalScope.launch {
        modulesGui.forEach { it.nextFrame() }
        delay(64); cycleUi() } }
    fun redrawAppUi() {
        modulesGui.get(1).reset(true)
        val uiUpdate: List<Player> = session.players.values.toList().sortedByDescending { item -> item.getRating() }.sortedByDescending { item -> item.getBounty() }.sortedByDescending { item -> if (!item.isIdle()) 1 else 0 }
        for (i in 0..7) if (uiUpdate.size > i) playersGui.get(i).applyData(uiUpdate.get(i))
        else playersGui.get(i).applyData(Player()) }

    init {
        with(root) { addClass(MainStyle.appContainer)
            hbox { alignment = Pos.TOP_CENTER; addClass(MainStyle.debuggable)
                setPadding(Insets(16.0, 0.0, 0.0, 0.0))

                // LEFT SIDE COLUMN ========
                vbox { alignment = Pos.TOP_CENTER; addClass(MainStyle.debuggable); setSpacing(2.0)
                    minWidth = 520.0
                    maxWidth = 520.0

                    // MODULE INFO
                    hbox { alignment = Pos.CENTER; addClass(MainStyle.debuggable)
                        vbox { modulesGui.add(ModuleView(parent, "Guilty Gear Xrd")); addClass(MainStyle.debuggable) }
                        vbox { modulesGui.add(ModuleView(parent, "GearNet Client")); addClass(MainStyle.debuggable) }
                        vbox { modulesGui.add(ModuleView(parent, "Stats Database")); addClass(MainStyle.debuggable) }
                    }

                    // MATCH VIEWS
                    for (i in 0..3) hbox { matchesGui.add(MatchView(parent)); addClass(MainStyle.debuggable) }

                }

                // RIGHT SIDE COLUMN ========
                vbox { alignment = Pos.TOP_CENTER; addClass(MainStyle.debuggable); setSpacing(2.0)

                    // LOBBY INFO
                    hbox { addClass(MainStyle.debuggable)
                        label("LOBBY_MAX_LENGTH") { addClass(MainStyle.lobbyName) }
                    }

                    // PLAYER VIEWS
                    for (i in 0..7) hbox { playersGui.add(PlayerView(parent)); addClass(MainStyle.debuggable) }
                }
            }

            cycleDatabase()
            cycleMemScan()
            cycleUi()
        }

    }

}



