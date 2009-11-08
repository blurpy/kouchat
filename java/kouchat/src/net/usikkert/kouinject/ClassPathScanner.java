
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
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;

import net.usikkert.kouchat.util.Tools;

/**
 * Finds classes by scanning the classpath. Classes are searched for in the file system
 * and in jar-files.
 *
 * @author Christian Ihle
 */
public class ClassPathScanner implements ClassLocator
{
	private static final Logger LOG = Logger.getLogger( ClassPathScanner.class.getName() );

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Class<?>> findClasses( final String basePackage )
	{
		final ClassLoader loader = getClassLoader();

		try
		{
			final long start = System.currentTimeMillis();
			final Set<Class<?>> classes = findClasses( loader, basePackage );
			final long stop = System.currentTimeMillis();

			LOG.info( "Time spent scanning classpath: " + ( stop - start ) + " ms" );
			LOG.info( "Classes found: " + classes.size() );

			return classes;
		}

		catch ( final IOException e )
		{
			throw new RuntimeException( e );
		}

		catch ( final ClassNotFoundException e )
		{
			throw new RuntimeException( e );
		}
	}

	private Set<Class<?>> findClasses( final ClassLoader loader, final String basePackage ) throws IOException, ClassNotFoundException
	{
		final Set<Class<?>> classes = new HashSet<Class<?>>();
		final String path = basePackage.replace( '.', '/' );
		final Enumeration<URL> resources = loader.getResources( path );

		if ( resources != null )
		{
			while ( resources.hasMoreElements() )
			{
				final String filePath = getFilePath( resources.nextElement() );

				if ( filePath != null )
				{
					if ( isJarFilePath( filePath ) )
					{
						final String jarPath = getJarPath( filePath );
						classes.addAll( getFromJARFile( jarPath, path ) );
					}

					else
					{
						classes.addAll( getFromDirectory( new File( filePath ), basePackage ) );
					}
				}
			}
		}

		return classes;
	}

	private Set<Class<?>> getFromDirectory( final File directory, final String packageName ) throws ClassNotFoundException
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

				else if ( isClass( file.getName() ) )
				{
					final String className = packageName + '.' + stripFilenameExtension( file.getName() );
					final Class<?> clazz = Class.forName( className );
					addClass( clazz, classes );
				}
			}
		}

		return classes;
	}

	private Set<Class<?>> getFromJARFile( final String jar, final String packageName ) throws IOException, ClassNotFoundException
	{
		final Set<Class<?>> classes = new HashSet<Class<?>>();
		final JarInputStream jarFile = new JarInputStream( new FileInputStream( jar ) );
		JarEntry jarEntry;

		do
		{
			jarEntry = jarFile.getNextJarEntry();

			if ( jarEntry != null )
			{
				final String fileName = jarEntry.getName();

				if ( isClass( fileName ) )
				{
					final String className = stripFilenameExtension( fileName );

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

	/**
	 * Gets the best possible classloader for scanning after classes. Usually it's
	 * the current thread's context classloader, but if that's not available then the
	 * classloader for this class is used instead.
	 *
	 * @return A usable classloader.
	 */
	private ClassLoader getClassLoader()
	{
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		if ( contextClassLoader != null )
			return contextClassLoader;
		else
			return getClass().getClassLoader();
	}

	private String getFilePath( final URL url )
	{
		final String filePath = url.getFile();

		if ( filePath != null )
		{
			return fixWindowsSpace( filePath );
		}

		return null;
	}

	private boolean isJarFilePath( final String filePath )
	{
		return ( filePath.indexOf( "!" ) > 0 ) && ( filePath.indexOf( ".jar" ) > 0 );
	}

	private String fixWindowsSpace( final String filePath )
	{
		if ( filePath.indexOf( "%20" ) > 0 )
		{
			return filePath.replaceAll( "%20", " " );
		}

		return filePath;
	}

	private String getJarPath( final String filePath )
	{
		final String jarPath = filePath.substring( 0, filePath.indexOf( "!" ) ).substring( filePath.indexOf( ":" ) + 1 );
		return fixWindowsJarPath( jarPath );
	}

	private String fixWindowsJarPath( final String jarPath )
	{
		if ( jarPath.indexOf( ":" ) >= 0 )
		{
			return jarPath.substring( 1 );
		}

		return jarPath;
	}

	private static String stripFilenameExtension( final String file )
	{
		return Tools.getFileBaseName( file );
	}

	private boolean isClass( final String fileName )
	{
		return fileName.endsWith( ".class" );
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
