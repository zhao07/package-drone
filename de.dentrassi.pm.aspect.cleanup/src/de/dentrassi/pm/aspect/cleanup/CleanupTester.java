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
package de.dentrassi.pm.aspect.cleanup;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import de.dentrassi.pm.common.ArtifactInformation;

public interface CleanupTester
{

    public static enum Action
    {
        KEEP,
        DELETE;
    }

    public SortedMap<ResultKey, List<ResultEntry>> testCleanup ( final Collection<ArtifactInformation> artifacts, CleanupConfiguration configuration );
}
