
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

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;

import net.usikkert.kouchat.util.Tools;

/**
 *
 * @author Christian Ihle
 */
public class ClassPathScanner
{
	private static final Logger LOG = Logger.getLogger( ClassPathScanner.class.getName() );

	public Set<Class<?>> findClasses( final String packageName )
	{
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();

		try
		{
			final long start = System.currentTimeMillis();
			final Set<Class<?>> classes = findClasses( loader, packageName );
			final long stop = System.currentTimeMillis();

			LOG.info( "Time spent scanning classpath: " + ( stop - start ) + " ms" );
			LOG.info( "Classes found: " + classes.size() );

			return classes;
		}

		catch ( final Exception e )
		{
			throw new RuntimeException( e );
		}
	}

	private Set<Class<?>> findClasses( final ClassLoader loader, final String packageName ) throws Exception
	{
		final Set<Class<?>> classes = new HashSet<Class<?>>();
		final String path = packageName.replace( '.', '/' );
		final Enumeration<URL> resources = loader.getResources( path );

		if ( resources != null )
		{
			while ( resources.hasMoreElements() )
			{
				String filePath = resources.nextElement().getFile();
				// WINDOWS HACK
				if ( filePath.indexOf( "%20" ) > 0 )
				{
					filePath = filePath.replaceAll( "%20", " " );
				}

				if ( filePath != null )
				{
					if ( ( filePath.indexOf( "!" ) > 0 ) && ( filePath.indexOf( ".jar" ) > 0 ) )
					{
						String jarPath = filePath.substring( 0, filePath.indexOf( "!" ) )
								.substring( filePath.indexOf( ":" ) + 1 );

						// WINDOWS HACK
						if ( jarPath.indexOf( ":" ) >= 0 )
						{
							jarPath = jarPath.substring( 1 );
						}

						classes.addAll( getFromJARFile( jarPath, path ) );
					}

					else
					{
						classes.addAll( getFromDirectory( new File( filePath ), packageName ) );
					}
				}
			}
		}

		return classes;
	}

	private Set<Class<?>> getFromDirectory( final File directory, final String packageName ) throws Exception
	{
		final Set<Class<?>> classes = new HashSet<Class<?>>();

		if ( directory.exists() )
		{
			final File[] files = directory.listFiles();

			for ( final File file : files )
			{
				if ( file.isDirectory() )
				{
					classes.addAll( getFromDirectory( file, packageName + "." + file.getName() ) );
				}

				else if ( file.getName().endsWith( ".class" ) )
				{
					final String name = packageName + '.' + stripFilenameExtension( file.getName() );
					final Class<?> clazz = Class.forName( name );
					addClass( clazz, classes );
				}
			}
		}

		return classes;
	}

	private static String stripFilenameExtension( final String file )
	{
		return Tools.getFileBaseName( file );
	}

	private Set<Class<?>> getFromJARFile( final String jar, final String packageName ) throws Exception
	{
		final Set<Class<?>> classes = new HashSet<Class<?>>();
		final JarInputStream jarFile = new JarInputStream( new FileInputStream( jar ) );
		JarEntry jarEntry;

		do
		{
			jarEntry = jarFile.getNextJarEntry();

			if ( jarEntry != null )
			{
				String className = jarEntry.getName();

				if ( className.endsWith( ".class" ) )
				{
					className = stripFilenameExtension( className );

					if ( className.startsWith( packageName ) )
					{
						final Class<?> clazz = Class.forName( className.replace( '/', '.' ) );
						addClass( clazz, classes );

					}
				}
			}
		} while ( jarEntry != null );

		return classes;
	}

	private void addClass( final Class<?> clazz, final Set<Class<?>> classes )
	{
		if ( !clazz.isAnonymousClass() && !clazz.isMemberClass() && !clazz.isSynthetic()
				&& !clazz.isAnnotation() && !clazz.isEnum() && !clazz.isInterface() )
		{
			classes.add( clazz );
		}
	}
}
