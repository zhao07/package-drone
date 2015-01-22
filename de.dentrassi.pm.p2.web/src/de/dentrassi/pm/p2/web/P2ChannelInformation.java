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
package de.dentrassi.pm.p2.web;

import de.dentrassi.pm.common.MetaKeyBinding;
import de.dentrassi.pm.p2.aspect.P2RepositoryAspect;

public class P2ChannelInformation
{
    @MetaKeyBinding ( namespace = P2RepositoryAspect.ID, key = "title" )
    private String title;

    public void setTitle ( final String title )
    {
        this.title = title;
    }

    public String getTitle ()
    {
        return this.title;
    }
}
