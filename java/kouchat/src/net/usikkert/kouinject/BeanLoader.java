
/***************************************************************************
 *   Copyright 2006-2009 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package net.usikkert.kouinject;

/**
 * Interface for the main IoC component, the component that loads beans and
 * autowires the dependencies.
 *
 * @author Christian Ihle
 */
public interface BeanLoader
{
	/**
	 * Loads and autowires all the beans setup in the container.
	 *
	 * @throws RuntimeException If the beans can not be instantiated
	 * or dependencies can not be satisfied.
	 */
	void loadBeans();

	/**
	 * Autowires dependencies in an already instantiated object.
	 *
	 * @param objectToAutowire The object to autowire.
	 * @throws RuntimeException If not all dependencies could be satisfied.
	 */
	void autowire( Object objectToAutowire );

	/**
	 * Gets a bean from the container for the given class.
	 *
	 * @param <T> The bean will be cast to the type specified.
	 * @param beanClass The class to get a bean for.
	 * @return An object satisfying the specified class.
	 * @throws RuntimeException If no bean is found.
	 */
	<T extends Object> T getBean( Class<T> beanClass );
}
