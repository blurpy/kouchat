
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.ui.swing;

import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

/**
 * This is almost a normal {@link StyledEditorKit}, with the
 * only difference being the use of a custom view factory
 * to be able to middle align icons with the text.
 *
 * @author Christian Ihle
 * @see MiddleAlignedIconViewFactory
 * @see MiddleAlignedIconView
 */
public class MiddleAlignedIconViewEditorKit extends StyledEditorKit
{
    /** Default version uid. */
    private static final long serialVersionUID = 1L;

    /** The custom view factory to use. */
    private final ViewFactory viewFactory;

    /**
     * Constructor. Initializes the view factory.
     */
    public MiddleAlignedIconViewEditorKit()
    {
        viewFactory = new MiddleAlignedIconViewFactory();
    }

    /**
     * Gets the {@link MiddleAlignedIconViewFactory}.
     *
     * {@inheritDoc}
     */
    @Override
    public ViewFactory getViewFactory()
    {
        return viewFactory;
    }
}
