
/***************************************************************************
 *   Copyright 2006-2019 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.util.Validate;

/**
 * Registers JMX MBeans.
 *
 * <p>Connect to <code>KouChat</code> with <code>JConsole</code> to get access
 * to the MBeans. <code>JConsole</code> is part of the Java SDK.</p>
 *
 * @author Christian Ihle
 */
public class JMXAgent {

    private static final Logger LOG = Logger.getLogger(JMXAgent.class.getName());

    private final JMXBeanLoader jmxBeanLoader;

    /**
     * Constructor.
     *
     * @param jmxBeanLoader The bean loader containing the JMX MBeans to register and activate.
     */
    public JMXAgent(final JMXBeanLoader jmxBeanLoader) {
        Validate.notNull(jmxBeanLoader, "JMXBeanLoader can not be null");

        this.jmxBeanLoader = jmxBeanLoader;
    }

    /**
     * Registers the MBeans, and logs any failures.
     */
    public void activate() {
        final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        final List<JMXBean> jmxBeans = jmxBeanLoader.getJMXBeans();

        try {
            for (final JMXBean jmxBean : jmxBeans) {
                registerJMXBean(mBeanServer, jmxBean);
            }
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
