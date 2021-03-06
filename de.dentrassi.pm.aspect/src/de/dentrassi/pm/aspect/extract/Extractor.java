/*******************************************************************************
 * Copyright (c) 2014, 2015 IBH SYSTEMS GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package de.dentrassi.pm.aspect.extract;

import java.nio.file.Path;
import java.util.Map;

public interface Extractor extends ChannelAspectFunction
{
    public interface Context
    {
        /**
         * Get the name of the uploaded artifact
         * 
         * @return the name of the artifact
         */
        public String getName ();

        /**
         * Get the path to a temporary file where the BLOB is stored
         *
         * @return the path to the temporary BLOB file
         */
        public Path getPath ();
    }

    public void extractMetaData ( Context context, Map<String, String> metadata ) throws Exception;
}
