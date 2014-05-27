
/***************************************************************************
 *   Copyright 2006-2014 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
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

import static org.mockito.Mockito.*;

import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test of {@link SwingPopupErrorHandler}.
 *
 * @author Christian Ihle
 */
public class SwingPopupErrorHandlerTest {

    private SwingPopupErrorHandler errorHandler;
    private UITools uiTools;

    @Before
    public void setUp() {
        errorHandler = new SwingPopupErrorHandler();
        uiTools = TestUtils.setFieldValueWithMock(errorHandler, "uiTools", UITools.class);
    }

    @Test
    public void errorReportedShouldShowErrorMessageUsingInvokeLater() {
        doAnswer(withRunningArgument()).when(uiTools).invokeLater(any(Runnable.class));

        errorHandler.errorReported("This is an error");

        verify(uiTools).invokeLater(any(Runnable.class));
        verify(uiTools).showErrorMessage("This is an error", "Error");
    }

    @Test
    public void criticalErrorReportedShouldShowErrorMessage() {
        errorHandler.criticalErrorReported("This is another error");

        verify(uiTools).showErrorMessage("This is another error", "Critical Error");
    }

    private Answer withRunningArgument() {
        return new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                final Runnable runnable = (Runnable) invocation.getArguments()[0];
                runnable.run();

                return null;
            }
        };
    }
}
