/*******************************************************************************
 * Copyright (c) 2015 IBH SYSTEMS GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package de.dentrassi.pm.storage;

import java.io.InputStream;

/**
 * The cache entry information and data
 */
public interface CacheEntry extends CacheEntryInformation
{
    /**
     * Get the stream to the data
     * <p>
     * <em>Note:</em> This method may only be called once
     * </p>
     */
    public InputStream getStream ();
}
