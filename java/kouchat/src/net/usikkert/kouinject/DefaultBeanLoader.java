
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * KouInjector
 * scope = singleton
 * scope = unique instance
 * child context?
 * postconstruct
 * predestroy
 * circular deps
 *
 * @author Christian Ihle
 */
public class DefaultBeanLoader implements BeanLoader
{
	private static final Logger LOG = Logger.getLogger( DefaultBeanLoader.class.getName() );

	private final Map<Class<?>, Object> beanMap = new HashMap<Class<?>, Object>();

	private final Map<Class<?>, BeanData> beanDataMap = new HashMap<Class<?>, BeanData>();

	private final BeanDataHandler beanDataHandler;

	public DefaultBeanLoader( final BeanDataHandler beanDataHandler )
	{
		this.beanDataHandler = beanDataHandler;
	}

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
			final BeanData beanData = beanDataHandler.getBeanData( objectToAutowire.getClass(), true );
			final List<Class<?>> missingDependencies = findMissingDependencies( beanData );

			if ( allDependenciesAreMet( missingDependencies ) )
			{
				autowireBean( beanData, objectToAutowire );
			}

			else
			{
				throw new RuntimeException( "Could not autowire object, missing dependencies: " + missingDependencies );
			}
		}

		catch ( final Exception e )
		{
			throw new RuntimeException( e );
		}
	}

	@Override
	public Object getBean( final Class<?> beanNeeded )
	{
		return findBean( beanNeeded, true );
	}

	@Override
	public void addBean( final Object bean )
	{
		final Class<? extends Object> beanClass = bean.getClass();
		LOG.info( "Bean added: " + beanClass.getName() );
		beanMap.put( beanClass, bean );
	}

	private void loadBeanData( final Set<Class<?>> detectedBeans ) throws Exception
	{
		for ( final Class<?> beanClass : detectedBeans )
		{
			final BeanData beanData = beanDataHandler.getBeanData( beanClass, false );
			beanDataMap.put( beanClass, beanData );
		}
	}

	private void autowireBeans( final Set<Class<?>> detectedBeans ) throws Exception
	{
		int round = 1;

		while ( beanMap.size() < detectedBeans.size() )
		{
			LOG.info( "Adding beans, round: " + round );
			autowireBean();
			round++;
		}
	}

	private void autowireBean() throws Exception
	{
		boolean beansAdded = false;

		final Iterator<Class<?>> iterator = beanDataMap.keySet().iterator();
		while ( iterator.hasNext() )
		{
			final Class<?> class1 = iterator.next();
			final BeanData beanData = beanDataMap.get( class1 );
			final List<Class<?>> missingDependencies = findMissingDependencies( beanData );

			if ( allDependenciesAreMet( missingDependencies ) )
			{
				final Object instance = getInstance( beanData );
				beanMap.put( class1, instance );
				iterator.remove();
				beansAdded = true;
				LOG.info( "Bean added: " + class1.getName() );
			}
			else
			{
				LOG.info( "Bean skipped: " + class1.getName() + ", missing dependencies: "
						+ missingDependencies );
			}
		}

		if ( !beansAdded )
			throw new RuntimeException( "Could not resolve all dependent beans" );
	}

	private Object getInstance( final BeanData beanData2 ) throws Exception
	{
		final Object instance = instantiateConstructor( beanData2 );
		autowireBean( beanData2, instance );

		return instance;
	}

	private void autowireBean( final BeanData beanData2, final Object instance ) throws Exception
	{
		autowireField( beanData2, instance );
		autowireMethod( beanData2, instance );
	}

	private void loadAndAutowireBeans() throws Exception
	{
		final Set<Class<?>> detectedBeans = beanDataHandler.findBeans();
		final long start = System.currentTimeMillis();

		loadBeanData( detectedBeans );
		autowireBeans( detectedBeans );

		final long stop = System.currentTimeMillis();

		LOG.info( "All beans added in: " + ( stop - start ) + " ms" );
	}

	private Object instantiateConstructor( final BeanData beanData ) throws Exception
	{
		final Constructor<?> constructor = beanData.getConstructor();
		final Class<?>[] parameterTypes = constructor.getParameterTypes();
		final Object[] beansForConstructor = new Object[parameterTypes.length];

		for ( int i = 0; i < parameterTypes.length; i++ )
		{
			final Class<?> class1 = parameterTypes[i];
			final Object findBean = findBean( class1, true );
			beansForConstructor[i] = findBean;
		}

		LOG.info( "Invoking constructor: " + constructor );

		final Object newInstance = constructor.newInstance( beansForConstructor );

		return newInstance;
	}

	private void autowireField( final BeanData beanData, final Object objectToAutowire ) throws Exception
	{
		final List<Field> fields = beanData.getFields();

		for ( final Field field : fields )
		{
			LOG.info( "Autowiring field: " + field );
			final boolean originalAccessible = field.isAccessible();
			field.setAccessible( true );
			final Object bean = findBean( field.getType(), true );
			field.set( objectToAutowire, bean );
			field.setAccessible( originalAccessible );
		}
	}

	private void autowireMethod( final BeanData beanData, final Object objectToAutowire ) throws Exception
	{
		final List<Method> methods = beanData.getMethods();

		for ( final Method method : methods )
		{
			LOG.info( "Autowiring method: " + method );

			final Class<?>[] parameterTypes = method.getParameterTypes();
			final Object[] beansForConstructor = new Object[parameterTypes.length];

			for ( int i = 0; i < parameterTypes.length; i++ )
			{
				final Class<?> class1 = parameterTypes[i];
				final Object findBean = findBean( class1, true );
				beansForConstructor[i] = findBean;
			}

			method.invoke( objectToAutowire, beansForConstructor );
		}
	}

	private Object findBean( final Class<?> beanNeeded, final boolean throwEx )
	{
		final Iterator<Class<?>> beanIterator = beanMap.keySet().iterator();
		final List<Object> matches = new ArrayList<Object>();

		while ( beanIterator.hasNext() )
		{
			final Class<?> beanClass = beanIterator.next();

			if ( beanNeeded.isAssignableFrom( beanClass ) )
			{
				matches.add( beanMap.get( beanClass ) );
			}
		}

		if ( matches.size() == 0 )
		{
			if ( throwEx )
				throw new IllegalArgumentException( "No matching bean found for autowiring "
						+ beanNeeded );
			else
				return null;
		}

		else if ( matches.size() > 1 )
		{
			throw new RuntimeException( "Wrong number of beans found for autowiring " + beanNeeded
					+ " " + matches );
		}

		return matches.get( 0 );
	}

	private boolean allDependenciesAreMet( final List<Class<?>> missingDependencies )
	{
		return missingDependencies.size() == 0;
	}

	private List<Class<?>> findMissingDependencies( final BeanData beanData )
	{
		final List<Class<?>> missingDeps = new ArrayList<Class<?>>();

		for ( final Class<?> class1 : beanData.getDependencies() )
		{
			final Object dependency = findBean( class1, false );

			if ( dependency == null )
			{
				missingDeps.add( class1 );
			}
		}

		return missingDeps;
	}
}
