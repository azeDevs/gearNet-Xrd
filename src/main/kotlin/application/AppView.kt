package application

import MyApp.Companion.ARTIFACT_NAME
import MyApp.Companion.BUILD_VERSION
import application.LogText.Effect.GRN
import application.LogText.Effect.LOW
import javafx.application.Platform
import javafx.scene.layout.VBox
import javafx.scene.text.TextFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import session.Session
import tornadofx.View
import tornadofx.addClass
import tornadofx.textflow
import tornadofx.vbox

typealias L = LogText

class AppView : View() {

    private val session: Session by inject()
    private lateinit var debugBox: VBox
    private lateinit var console: TextFlow

    private fun cycleGameLoop() {
        GlobalScope.launch {
            session.generateEvents()
            delay(4); cycleGameLoop()
        }
    }

    private fun cycleUILoop() {
        GlobalScope.launch {
            updateConsole()
            delay(16); cycleUILoop()
        }
    }

    private fun updateConsole() = Platform.runLater {
        updateLogs(console)
    }

    override val root = vbox { addClass(AppStyle.appContainer) }

    init {
        with(root) {
            debugBox = vbox {
                addClass(AppStyle.debugContainer)
                translateY -= 220
                translateX += 240
                console = textflow {
                    addClass(AppStyle.debugConsole)
                    translateY += 16
                }
            }
        }

        log(L("Starting "), L("$ARTIFACT_NAME ", GRN), L(BUILD_VERSION, LOW))
//        for(i in 0..3) {
//            log(
//                L("STANDARD TEXT IS DANDY WHEN YOU LOREM IPSUM AS WELL AS I CAN"),
//    //            L("000000000000000000000000000000000000000000000000000000000000", RED),
//                L("Green is what we had need for this scene", GRN),
//    //            L("0000000000000000000000000000000000000000", YLW),
//                L("All I can say is that this line is gray", LOW)
//            )
//            log(
//    //            L("STANDARD TEXT IS DANDY WHEN YOU LOREM IPSUM AS WELL AS I CAN"),
//                L("000000000000000000000000000000000000000000000000000000000000", RED),
//    //            L("Green is what we had need for this scene", GRN),
//                L("0000000000000000000000000000000000000000", YLW),
//                L("All I can say is that this line is blue", BLU)
//            )
//        }

        cycleGameLoop()
        cycleUILoop()
    }
}



