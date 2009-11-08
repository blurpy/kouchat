
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
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import net.usikkert.kouinject.testbeans.notscanned.ACloserMatchOfImplementationUser;
import net.usikkert.kouinject.testbeans.notscanned.FirstCircularBean;
import net.usikkert.kouinject.testbeans.notscanned.FirstInterfaceImpl;
import net.usikkert.kouinject.testbeans.notscanned.SecondCircularBean;
import net.usikkert.kouinject.testbeans.notscanned.SecondInterfaceImpl;
import net.usikkert.kouinject.testbeans.notscanned.TheInterface;
import net.usikkert.kouinject.testbeans.notscanned.TheInterfaceUser;
import net.usikkert.kouinject.testbeans.scanned.ConstructorBean;
import net.usikkert.kouinject.testbeans.scanned.EverythingBean;
import net.usikkert.kouinject.testbeans.scanned.FieldBean;
import net.usikkert.kouinject.testbeans.scanned.HelloBean;
import net.usikkert.kouinject.testbeans.scanned.LastBean;
import net.usikkert.kouinject.testbeans.scanned.SetterBean;
import net.usikkert.kouinject.testbeans.scanned.coffee.CoffeeBean;
import net.usikkert.kouinject.testbeans.scanned.coffee.JavaBean;
import net.usikkert.kouinject.testbeans.scanned.hierarchy.abstractbean.AbstractBean;
import net.usikkert.kouinject.testbeans.scanned.hierarchy.abstractbean.AbstractBeanImpl;
import net.usikkert.kouinject.testbeans.scanned.hierarchy.interfacebean.InterfaceBean;
import net.usikkert.kouinject.testbeans.scanned.hierarchy.interfacebean.InterfaceBeanImpl;
import net.usikkert.kouinject.testbeans.scanned.notloaded.NoBean;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link DefaultBeanLoader}.
 *
 * @author Christian Ihle
 */
public class DefaultBeanLoaderTest
{
	private DefaultBeanLoader beanLoader;

	@Before
	public void setupBeanLoader()
	{
		final ClassLocator classLocator = new ClassPathScanner();
		final BeanDataHandler beanDataHandler = new AnnotationBasedBeanDataHandler(
				"net.usikkert.kouinject.testbeans.scanned", classLocator );
		beanLoader = new DefaultBeanLoader( beanDataHandler );
	}

	@Test
	public void checkAbstractBean()
	{
		beanLoader.loadBeans();

		final AbstractBean abstractBean = beanLoader.getBean( AbstractBean.class );
		assertNotNull( abstractBean );

		final AbstractBeanImpl abstractBeanImpl = beanLoader.getBean( AbstractBeanImpl.class );
		assertNotNull( abstractBeanImpl );
	}

	@Test
	public void checkCoffeeBean()
	{
		beanLoader.loadBeans();

		final CoffeeBean coffeeBean = beanLoader.getBean( CoffeeBean.class );

		assertNotNull( coffeeBean.getHelloBean() );
		assertNotNull( coffeeBean.getJavaBean() );
	}

	@Test
	public void checkConstructorBean()
	{
		beanLoader.loadBeans();

		final ConstructorBean constructorBean = beanLoader.getBean( ConstructorBean.class );

		assertNotNull( constructorBean.getHelloBean() );
		assertNotNull( constructorBean.getSetterBean() );
	}

	@Test
	public void checkEverythingBean()
	{
		beanLoader.loadBeans();

		final EverythingBean everythingBean = beanLoader.getBean( EverythingBean.class );

		assertNotNull( everythingBean.getCoffeeBean() );
		assertNotNull( everythingBean.getConstructorBean() );
		assertNotNull( everythingBean.getFieldBean() );
		assertNotNull( everythingBean.getHelloBean() );
		assertNotNull( everythingBean.getJavaBean() );
		assertNotNull( everythingBean.getSetterBean() );
		assertNotNull( everythingBean.getInterfaceBeanImpl() );
		assertNotNull( everythingBean.getAbstractBeanImpl() );
	}

	@Test
	public void checkFieldBean()
	{
		beanLoader.loadBeans();

		final FieldBean fieldBean = beanLoader.getBean( FieldBean.class );

		assertNotNull( fieldBean.getHelloBean() );
		assertNotNull( fieldBean.getAbstractBean() );
		assertNotNull( fieldBean.getInterfaceBean() );
	}

	@Test
	public void checkHelloBean()
	{
		beanLoader.loadBeans();

		final HelloBean helloBean = beanLoader.getBean( HelloBean.class );
		assertNotNull( helloBean );
	}

	@Test
	public void checkInterfaceBean()
	{
		beanLoader.loadBeans();

		final InterfaceBean interfaceBean = beanLoader.getBean( InterfaceBean.class );
		assertNotNull( interfaceBean );

		final InterfaceBeanImpl interfaceBeanImpl = beanLoader.getBean( InterfaceBeanImpl.class );
		assertNotNull( interfaceBeanImpl );
	}

	@Test
	public void checkJavaBean()
	{
		beanLoader.loadBeans();

		final JavaBean javaBean = beanLoader.getBean( JavaBean.class );

		assertNotNull( javaBean.getFieldBean() );
		assertNotNull( javaBean.getHelloBean() );
	}

