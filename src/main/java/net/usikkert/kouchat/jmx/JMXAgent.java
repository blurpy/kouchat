
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
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

package net.usikkert.kouchat.jmx;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.net.ConnectionWorker;

/**
 * Registers JMX MBeans.
 *
 * <p>Connect to <code>KouChat</code> with <code>JConsole</code> to get access
 * to the MBeans. <code>JConsole</code> is part of the Java SDK.</p>
 *
 * <p>The following MBeans are registered:</p>
 *
 * <ul>
 *   <li>{@link NetworkInformation}</li>
 *   <li>{@link ControllerInformation}</li>
 *   <li>{@link GeneralInformation}</li>
 * </ul>
 *
 * @author Christian Ihle
 */
public class JMXAgent {

    private static final Logger LOG = Logger.getLogger(JMXAgent.class.getName());

    /**
     * Default constructor. Registers the MBeans, and logs any failures.
     *
     * @param controller The controller.
     * @param connectionWorker The connection worker.
     */
    public JMXAgent(final Controller controller, final ConnectionWorker connectionWorker) {
        final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        try {
            registerJMXBean(mBeanServer, new NetworkInformation(connectionWorker));
            registerJMXBean(mBeanServer, new ControllerInformation(controller));
            registerJMXBean(mBeanServer, new GeneralInformation());
        }

        catch (final JMException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        }
    }

    private void registerJMXBean(final MBeanServer mBeanServer, final JMXBean jmxBean) throws JMException {
        final ObjectName generalInfoName = createObjectName(jmxBean);
        mBeanServer.registerMBean(jmxBean, generalInfoName);
    }

    private ObjectName createObjectName(final JMXBean jmxBean) throws JMException {
        return new ObjectName(Constants.APP_NAME + ":name=" + jmxBean.getBeanName());
    }
}
