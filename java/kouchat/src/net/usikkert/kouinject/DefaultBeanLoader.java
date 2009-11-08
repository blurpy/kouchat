
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Default implementation of the {@link BeanLoader}.
 *
 * TODO:
 * Add scope support
 * Add child contexts?
 * Run initializer method
 * Run destroyer method
 * Allow circular deps in fields and methods?
 *
 * @author Christian Ihle
 */
public class DefaultBeanLoader implements BeanLoader
{
	private static final Logger LOG = Logger.getLogger( DefaultBeanLoader.class.getName() );

	private final Map<Class<?>, Object> beanMap;

	private final Collection<Class<?>> beansInCreation;

	private final BeanDataHandler beanDataHandler;

	public DefaultBeanLoader( final BeanDataHandler beanDataHandler )
	{
		this.beanDataHandler = beanDataHandler;
		this.beanMap = Collections.synchronizedMap( new HashMap<Class<?>, Object>() );
		this.beansInCreation = Collections.synchronizedCollection( new ArrayList<Class<?>>() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadBeans()
	{
		try
		{
			loadAndAutowireBeans();
		}

		catch ( final IllegalAccessException e )
		{
			throw new RuntimeException( e );
		}

		catch ( final InvocationTargetException e )
		{
			throw new RuntimeException( e );
		}

		catch ( final InstantiationException e )
		{
			throw new RuntimeException( e );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void autowire( final Object objectToAutowire )
	{
		final BeanData beanData = beanDataHandler.getBeanData( objectToAutowire.getClass(), true );
		final List<Class<?>> missingDependencies = findMissingDependencies( beanData );

		if ( allDependenciesAreMet( missingDependencies ) )
		{
			try
			{
				autowireBean( beanData, objectToAutowire );
			}

			catch ( final IllegalAccessException e )
			{
				throw new RuntimeException( e );
			}

			catch ( final InvocationTargetException e )
			{
				throw new RuntimeException( e );
			}
		}

		else
		{
			throw new IllegalArgumentException( "Could not autowire object, missing dependencies: " + missingDependencies );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Object> T getBean( final Class<T> beanClass )
	{
		return findBean( beanClass, true );
	}

	protected void addBean( final Object beanToAdd )
	{
		final Class<?> beanClass = beanToAdd.getClass();

		if ( beanAlreadyExists( beanClass ) )
		{
			throw new IllegalArgumentException( "Cannot add already existing bean: " + beanClass );
		}

		synchronized ( beanMap )
		{
			beanMap.put( beanClass, beanToAdd );
		}

		LOG.info( "Bean added: " + beanClass.getName() );
	}

	private void loadAndAutowireBeans() throws IllegalAccessException, InvocationTargetException, InstantiationException
	{
		final Set<Class<?>> detectedBeans = beanDataHandler.findBeans();
		LOG.info( "Beans found: " + detectedBeans.size() );

		final long start = System.currentTimeMillis();

		final Map<Class<?>, BeanData> beanDataMap = getBeanDataMap( detectedBeans );
		createBeans( beanDataMap );

		final long stop = System.currentTimeMillis();

		LOG.info( "All beans created in: " + ( stop - start ) + " ms" );
	}

	private Map<Class<?>, BeanData> getBeanDataMap( final Set<Class<?>> detectedBeans )
	{
		final Map<Class<?>, BeanData> beanDataMap = new HashMap<Class<?>, BeanData>();

		for ( final Class<?> beanClass : detectedBeans )
		{
			final BeanData beanData = beanDataHandler.getBeanData( beanClass, false );
			beanDataMap.put( beanClass, beanData );
		}

		return beanDataMap;
	}

	private void createBeans( final Map<Class<?>, BeanData> beanDataMap ) throws IllegalAccessException, InvocationTargetException, InstantiationException
	{
		final Iterator<Class<?>> beanIterator = beanDataMap.keySet().iterator();

		while ( beanIterator.hasNext() )
		{
			final Class<?> beanClass = beanIterator.next();
			createBean( beanClass, beanDataMap );
		}
	}

	private void createBean( final Class<?> beanClass, final Map<Class<?>, BeanData> beanDataMap ) throws IllegalAccessException, InvocationTargetException, InstantiationException
	{
		LOG.info( "Checking bean before creation: " + beanClass );

		if ( beanAlreadyExists( beanClass ) )
		{
			LOG.info( "Bean already added - skipping: " + beanClass );
			return;
		}

		abortIfBeanCurrentlyInCreation( beanClass );
		addBeanInCreation( beanClass );

		final BeanData beanData = findBeanData( beanClass, beanDataMap );
		final List<Class<?>> missingDependencies = findMissingDependencies( beanData );

		for ( final Class<?> dependency : missingDependencies )
		{
			LOG.info( "Checking bean " + beanClass + " for missing dependency: " + dependency );
			createBean( dependency, beanDataMap );
		}

		final Object instance = instantiateBean( beanData );
		addBean( instance );

		removeBeanInCreation( beanClass );
	}

	private void removeBeanInCreation( final Class<?> beanClass )
	{
		synchronized ( beansInCreation )
		{
			beansInCreation.remove( beanClass );
		}
	}

	private void addBeanInCreation( final Class<?> beanClass )
	{
		synchronized ( beansInCreation )
		{
			beansInCreation.add( beanClass );
		}
	}

	private void abortIfBeanCurrentlyInCreation( final Class<?> beanClass )
	{
		synchronized ( beansInCreation )
		{
			final boolean beanCurrentlyInCreation = beansInCreation.contains( beanClass );

			if ( beanCurrentlyInCreation )
			{
				throw new IllegalStateException( "Circular dependency - bean already in creation: " + beanClass );
			}
		}
	}

	private boolean beanAlreadyExists( final Class<?> beanClass )
	{
		final Object existingBean = findBean( beanClass, false );
		return existingBean != null;
	}

	private BeanData findBeanData( final Class<?> beanNeeded, final Map<Class<?>, BeanData> beanDataMap )
	{
		final Iterator<Class<?>> beanIterator = beanDataMap.keySet().iterator();
		final Class<?> matchingBean = getMatchingBean( beanNeeded, beanIterator, true );

		return beanDataMap.get( matchingBean );
	}

	private Object instantiateBean( final BeanData beanData ) throws IllegalAccessException, InvocationTargetException, InstantiationException
	{
		final Object instance = instantiateConstructor( beanData );
		autowireBean( beanData, instance );

		return instance;
	}

	private Object instantiateConstructor( final BeanData beanData ) throws InstantiationException, IllegalAccessException, InvocationTargetException
	{
		final Constructor<?> constructor = beanData.getConstructor();
		final Class<?>[] parameterTypes = constructor.getParameterTypes();
		final Object[] beansForConstructor = new Object[parameterTypes.length];

		for ( int i = 0; i < parameterTypes.length; i++ )
		{
			final Class<?> beanClass = parameterTypes[i];
			final Object bean = findBean( beanClass, true );
			beansForConstructor[i] = bean;
		}

		LOG.info( "Invoking constructor: " + constructor );
		final Object newInstance = constructor.newInstance( beansForConstructor );

		return newInstance;
	}

	private void autowireBean( final BeanData beanData, final Object instance ) throws IllegalAccessException, InvocationTargetException
	{
		autowireField( beanData, instance );
		autowireMethod( beanData, instance );
	}

	private void autowireField( final BeanData beanData, final Object objectToAutowire ) throws IllegalAccessException
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

	private void autowireMethod( final BeanData beanData, final Object objectToAutowire ) throws IllegalAccessException, InvocationTargetException
	{
		final List<Method> methods = beanData.getMethods();

		for ( final Method method : methods )
		{
			LOG.info( "Autowiring method: " + method );

			final Class<?>[] parameterTypes = method.getParameterTypes();
			final Object[] beansForMethod = new Object[parameterTypes.length];

			for ( int i = 0; i < parameterTypes.length; i++ )
			{
				final Class<?> beanClass = parameterTypes[i];
				final Object bean = findBean( beanClass, true );
				beansForMethod[i] = bean;
			}

			method.invoke( objectToAutowire, beansForMethod );
		}
	}

	private <T extends Object> T findBean( final Class<T> beanNeeded, final boolean throwEx )
	{
		synchronized ( beanMap )
		{
			final Iterator<Class<?>> beanIterator = beanMap.keySet().iterator();
			final Class<?> matchingBean = getMatchingBean( beanNeeded, beanIterator, throwEx );
			return (T) beanMap.get( matchingBean );
		}
	}

	private Class<?> getMatchingBean( final Class<?> beanNeeded, final Iterator<Class<?>> beanIterator, final boolean throwEx )
	{
		final List< Class<?>> matches = new ArrayList< Class<?>>();

		while ( beanIterator.hasNext() )
		{
			final Class<?> beanClass = beanIterator.next();

			if ( beanNeeded.isAssignableFrom( beanClass ) )
			{
				matches.add( beanClass );
			}
		}

		if ( matches.size() == 0 )
		{
			if ( throwEx )
				throw new IllegalArgumentException( "No matching bean found for " + beanNeeded );
			else
				return null;
		}

		else if ( matches.size() > 1 )
		{
			throw new IllegalStateException( "Too many matching beans found for " + beanNeeded + " " + matches );
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

		for ( final Class<?> beanClass : beanData.getDependencies() )
		{
			final Object dependency = findBean( beanClass, false );

			if ( dependency == null )
			{
				missingDeps.add( beanClass );
			}
		}

		return missingDeps;
	}
}