	@Test
	public void checkLastBean()
	{
		beanLoader.loadBeans();

		final LastBean lastBean = beanLoader.getBean( LastBean.class );

		assertNotNull( lastBean.getEverythingBean() );
	}

	@Test( expected = IllegalArgumentException.class )
	public void checkNoBean()
	{
		beanLoader.loadBeans();

		beanLoader.getBean( NoBean.class );
	}

	@Test
	public void checkSetterBean()
	{
		beanLoader.loadBeans();

		final SetterBean setterBean = beanLoader.getBean( SetterBean.class );

		assertNotNull( setterBean.getFieldBean() );
	}

	@Test
	public void addBeanShouldMakeBeanAvailableButNotAutowire()
	{
		beanLoader.loadBeans();

		final NoBean noBean = new NoBean();
		beanLoader.addBean( noBean );

		final NoBean noBeanFromBeanLoader = beanLoader.getBean( NoBean.class );
		assertNotNull( noBeanFromBeanLoader );
		assertNull( noBeanFromBeanLoader.getHelloBean() );
		assertNull( noBeanFromBeanLoader.getCoffeeBean() );
	}

	@Test
	public void autowireShouldInjectFieldsInBean()
	{
		beanLoader.loadBeans();

		final NoBean noBean = new NoBean();
		beanLoader.autowire( noBean );

		assertNotNull( noBean.getHelloBean() );
		assertNotNull( noBean.getCoffeeBean() );
	}

	@Test
	public void beanLoaderShouldHandleMocks()
	{
		final HelloBean helloBean = mock( HelloBean.class );
		beanLoader.addBean( helloBean );

		final AbstractBeanImpl abstractBean = mock( AbstractBeanImpl.class );
		beanLoader.addBean( abstractBean );

		final InterfaceBean interfaceBean = mock( InterfaceBean.class );
		beanLoader.addBean( interfaceBean );

		final FieldBean fieldBean = new FieldBean();
		beanLoader.autowire( fieldBean );

		assertSame( helloBean, fieldBean.getHelloBean() );
		assertSame( abstractBean, fieldBean.getAbstractBean() );
		assertSame( interfaceBean, fieldBean.getInterfaceBean() );
	}

	@Test( expected = IllegalStateException.class )
	public void circularDependenciesShouldBeDetected()
	{
		final ClassLocator classLocator = mock( ClassLocator.class );
		final BeanDataHandler beanDataHandler = new AnnotationBasedBeanDataHandler( "some.package", classLocator );
		final DefaultBeanLoader loader = new DefaultBeanLoader( beanDataHandler );

		final Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add( FirstCircularBean.class );
		classes.add( SecondCircularBean.class );

		when( classLocator.findClasses( "some.package" ) ).thenReturn( classes );

		loader.loadBeans();
	}

	@Test( expected = IllegalStateException.class )
	public void tooManyMatchesForADependencyShouldBeDetected()
	{
		final ClassLocator classLocator = mock( ClassLocator.class );
		final BeanDataHandler beanDataHandler = new AnnotationBasedBeanDataHandler( "some.package", classLocator );
		final DefaultBeanLoader loader = new DefaultBeanLoader( beanDataHandler );

		final Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add( FirstInterfaceImpl.class );
		classes.add( SecondInterfaceImpl.class );

		when( classLocator.findClasses( "some.package" ) ).thenReturn( classes );

		loader.loadBeans();

		final TheInterfaceUser theInterfaceUser = new TheInterfaceUser();
		loader.autowire( theInterfaceUser );
	}

	@Test
	public void severalBeansForAnInterfaceIsOKIfACloserMatchToImplIsRequested()
	{
		final ClassLocator classLocator = mock( ClassLocator.class );
		final BeanDataHandler beanDataHandler = new AnnotationBasedBeanDataHandler( "some.package", classLocator );
		final DefaultBeanLoader loader = new DefaultBeanLoader( beanDataHandler );

		final Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add( FirstInterfaceImpl.class );
		classes.add( SecondInterfaceImpl.class );

		when( classLocator.findClasses( "some.package" ) ).thenReturn( classes );

		loader.loadBeans();

		final ACloserMatchOfImplementationUser aCloserMatch = new ACloserMatchOfImplementationUser();
		loader.autowire( aCloserMatch );

		assertTrue( aCloserMatch.getFirstInterfaceImplInterface() instanceof TheInterface );
		assertTrue( aCloserMatch.getSecondInterfaceImpl() instanceof TheInterface );
	}

	@Test( expected = IllegalArgumentException.class )
	public void noMatchesForADependencyShouldBeDetected()
	{
		final ClassLocator classLocator = mock( ClassLocator.class );
		final BeanDataHandler beanDataHandler = new AnnotationBasedBeanDataHandler( "some.package", classLocator );
		final DefaultBeanLoader loader = new DefaultBeanLoader( beanDataHandler );

		final Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add( TheInterfaceUser.class );

		when( classLocator.findClasses( "some.package" ) ).thenReturn( classes );

		loader.loadBeans();
	}
}
