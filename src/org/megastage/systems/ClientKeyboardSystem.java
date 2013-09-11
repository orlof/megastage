package org.megastage.systems;

import com.artemis.systems.VoidEntitySystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ClientKeyboardSystem extends VoidEntitySystem {
    private final static Logger LOG = Logger.getLogger(ClientKeyboardSystem.class.getName());
    private final Map<Integer, ReleasedAction> _map = new HashMap<Integer, ReleasedAction>();

    public ClientKeyboardSystem() {
        super();

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    // Remember that this is single threaded (EDT), so we can't have races.
                    ReleasedAction action = _map.remove(e.getKeyCode());

                    // ?: Do we have a corresponding RELEASED waiting?
                    if (action != null) {
                        // -> Yes, so dump it
                        action.cancel();
                    }

                    return keyPressed(e);
                } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                    Timer timer = new Timer(2, null);
                    ReleasedAction action = new ReleasedAction(e, timer);
                    timer.addActionListener(action);
                    timer.start();

                    _map.put(e.getKeyCode(), action);

                    return true;
                } else if (e.getID() == KeyEvent.KEY_TYPED) {
                    return keyTyped(e);
                }

                return false;
            }

        });
    }

    private boolean[] keyCharPressed = new boolean[256];
    private boolean[] keyCharTyped = new boolean[256];
    private boolean[] keyCodePressed = new boolean[256];

    public boolean keyPressed(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
            int keyCode = e.getKeyCode();
            if (keyCode < 256 && !keyCodePressed[keyCode]) {
                LOG.fine("PRESS KeyCode = " + Integer.toHexString(e.getKeyCode()));
                keyCodePressed[keyCode] = true;
                world.getSystem(ClientNetworkSystem.class).sendKeyPressed(keyCode);
            }
            return true;
        }

        char keyChar = e.getKeyChar();
        if (keyChar < 256 && !keyCharPressed[keyChar]) {
            LOG.fine("PRESS KeyChar = " + Integer.toHexString(e.getKeyChar()));
            keyCharPressed[keyChar] = true;
            world.getSystem(ClientNetworkSystem.class).sendKeyPressed(e.getKeyChar());
        }
        return true;
    }

    public boolean keyReleased(KeyEvent e) {

        if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
            int keyCode = e.getKeyCode();
            if (keyCode < 256 && keyCodePressed[keyCode]) {
                LOG.fine("RELEASE KeyCode = " + Integer.toHexString(e.getKeyCode()));
                keyCodePressed[keyCode] = false;
                world.getSystem(ClientNetworkSystem.class).sendKeyReleased(keyCode);
            }
            return true;
        }

        char keyChar = e.getKeyChar();
        if (keyChar < 256 && keyCharPressed[keyChar]) {
            LOG.fine("RELEASE KeyChar = " + Integer.toHexString(e.getKeyChar()));
            keyCharPressed[keyChar] = false;
            keyCharTyped[keyChar] = false;
            world.getSystem(ClientNetworkSystem.class).sendKeyReleased(keyChar);
        }
        return true;
    }

    public boolean keyTyped(KeyEvent e) {
        char keyChar = e.getKeyChar();
        if (keyChar < 256 && !keyCharTyped[keyChar]) {
            LOG.fine("TYPE KeyChar = " + Integer.toHexString(e.getKeyChar()));
            keyCharTyped[keyChar] = true;
            world.getSystem(ClientNetworkSystem.class).sendKeyTyped(keyChar);
        }
        return true;
    }

    /**
     * The ActionListener that posts RELEASED if the {@link Timer} times out (and hence the
     * repeat-action was over).
     */
    class ReleasedAction implements ActionListener {

        private final KeyEvent _originalKeyEvent;
        private Timer _timer;

        ReleasedAction(KeyEvent originalReleased, Timer timer) {
            _timer = timer;
            _originalKeyEvent = originalReleased;
        }

        void cancel() {
            _timer.stop();
            _timer = null;
            _map.remove(_originalKeyEvent.getKeyCode());
        }

        @Override
        public void actionPerformed(@SuppressWarnings ("unused") ActionEvent e) {
            // ?: Are we already cancelled?
            // (Judging by Timer and TimerQueue code, we can theoretically be raced to be posted onto EDT by TimerQueue,
            // due to some lag, unfair scheduling)
            if (_timer == null) {
                // -> Yes, so don't post the new RELEASED event.
                return;
            }
            // Stop Timer and clean.
            cancel();
            // Creating new KeyEvent (we've consumed the original).
            //KeyEvent newEvent = new RepostedKeyEvent((Component) _originalKeyEvent.getSource(),
            //        _originalKeyEvent.getID(), _originalKeyEvent.getWhen(), _originalKeyEvent.getModifiers(),
            //        _originalKeyEvent.getKeyCode(), _originalKeyEvent.getKeyChar(), _originalKeyEvent.getKeyLocation());
            // Posting to EventQueue.
            keyReleased(_originalKeyEvent);
        }
    }

    @Override
    protected void processSystem() {
        // Intentionally left empty
    }

    @Override
    protected boolean checkProcessing() {
        return false;
    }


}
