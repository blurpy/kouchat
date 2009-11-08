
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.usikkert.kouinject.annotation.Component;
import net.usikkert.kouinject.annotation.Inject;

/**
 * This bean-data handler uses annotations to find beans and extract their meta-data.
 *
 * @author Christian Ihle
 */
public class AnnotationBasedBeanDataHandler implements BeanDataHandler
{
	private static final Class<Inject> INJECTION_ANNOTATION = Inject.class;
	private static final Class<Component> COMPONENT_ANNOTATION = Component.class;

	private final ClassLocator classLocator;

	private final String basePackage;

	public AnnotationBasedBeanDataHandler( final String basePackage, final ClassLocator classLocator )
	{
		this.basePackage = basePackage;
		this.classLocator = classLocator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Class<?>> findBeans()
	{
		final Set<Class<?>> allClasses = classLocator.findClasses( basePackage );
		final Set<Class<?>> detectedBeans = new HashSet<Class<?>>();

		for ( final Class<?> clazz : allClasses )
		{
			if ( classIsBean( clazz ) )
			{
				detectedBeans.add( clazz );
			}
		}

		return detectedBeans;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BeanData getBeanData( final Class<?> beanClass, final boolean skipConstructor )
	{
		final BeanData beanData = new BeanData( beanClass );

		if ( !skipConstructor )
		{
			final Constructor<?> constructor = findConstructor( beanClass );
			beanData.setConstructor( constructor );
		}

		final List<Field> fields = findFields( beanClass );
		beanData.setFields( fields );

		final List<Method> methods = findMethods( beanClass );
		beanData.setMethods( methods );

		beanData.mapDependencies();

		return beanData;
	}

	private boolean classIsBean( final Class<?> clazz )
	{
		return clazz.isAnnotationPresent( COMPONENT_ANNOTATION );
	}

	private List<Field> findFields( final Class<?> beanClass )
	{
		final Field[] declaredFields = beanClass.getDeclaredFields();
		final List<Field> fields = new ArrayList<Field>();

		for ( final Field field : declaredFields )
		{
			if ( fieldNeedsInjection( field ) )
			{
				fields.add( field );
			}
		}

		return fields;
	}

	private boolean fieldNeedsInjection(  final Field field )
	{
		return field.isAnnotationPresent( INJECTION_ANNOTATION );
	}

	private List<Method> findMethods( final Class<?> beanClass )
	{
		final Method[] declaredMethods = beanClass.getDeclaredMethods();
		final List<Method> methods = new ArrayList<Method>();

		for ( final Method method : declaredMethods )
		{
			if ( methodNeedsInjection( method ) )
			{
				methods.add( method );
			}
		}

		return methods;
	}

	private boolean methodNeedsInjection(  final Method method )
	{
		return method.isAnnotationPresent( INJECTION_ANNOTATION );
	}

	private Constructor<?> findConstructor( final Class<?> beanClass )
	{
		final Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
		final List<Constructor<?>> matches = new ArrayList<Constructor<?>>();

		for ( final Constructor<?> constructor : declaredConstructors )
		{
			if ( constructorNeedsInjection( constructor ) )
			{
				matches.add( constructor );
			}
		}

		if ( matches.size() == 0 )
		{
			try
			{
				return beanClass.getDeclaredConstructor();
			}

			catch ( final SecurityException e )
			{
				throw new RuntimeException( e );
			}

			catch ( final NoSuchMethodException e )
			{
				throw new RuntimeException( e );
			}
		}

		else if ( matches.size() > 1 )
		{
			throw new UnsupportedOperationException( "Wrong number of constructors found for autowiring " + beanClass + " " + matches );
		}

		return matches.get( 0 );
	}

	private boolean constructorNeedsInjection(  final Constructor<?> constructor )
	{
		return constructor.isAnnotationPresent( INJECTION_ANNOTATION );
	}
}
