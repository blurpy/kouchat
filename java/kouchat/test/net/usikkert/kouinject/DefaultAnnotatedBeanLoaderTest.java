
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

import net.usikkert.kouinject.testbeans.AbstractBean;
import net.usikkert.kouinject.testbeans.AbstractBeanImpl;
import net.usikkert.kouinject.testbeans.CoffeeBean;
import net.usikkert.kouinject.testbeans.ConstructorBean;
import net.usikkert.kouinject.testbeans.EverythingBean;
import net.usikkert.kouinject.testbeans.FieldBean;
import net.usikkert.kouinject.testbeans.HelloBean;
import net.usikkert.kouinject.testbeans.InterfaceBean;
import net.usikkert.kouinject.testbeans.InterfaceBeanImpl;
import net.usikkert.kouinject.testbeans.JavaBean;
import net.usikkert.kouinject.testbeans.LastBean;
import net.usikkert.kouinject.testbeans.NoBean;
import net.usikkert.kouinject.testbeans.SetterBean;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link DefaultAnnotatedBeanLoader}.
 *
 * @author Christian Ihle
 */
public class DefaultAnnotatedBeanLoaderTest
{
	private DefaultAnnotatedBeanLoader beanLoader;

	@Before
	public void loadBeans()
	{
		final ClassLocator classLocator = new ClassPathScanner();
		final BeanDataHandler beanDataHandler = new AnnotationBasedBeanDataHandler();
		beanLoader = new DefaultAnnotatedBeanLoader( "net.usikkert.kouinject", beanDataHandler, classLocator );
		beanLoader.loadBeans();
	}

	@Test
	public void checkAbstractBean()
	{
		final AbstractBean abstractBean = (AbstractBean) beanLoader.getBean( AbstractBean.class );
		assertNotNull( abstractBean );

		final AbstractBeanImpl abstractBeanImpl = (AbstractBeanImpl) beanLoader.getBean( AbstractBeanImpl.class );
		assertNotNull( abstractBeanImpl );
	}

	@Test
	public void checkCoffeeBean()
	{
		final CoffeeBean coffeeBean = (CoffeeBean) beanLoader.getBean( CoffeeBean.class );

		assertNotNull( coffeeBean.getHelloBean() );
		assertNotNull( coffeeBean.getJavaBean() );
	}

	@Test
	public void checkConstructorBean()
	{
		final ConstructorBean constructorBean = (ConstructorBean) beanLoader.getBean( ConstructorBean.class );

		assertNotNull( constructorBean.getHelloBean() );
		assertNotNull( constructorBean.getSetterBean() );
	}

	@Test
	public void checkEverythingBean()
	{
		final EverythingBean everythingBean = (EverythingBean) beanLoader.getBean( EverythingBean.class );

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
		final FieldBean fieldBean = (FieldBean) beanLoader.getBean( FieldBean.class );

		assertNotNull( fieldBean.getHelloBean() );
		assertNotNull( fieldBean.getAbstractBean() );
		assertNotNull( fieldBean.getInterfaceBean() );
	}

	@Test
	public void checkHelloBean()
	{
		final HelloBean helloBean = (HelloBean) beanLoader.getBean( HelloBean.class );
		assertNotNull( helloBean );
	}

	@Test
	public void checkInterfaceBean()
	{
		final InterfaceBean interfaceBean = (InterfaceBean) beanLoader.getBean( InterfaceBean.class );
		assertNotNull( interfaceBean );

		final InterfaceBeanImpl interfaceBeanImpl = (InterfaceBeanImpl) beanLoader.getBean( InterfaceBeanImpl.class );
		assertNotNull( interfaceBeanImpl );
	}

	@Test
	public void checkJavaBean()
	{
		final JavaBean javaBean = (JavaBean) beanLoader.getBean( JavaBean.class );

		assertNotNull( javaBean.getFieldBean() );
		assertNotNull( javaBean.getHelloBean() );
	}

	@Test
	public void checkLastBean()
	{
		final LastBean lastBean = (LastBean) beanLoader.getBean( LastBean.class );

		assertNotNull( lastBean.getEverythingBean() );
	}

	@Test( expected = IllegalArgumentException.class )
	public void checkNoBean()
	{
		beanLoader.getBean( NoBean.class );
	}

	@Test
	public void checkSetterBean()
	{
		final SetterBean setterBean = (SetterBean) beanLoader.getBean( SetterBean.class );

		assertNotNull( setterBean.getFieldBean() );
	}

	@Test
	public void addBeanShouldMakeBeanAvailableButNotAutowire()
	{
		final NoBean noBean = new NoBean();
		beanLoader.addBean( noBean );

		final NoBean noBeanFromBeanLoader = (NoBean) beanLoader.getBean( NoBean.class );
		assertNotNull( noBeanFromBeanLoader );
		assertNull( noBeanFromBeanLoader.getHelloBean() );
		assertNull( noBeanFromBeanLoader.getCoffeeBean() );
	}

	@Test
	public void autowireShouldInjectFieldsInBean()
	{
		final NoBean noBean = new NoBean();
		beanLoader.autowire( noBean );

		assertNotNull( noBean.getHelloBean() );
		assertNotNull( noBean.getCoffeeBean() );
	}

	@Test
	public void beanLoaderShouldHandleMocks()
	{
		final ClassLocator classLocator = new ClassPathScanner();
		final BeanDataHandler beanDataHandler = new AnnotationBasedBeanDataHandler();
		final DefaultAnnotatedBeanLoader loader = new DefaultAnnotatedBeanLoader( beanDataHandler, classLocator );

		final HelloBean helloBean = mock( HelloBean.class );
		loader.addBean( helloBean );

		final AbstractBeanImpl abstractBean = mock( AbstractBeanImpl.class );
		loader.addBean( abstractBean );

		final InterfaceBean interfaceBean = mock( InterfaceBean.class );
		loader.addBean( interfaceBean );

		final FieldBean fieldBean = new FieldBean();
		loader.autowire( fieldBean );

		assertSame( helloBean, fieldBean.getHelloBean() );
		assertSame( abstractBean, fieldBean.getAbstractBean() );
		assertSame( interfaceBean, fieldBean.getInterfaceBean() );
	}
}
