
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.ui.swing.debug;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * A {@link RepaintManager} that throws exceptions when Swing components are modified outside the Event Dispatch Thread.
 *
 * <p>Taken from http://weblogs.java.net/blog/2006/02/16/debugging-swing-final-summary</p>
 *
 * @author Christian Ihle
 */
public class CheckThreadViolationRepaintManager extends RepaintManager {

    /**
     * Constructor that sets the current rapaint manager to this class.
     */
    public CheckThreadViolationRepaintManager() {
        setCurrentManager(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addInvalidComponent(final JComponent component) {
        checkThreadViolations();
        super.addInvalidComponent(component);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDirtyRegion(final JComponent component, final int x, final int y, final int w, final int h) {
        checkThreadViolations();
        super.addDirtyRegion(component, x, y, w, h);
    }

    private void checkThreadViolations() {
        if (!SwingUtilities.isEventDispatchThread()) {
            final Exception exception = new Exception();
            boolean repaint = false;
            boolean fromSwing = false;
            final StackTraceElement[] stackTrace = exception.getStackTrace();

            for (final StackTraceElement st : stackTrace) {
                if (repaint && st.getClassName().startsWith("javax.swing.")) {
                    fromSwing = true;
                }

                if ("repaint".equals(st.getMethodName())) {
                    repaint = true;
                }
            }

            if (repaint && !fromSwing) {
                //no problems here, since repaint() is thread safe
                return;
            }

            exception.printStackTrace();
        }
    }
}
