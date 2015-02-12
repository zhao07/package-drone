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
package de.dentrassi.pm.npm;

public class PackageInfo
{
    private String name;

    private String version;

    private String license;

    public void setName ( final String name )
    {
        this.name = name;
    }

    public void setVersion ( final String version )
    {
        this.version = version;
    }

    public String getName ()
    {
        return this.name;
    }

    public String getVersion ()
    {
        return this.version;
    }

    public String getLicense ()
    {
        return this.license;
    }

    public void setLicense ( final String license )
    {
        this.license = license;
    }
}
