package application

import azUtils.addCommas
import azUtils.getRes
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import session.Character.getCharacterPortrait
import session.Player
import tornadofx.*
import kotlin.random.Random

class PlayerView(override val root: Parent) : Fragment() {

    lateinit var character: ImageView
    lateinit var handle: Label
    lateinit var statusBar: HBox
    lateinit var bounty1: Label
    lateinit var bounty2: Label

    lateinit var chain1: Label
    lateinit var chain2: Label
    lateinit var change: Label

    lateinit var status: Label
    lateinit var location: Label

    lateinit var record: Label

    init { with(root) {
        hbox { addClass(PlayerStyle.playerContainer)
            minWidth = 400.0
            maxWidth = 400.0
            character = imageview(getRes("gn_atlas.png").toString()) {
                setViewport(Rectangle2D(576.0, 192.0, 64.0, 64.0))
                translateY -= 2.0
                translateX -= 2.0
            }
            vbox {
                translateX -= 4.0
                minWidth = 340.0
                maxWidth = 340.0
                stackpane { alignment = Pos.CENTER_LEFT
                    statusBar = hbox { addClass(PlayerStyle.statusBar);
                        translateY += 1.0
                        maxWidth = 0.0
                    }
                    hbox {
                        handle = label("") {
                            addClass(PlayerStyle.handleText)
                            translateY += 1.0
                        }
                        status = label("") {
                            addClass(PlayerStyle.statusText)
                            translateX -= 160.0
                            translateY += 4.0
                            maxWidth = 160.0
                            minWidth = 160.0
                        }
                    }
                }

                hbox {
                    vbox { addClass(PlayerStyle.bountyBackdrop)
                        translateY += 2.0
                        stackpane {
                            translateX += 10.0
                            translateY -= 5.0
                            bounty2 = label("") { addClass(PlayerStyle.bountyShadow)
                                scaleX += 0.05
                                scaleY += 0.20
                            }
                            bounty1 = label("") { addClass(PlayerStyle.bountyText) }
                        }
                    }
                    vbox {
                        stackpane {
                            translateX -= 64.0
                            chain2 = label("") { addClass(PlayerStyle.chainShadow)
                                translateY += 6.0
                                scaleY += 0.20
                            }
                            chain1 = label("") { addClass(PlayerStyle.chainText)
                                translateY += 6.0
                                scaleX -= 0.20
                            }
                        }
                    }
                    vbox { addClass(PlayerStyle.statsBackdrop)
                        translateX -= 144.0
                        location = label("") { addClass(PlayerStyle.recordText) }
                        record = label("") { addClass(PlayerStyle.recordText) }
                    }
                    vbox { setPadding(Insets(0.0,0.0,0.0,8.0))
                        translateX -= 525.0
                        translateY += 5.0
                        change = label("") { addClass(PlayerStyle.changeText) }
                    }
                }
            }
        }
    } }

    fun applyData(p: Player) {
        Platform.runLater({
            if (true) applyRandomData(p)
            else if (p.getSteamId() > 0L) {
                character.setViewport(getCharacterPortrait(p.getData().characterId))
                handle.text = p.getNameString(); handle.setVisible(true)
                statusBar.maxWidth = 335.0 * (p.getLoadPercent()*0.01)

                bounty1.text = p.getBountyString()
                bounty2.text = p.getBountyString()

                chain1.text = p.getChainString()
                chain2.text = p.getChainString()
                change.text = p.getChangeString()

                status.text = p.getStatusString()
                location.text = p.getCabinetString() + " " + p.getPlaySideString()
                record.text = p.getRecordString()
            } else {
                character.setViewport(Rectangle2D(576.0, 192.0, 64.0, 64.0))
                handle.text = ""; handle.setVisible(false)
                bounty1.text = ""
                bounty2.text = ""

                chain1.text = ""
                chain2.text = ""
                change.text = ""

                record.text = ""
                location.text = ""
                status.text = ""
            }
        })
    }

