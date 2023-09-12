package CowLooter.src.Main;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.GroundItem;

import javax.swing.*;
import java.awt.*;

@ScriptManifest(
        name = "Bredz' CowLooter",
        author = "Bredz",
        version = 1.0,
        category = Category.MONEYMAKING
)

public class Main extends AbstractScript {

    public enum State {
        COLLECTING_HIDES, MOVING_TO_COWFIELDS, BANKING
    }

    private final Area CowArea_A = new Area(3265, 3278, 3246, 3297);
    private final Area CowArea_B = new Area(3253, 3277, 3265, 3257);
    private final Area CowArea_C = new Area(new Tile(3240, 3285), new Tile(3240, 3286),
            new Tile(3241, 3287), new Tile(3241, 3286), new Tile(3241, 3285),
            new Tile(3241, 3284), new Tile(3242, 3284), new Tile(3242, 3283),
            new Tile(3243, 3282), new Tile(3243, 3283), new Tile(3243, 3284),
            new Tile(3243, 3285), new Tile(3243, 3286), new Tile(3243, 3287),
            new Tile(3243, 3288), new Tile(3242, 3288), new Tile(3241, 3288),
            new Tile(3242, 3287), new Tile(3242, 3289), new Tile(3242, 3290),
            new Tile(3242, 3291), new Tile(3243, 3291), new Tile(3243, 3292),
            new Tile(3242, 3292), new Tile(3242, 3293), new Tile(3242, 3294),
            new Tile(3241, 3294), new Tile(3241, 3295), new Tile(3240, 3296),
            new Tile(3241, 3296), new Tile(3240, 3297), new Tile(3241, 3297),
            new Tile(3241, 3298), new Tile(3242, 3298), new Tile(3242, 3297),
            new Tile(3242, 3296), new Tile(3242, 3295), new Tile(3243, 3298),
            new Tile(3243, 3297), new Tile(3243, 3296), new Tile(3243, 3295),
            new Tile(3243, 3294), new Tile(3243, 3293));

    private Timer t = new Timer();
    private int hidesBanked;

    public boolean isStarted;
    private State state;


    public void state_handler() {
        switch (state) {
            case COLLECTING_HIDES:
                loot_closest_cowhide();
                break;
            case MOVING_TO_COWFIELDS:
                move_to_cowfields();
                break;
            case BANKING:
                bank_cowhides();
                break;
        }
    }

    public void set_state() {
        if (Inventory.isFull()) {
            this.state = State.BANKING;
        } else if (CowArea_A.contains(getLocalPlayer())) {
            this.state = State.COLLECTING_HIDES;
        } else if (CowArea_B.contains(getLocalPlayer())) {
            this.state = State.COLLECTING_HIDES;
        } else {
            this.state = State.MOVING_TO_COWFIELDS;
        }
    }

    public void bank_cowhides() {
        if (!BankLocation.LUMBRIDGE.getArea(2).contains(getLocalPlayer())) {
            Walking.walk(BankLocation.LUMBRIDGE.getCenter());
            sleep(500, 800);
        } else {
            if (Bank.isOpen()) {
                if (Inventory.contains("Cowhide")) {
                    Bank.depositAllItems();
                    Bank.close();
                    this.hidesBanked = +28;
                } else {
                    Bank.close();
                    this.state = State.MOVING_TO_COWFIELDS;
                }
            } else {
                Bank.open(Bank.getClosestBankLocation());
            }
        }
    }

    public void move_to_cowfields() {
        while (!CowArea_B.contains(getLocalPlayer())) {
            Walking.walk(CowArea_B.getRandomTile());
            sleep(500, 700);
        }
        this.state = State.COLLECTING_HIDES;
    }

    private void loot_closest_cowhide() {
        GroundItem hide = GroundItems.closest("Cowhide");
        if (hide != null) {
            Tile hideTile = hide.getTile();
            if (CowArea_A.contains(hideTile) || CowArea_B.contains(hideTile) || CowArea_C.contains(hideTile)) {
                if (hide.isOnScreen()) {
                    hide.interact("Take");
                    sleepUntil(() -> getLocalPlayer().isStandingStill(), 2500);
                } else {
                    Walking.walk(hide.getTile());
                    double random = Math.random() * 10 + 1;
                    if (random > 6){
                        Camera.rotateToEntity(hide);
                    }
                }
            }
        }
    }

    public void onPaint(Graphics g) {
        g.drawString(t.formatTime(), 102, 269);
        g.drawString("Hides Banked: " + hidesBanked, 102, 289);
    }

    public void onStart() {
        this.hidesBanked = 0;
        SwingUtilities.invokeLater(() -> {
            new CowLooter.src.Gui.Gui(this);
        });
    }

    public int onLoop() {
        set_state();
        state_handler();
        return Calculations.random(600, 700);
    }
}

