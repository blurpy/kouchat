
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import net.usikkert.kouinject.annotation.Bean;
import net.usikkert.kouinject.annotation.Inject;

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
public class DefaultAnnotatedBeanLoader implements BeanLoader
{
	private static final Logger LOG = Logger.getLogger( DefaultAnnotatedBeanLoader.class.getName() );

	private static final String BASE_PACKAGE = "net.usikkert.kouchat";

	protected final Map<Class<?>, Object> beans = new HashMap<Class<?>, Object>();
	protected final Map<Class<?>, BeanData> beandata = new HashMap<Class<?>, BeanData>();

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
			final BeanData beanData2 = findBeanData( objectToAutowire.getClass(), true );

			if (allDependenciesAreMet(beanData2)) {
				autowireBean(beanData2, objectToAutowire);
			} else {
				throw new RuntimeException("Could not autowire object, missing dependencies");
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
		beans.put( beanClass, bean );
	}

	private BeanData findBeanData( final Class<?> beanClass, final boolean skipConstructor ) throws Exception {
		final BeanData beanData = new BeanData(beanClass);

		if (!skipConstructor) {
			final Constructor<?> constructor = findConstructor(beanClass);
			beanData.setConstructor(constructor);
		}

		final List<Field> fields = findFields(beanClass);
		beanData.setFields(fields);

		final List<Method> methods = findMethods(beanClass);
		beanData.setMethods(methods);

		beanData.mapDependencies();

		return beanData;
	}

	private void loadBeanData(final Set<Class<?>> detectedBeans) throws Exception {
		for ( final Class<?> beanClass : detectedBeans )
		{
			final BeanData findBeanData = findBeanData(beanClass, false);
			beandata.put(beanClass, findBeanData);
		}
	}

	private void autowireBeans(final Set<Class<?>> detectedBeans) throws Exception {
		int round = 1;

		while (beans.size() < detectedBeans.size()) {
			LOG.info( "Adding beans, round: " + round);
			autowireBean();
			round++;
		}
	}

	private void autowireBean() throws Exception {
		boolean beansAdded = false;

		final Iterator<Class<?>> iterator = beandata.keySet().iterator();
		while (iterator.hasNext()) {
			final Class<?> class1 = iterator.next();
			final BeanData beanData2 = beandata.get(class1);
			final List<Class<?>> missingDependencies = findMissingDependencies(beanData2);

			if (missingDependencies.size() == 0) {
				final Object instance = getInstance(beanData2);
				beans.put(class1, instance);
				iterator.remove();
				beansAdded = true;
				LOG.info( "Bean added: " + class1.getName() );
			} else {
				LOG.info( "Bean skipped: " + class1.getName() + ", missing dependencies: " + missingDependencies );
			}
		}

		if (!beansAdded)
			throw new RuntimeException("Could not resolve all dependent beans");
	}

	private Object getInstance(final BeanData beanData2) throws Exception {
		final Object instance = instantiateConstructor(beanData2);
		autowireBean(beanData2, instance);

		return instance;
	}

	private void autowireBean(final BeanData beanData2, final Object instance) throws Exception {
		autowireField(beanData2, instance);
		autowireMethod(beanData2, instance);
	}

	private void loadAndAutowireBeans() throws Exception
	{
		final Set<Class<?>> detectedBeans = findBeans( Bean.class );
		final long start = System.currentTimeMillis();

		loadBeanData(detectedBeans);
		autowireBeans(detectedBeans);

		final long stop = System.currentTimeMillis();

		LOG.info( "All beans added in: " + (stop - start) + " ms");
	}

	private Object instantiateConstructor( final BeanData beanData ) throws Exception
	{
		final Constructor<?> constructor = beanData.getConstructor();
		final Class<?>[] parameterTypes = constructor.getParameterTypes();
		final Object[] beansForConstructor = new Object[parameterTypes.length];

		for (int i = 0; i < parameterTypes.length; i++) {
			final Class<?> class1 = parameterTypes[i];
			final Object findBean = findBean(class1, true);
			beansForConstructor[i] = findBean;
		}

		LOG.info("Invoking constructor: " + constructor);

		final Object newInstance = constructor.newInstance(beansForConstructor);

		return newInstance;
	}

	private Constructor<?> findConstructor( final Class<?> beanClass ) throws Exception
	{
		final Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
		final List<Constructor<?>> matches = new ArrayList<Constructor<?>>();

		for (final Constructor<?> constructor2 : declaredConstructors) {
			if (constructor2.isAnnotationPresent(Inject.class)) {
				matches.add(constructor2);
			}
		}

		if ( matches.size() == 0 )
		{
			return beanClass.getDeclaredConstructor();
		}

		else if ( matches.size() > 1 )
		{
			throw new RuntimeException( "Wrong number of constructors found for autowiring " + beanClass + " " + matches );
		}

		return matches.get( 0 );
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

	private List<Field> findFields( final Class<?> beanClass ) {
		final Field[] declaredFields = beanClass.getDeclaredFields();
		final List<Field> fields = new ArrayList<Field>();

		for ( final Field field : declaredFields )
		{
			if ( field.isAnnotationPresent( Inject.class ) )
			{
				fields.add(field);
			}
		}

		return fields;
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

		for (final Method method : methods) {
			LOG.info( "Autowiring method: " + method );

			final Class<?>[] parameterTypes = method.getParameterTypes();
			final Object[] beansForConstructor = new Object[parameterTypes.length];

			for (int i = 0; i < parameterTypes.length; i++) {
				final Class<?> class1 = parameterTypes[i];
				final Object findBean = findBean(class1, true);
				beansForConstructor[i] = findBean;
			}

			method.invoke( objectToAutowire, beansForConstructor );
		}
	}

	private List<Method> findMethods( final Class<?> beanClass ) {
		final Method[] declaredMethods = beanClass.getDeclaredMethods();
		final List<Method> methods = new ArrayList<Method>();

		for (final Method method : declaredMethods) {
			if ( method.isAnnotationPresent( Inject.class ) )
			{
				methods.add(method);
			}
		}

		return methods;
	}

	private Object findBean( final Class<?> beanNeeded, final boolean throwEx )
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
			if (throwEx)
				throw new RuntimeException( "No matching bean found for autowiring " + beanNeeded );
			else
				return null;
		}

		else if ( matches.size() > 1 )
		{
			throw new RuntimeException( "Wrong number of beans found for autowiring " + beanNeeded + " " + matches );
		}

		return matches.get( 0 );
	}

	public boolean allDependenciesAreMet( final BeanData beanData ) {
		for (final Class<?> class1 : beanData.getDependencies()) {
			final Object dependency = findBean(class1, false);

			if (dependency == null) {
				return false;
			}
		}

		return true;
	}

	public List<Class<?>> findMissingDependencies( final BeanData beanData ) {
		final List<Class<?>> missingDeps = new ArrayList<Class<?>>();

		for (final Class<?> class1 : beanData.getDependencies()) {
			final Object dependency = findBean(class1, false);

			if (dependency == null) {
				missingDeps.add(class1);
			}
		}

		return missingDeps;
	}
}
