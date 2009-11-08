
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

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import net.usikkert.kouinject.annotation.Component;
import net.usikkert.kouinject.annotation.Inject;
import net.usikkert.kouinject.testbeans.scanned.ConstructorBean;
import net.usikkert.kouinject.testbeans.scanned.EverythingBean;
import net.usikkert.kouinject.testbeans.scanned.FieldBean;
import net.usikkert.kouinject.testbeans.scanned.HelloBean;
import net.usikkert.kouinject.testbeans.scanned.SetterBean;
import net.usikkert.kouinject.testbeans.scanned.coffee.JavaBean;
import net.usikkert.kouinject.testbeans.scanned.hierarchy.abstractbean.AbstractBean;
import net.usikkert.kouinject.testbeans.scanned.hierarchy.interfacebean.InterfaceBean;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link AnnotationBasedBeanDataHandler}.
 *
 * @author Christian Ihle
 */
public class AnnotationBasedBeanDataHandlerTest
{
	private AnnotationBasedBeanDataHandler handler;

	@Before
	public void createHandler()
	{
		final ClassLocator classLocator = new ClassPathScanner();
		handler = new AnnotationBasedBeanDataHandler( "net.usikkert.kouinject", classLocator );
	}

	@Test
	public void findBeansShouldOnlyReturnComponents()
	{
		final Set<Class<?>> beans = handler.findBeans();

		assertTrue( beans.size() >= 10 );

		for ( final Class<?> bean : beans )
		{
			assertTrue( bean.isAnnotationPresent( Component.class ) );
		}
	}

	@Test
	public void getBeanDataShouldDetectFieldsAndDependenciesForInjection()
	{
		final BeanData beanData = handler.getBeanData( FieldBean.class, false );

		assertEquals( FieldBean.class, beanData.getBeanClass() );

		final List<Class<?>> dependencies = beanData.getDependencies();
		assertEquals( 3, dependencies.size() );

		assertTrue( containsDependency( dependencies, HelloBean.class ) );
		assertTrue( containsDependency( dependencies, AbstractBean.class ) );
		assertTrue( containsDependency( dependencies, InterfaceBean.class ) );

		final List<Field> fields = beanData.getFields();
		assertEquals( 3, fields.size() );

		for ( final Field field : fields )
		{
			assertTrue( field.isAnnotationPresent( Inject.class ) );
		}

		assertTrue( containsField( fields, HelloBean.class ) );
		assertTrue( containsField( fields, AbstractBean.class ) );
		assertTrue( containsField( fields, InterfaceBean.class ) );
	}

	@Test
	public void getBeanDataShouldDetectMethodsAndDependenciesForInjection()
	{
		final BeanData beanData = handler.getBeanData( JavaBean.class, false );

		assertEquals( JavaBean.class, beanData.getBeanClass() );

		final List<Class<?>> dependencies = beanData.getDependencies();
		assertEquals( 2, dependencies.size() );

		assertTrue( containsDependency( dependencies, HelloBean.class ) );
		assertTrue( containsDependency( dependencies, FieldBean.class ) );

		final List<Method> methods = beanData.getMethods();
		assertEquals( 1, methods.size() );

		for ( final Method method : methods )
		{
			assertTrue( method.isAnnotationPresent( Inject.class ) );
			assertTrue( containsMethodParameter( method, HelloBean.class ) );
			assertTrue( containsMethodParameter( method, FieldBean.class ) );
		}
	}

	@Test
	public void getBeanDataShouldDetectCorrectConstuctorAndDependenciesForInjection()
	{
		final BeanData beanData = handler.getBeanData( ConstructorBean.class, false );

		assertEquals( ConstructorBean.class, beanData.getBeanClass() );

		final List<Class<?>> dependencies = beanData.getDependencies();
		assertEquals( 2, dependencies.size() );

		assertTrue( containsDependency( dependencies, HelloBean.class ) );
		assertTrue( containsDependency( dependencies, SetterBean.class ) );

		final Constructor<?> constructor = beanData.getConstructor();
		assertTrue( constructor.isAnnotationPresent( Inject.class ) );

		assertTrue( containsConstructorParameter( constructor, HelloBean.class ) );
		assertTrue( containsConstructorParameter( constructor, SetterBean.class ) );
	}

	@Test
	public void getBeanDataShouldDetectConstructorAndFieldsAndMethodsAtTheSameTime()
	{
		final BeanData beanData = handler.getBeanData( EverythingBean.class, false );

		assertEquals( EverythingBean.class, beanData.getBeanClass() );

		final List<Class<?>> dependencies = beanData.getDependencies();
		assertEquals( 8, dependencies.size() );

		final Constructor<?> constructor = beanData.getConstructor();
		assertTrue( constructor.isAnnotationPresent( Inject.class ) );
		assertEquals( 5, constructor.getParameterTypes().length );

		final List<Field> fields = beanData.getFields();
		assertEquals( 1, fields.size() );

		for ( final Field field : fields )
		{
			assertTrue( field.isAnnotationPresent( Inject.class ) );
		}

		final List<Method> methods = beanData.getMethods();
		assertEquals( 2, methods.size() );

		for ( final Method method : methods )
		{
			assertTrue( method.isAnnotationPresent( Inject.class ) );
		}
	}

	@Test
	public void getBeanDataShouldSupportIgnoringConstructor()
	{
		final BeanData beanData = handler.getBeanData( ConstructorBean.class, true );

		assertEquals( ConstructorBean.class, beanData.getBeanClass() );

		final List<Class<?>> dependencies = beanData.getDependencies();
		assertEquals( 0, dependencies.size() );

		final Constructor<?> constructor = beanData.getConstructor();
		assertNull( constructor );
	}

	@Test
	public void getBeanDataShouldHandleClassesWithoutAnnotations()
	{
		final BeanData beanData = handler.getBeanData( ClassPathScanner.class, false );

		assertEquals( ClassPathScanner.class, beanData.getBeanClass() );

		final List<Class<?>> dependencies = beanData.getDependencies();
		assertEquals( 0, dependencies.size() );

		final Constructor<?> constructor = beanData.getConstructor();
		assertNotNull( constructor );

		final List<Field> fields = beanData.getFields();
		assertEquals( 0, fields.size() );

		final List<Method> methods = beanData.getMethods();
		assertEquals( 0, methods.size() );
	}

	private boolean containsConstructorParameter( final Constructor<?> constructor, final Class<?> beanClass )
	{
		for ( final Class<?> parameter : constructor.getParameterTypes() )
		{
			if ( parameter.equals( beanClass ) )
				return true;
		}

		return false;
	}

	private boolean containsMethodParameter( final Method method, final Class<?> beanClass )
	{
		for ( final Class<?> parameter : method.getParameterTypes() )
		{
			if ( parameter.equals( beanClass ) )
				return true;
		}

		return false;
	}

	private boolean containsDependency( final List<Class<?>> dependencies, final Class<?> beanClass )
	{
		for ( final Class<?> dependency : dependencies )
		{
			if ( dependency.equals( beanClass ) )
				return true;
		}

		return false;
	}

	private boolean containsField( final List<Field> fields, final Class<?> beanClass )
	{
		for ( final Field field : fields )
		{
			if ( field.getType().equals( beanClass ) )
				return true;
		}

		return false;
	}
}
