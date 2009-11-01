
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

package net.usikkert.kouchat.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author Christian Ihle
 */
public class DefaultAnnotatedBeanLoader implements BeanLoader
{
	private static final Logger LOG = Logger.getLogger( DefaultAnnotatedBeanLoader.class.getName() );

	private static final String BASE_PACKAGE = "net.usikkert.kouchat";

	protected final Map<Class<?>, Object> beans = new HashMap<Class<?>, Object>();

	@Override
	public void loadBeans()
	{
		try
		{
			loadAndAutowireBeans();
		}

		catch ( final Exception e )
		{
			throw new RuntimeException( e );
		}
	}

	@Override
	public void autowire( final Object objectToAutowire )
	{
		try
		{
			autowire( objectToAutowire.getClass(), objectToAutowire );
		}

		catch ( final Exception e )
		{
			throw new RuntimeException( e );
		}
	}

	@Override
	public Object getBean( final Class<?> beanNeeded )
	{
		return findBean( beanNeeded );
	}

	@Override
	public void addBean( final Object bean )
	{
		final Class<? extends Object> beanClass = bean.getClass();
		beans.put( beanClass, bean );
	}

	private void loadAndAutowireBeans() throws Exception
	{
		final Set<Class<?>> detectedBeans = findBeans( Bean.class );

		for ( final Class<?> beanClass : detectedBeans )
		{
			LOG.info( "Bean found: " + beanClass.getName() );
			final Object bean = instantiateBean( beanClass );
			beans.put( beanClass, bean );
		}

		autowireDetectedBeans();
	}

	private Object instantiateBean( final Class<?> beanClass ) throws Exception
	{
		final Constructor<?> constructor = beanClass.getDeclaredConstructor();
		final boolean originalAccessible = constructor.isAccessible();
		constructor.setAccessible( true );
		final Object newInstance = constructor.newInstance();
		constructor.setAccessible( originalAccessible );

		return newInstance;
	}

	private Set<Class<?>> findBeans( final Class<? extends Annotation> annotation )
	{
		final ClassPathScanner classPathScanner = new ClassPathScanner();
		final Set<Class<?>> allClasses = classPathScanner.findClasses( BASE_PACKAGE );
		final Set<Class<?>> detectedBeans = new HashSet<Class<?>>();

		for ( final Class<?> clazz : allClasses )
		{
			if ( clazz.isAnnotationPresent( annotation ) )
			{
				detectedBeans.add( clazz );
			}
		}

		return detectedBeans;
	}

	private void autowireDetectedBeans() throws Exception
	{
		final Iterator<Class<?>> beanIterator = beans.keySet().iterator();

		while ( beanIterator.hasNext() )
		{
			final Class<?> beanClass = beanIterator.next();
			autowire( beanClass, beans.get( beanClass ) );
		}
	}

	private void autowire( final Class<?> classToAutowire, final Object objectToAutowire ) throws Exception
	{
		final Field[] fields = classToAutowire.getDeclaredFields();

		for ( final Field field : fields )
		{
			if ( field.isAnnotationPresent( Inject.class ) )
			{
				LOG.info( "Field detected for autowiring: " + field );
				final boolean originalAccessible = field.isAccessible();
				field.setAccessible( true );
				final Object bean = findBean( field );
				field.set( objectToAutowire, bean );
				field.setAccessible( originalAccessible );
			}
		}
	}

	private Object findBean( final Field field )
	{
		final Class<?> beanNeeded = field.getType();
		return findBean( beanNeeded );
	}

	private Object findBean( final Class<?> beanNeeded )
	{
		final Iterator<Class<?>> beanIterator = beans.keySet().iterator();
		final List<Object> matches = new ArrayList<Object>();

		while ( beanIterator.hasNext() )
		{
			final Class<?> beanClass = beanIterator.next();

			if ( beanNeeded.isAssignableFrom( beanClass ) )
			{
				matches.add( beans.get( beanClass ) );
			}
		}

		if ( matches.size() == 0 )
		{
			throw new RuntimeException( "No matching bean found for autowiring " + beanNeeded );
		}

		else if ( matches.size() > 1 )
		{
			throw new RuntimeException( "Wrong number of beans found for autowiring " + beanNeeded + " " + matches );
		}

		return matches.get( 0 );
	}
}