    private fun applyRandomData(p: Player) {
        val rn1 = arrayListOf("a","ze","st","ar","Koov","er","Te","chno","Lost","Ill","usion","isio","avi","La","bryz","Cath","at","icus","gry","phen","Soff","ish","Aoi","Mai","den","epo","ck","robo","sting","ray","sw","eet","X","jam","Tar","kus","Ev","ir","Dwa","jio","Big","bow","sa","TK","sha","dow","Del","rian","son","ny","wort","zik","Bon","bei","beez","uz","agri","guck","le","Jub","Kiz","zer","Day","men","dou","Pep","pery","Sp","lash","Kuro","gane","Ri","ven","Whoo","Boo","st","Whom","Sput","nik","Mk0","Cre","amy","Shits","Poo","Lord","Shin","Mun","chy","Mad","ao","Pan","Je","yu","dus","Sin","Pom","pa","dude","Riss","ay","Ja","yne","Mk1","Bea","Whi","Octo","pimp","Bon","bei","eez","us","Guck","le","oki","zeme","69","420","XxX","xXx","Seph","iroth","Sex","Haver","Weed","Pan","zee","boo","ties","Der","win","Sla","Elv","Sha","dow","Bla","ck","Sna","ke","Pru","sha","Cute","Miku","Rock","Man","Girl","Boy","Bitch","Bro","tato","seph","heim","Free","Wind","Jutsu","Ninja","obi","chan","kun","Kami","Poke","Kill","mon","Digi","Ahe","gao","Face","ken","dojo","Dead","State","God","Gren","dy","lici","ous","Love","Fire","Flame","Ice","Elf","Fair","Drag","Devil","Jin","Muge","Moog","Lock","Kara","Sol","Badguy","Jelly","Rich","Fake","Fraud","Pro","Sport","Spice","Butt","Blood","Evil","Goo","HUE","HUEHUE","HURR","HNNG","Gai","jen","Ota","ku","con")
        val rn2 = arrayListOf("b","c","d","f","g","h","j","k","l","m","n","p","q","r","s","t","v","w","x","z")
        val rn3 = arrayListOf("a","e","i","o","u","a","e","i","o","u","y")
        val bountyStr = addCommas(Random.nextInt(1222333).toString())
        val loadingInt = Random.nextInt(100)
        val changeInt = Random.nextInt(-444555, 666777)
        val chainInt = Random.nextInt(0, 9)
        val winsInt = Random.nextInt(44)
        character.setViewport(Rectangle2D(Random.nextInt(8) * 64.0, Random.nextInt(4) * 64.0, 64.0, 64.0))

        handle.text = "${rn2[Random.nextInt(rn2.size)].toUpperCase()}${rn3[Random.nextInt(rn3.size)]}"; var nameStep = 0
        for (i in 0..Random.nextInt(2,4)) { var part = ""
            if (nameStep % 2 == 0) part += "${rn2[Random.nextInt(rn2.size)]}${rn3[Random.nextInt(rn3.size)]}"
            else part += "${rn1[Random.nextInt(rn1.size)]}"
            when (Random.nextInt(16)) {
                1 -> part += " ${rn2[Random.nextInt(rn2.size)].toUpperCase()}${rn3[Random.nextInt(rn3.size)]}"
                2 -> part += "_${rn2[Random.nextInt(rn2.size)].toUpperCase()}${rn3[Random.nextInt(rn3.size)]}"
                3 -> part += "${rn2[Random.nextInt(rn2.size)]}${rn3[Random.nextInt(rn3.size)]}"
                4 -> part += Random.nextInt(1000)
                5 -> part = part.toUpperCase()
                6 -> part = part.toLowerCase()
                else -> part += ""
            }
            nameStep++
            handle.text += part
        }

        statusBar.maxWidth = 335.0 * (loadingInt * 0.01)

        bounty1.text = "${bountyStr} W$"
        bounty2.text = "${bountyStr} W$"

        chain1.text = if (chainInt >= 8) "★" else if (chainInt > 0) chainInt.toString() else ""
        chain2.text = if (chainInt >= 8) "★" else if (chainInt > 0) chainInt.toString() else ""

        if (changeInt > 0) change.setTextFill(c("#84c928")) else change.setTextFill(c("#d22e44"))
        change.text = p.getChangeString(1f, changeInt)

        record.text = "W:${winsInt}  /  M:${winsInt + Random.nextInt(44)}"
        location.text = "Roaming Lobby"
        status.text = "Standby: ${Random.nextInt(1, 8)} [${loadingInt}%]"
    }

}