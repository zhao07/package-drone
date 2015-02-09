/*******************************************************************************
 * Copyright (c) 2014, 2015 Jens Reimann.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jens Reimann - initial API and implementation
 *******************************************************************************/
package de.dentrassi.osgi.web.controller.binding;

import org.eclipse.scada.utils.ExceptionHelper;

public class ExceptionError implements BindingError
{
    private final Throwable ex;

    public ExceptionError ( final Throwable ex )
    {
        this.ex = ex;
    }

    @Override
    public String getMessage ()
    {
        return ExceptionHelper.getMessage ( this.ex );
    }

    @Override
    public String toString ()
    {
        return String.format ( "[ExceptionError: %s]", getMessage () );
    }
}
