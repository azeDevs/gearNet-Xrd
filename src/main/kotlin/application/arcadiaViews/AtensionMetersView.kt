package application.arcadiaViews

import arcadia.ArcadeView
import arcadia.Arcadia
import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import tornadofx.Fragment
import tornadofx.stackpane

class AtensionMetersView(override val root: Parent) : Fragment(), ArcadeView {

    private val a: Arcadia by inject()
    private val container: StackPane
    private lateinit var atensionWheel: AtensionWheelView
    private lateinit var atensionGaugeR: AtensionGaugeView
    private lateinit var atensionGaugeB: AtensionGaugeView

    init {
        with(root) {
            container = stackpane {
                atensionWheel = AtensionWheelView(parent)
                atensionGaugeR = AtensionGaugeView(parent, PLAYER_1)
                atensionGaugeB = AtensionGaugeView(parent, PLAYER_2)
            }
        }
    }

    override fun animate() {
        atensionWheel.animate()
        atensionGaugeR.animate()
        atensionGaugeB.animate()
    }

    override fun update() = Platform.runLater {
        atensionWheel.update()
        atensionGaugeR.update()
        atensionGaugeB.update()
    }

}