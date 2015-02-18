/*******************************************************************************
 * Copyright (c) 2015 Jens Reimann.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jens Reimann - initial API and implementation
 *******************************************************************************/
package de.dentrassi.osgi.job;

public interface JobHandle
{
    public String getId ();

    public State getState ();

    public Throwable getError ();

    public Job getJob ();

    public default boolean isComplete ()
    {
        return getState () == State.COMPLETE;
    }
}