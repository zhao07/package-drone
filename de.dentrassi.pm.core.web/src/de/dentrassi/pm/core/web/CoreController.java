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
package de.dentrassi.pm.core.web;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import de.dentrassi.osgi.web.Controller;
import de.dentrassi.osgi.web.LinkTarget;
import de.dentrassi.osgi.web.ModelAndView;
import de.dentrassi.osgi.web.RequestMapping;
import de.dentrassi.osgi.web.RequestMethod;
import de.dentrassi.osgi.web.ViewResolver;
import de.dentrassi.osgi.web.controller.ControllerInterceptor;
import de.dentrassi.osgi.web.controller.binding.BindingResult;
import de.dentrassi.osgi.web.controller.form.FormData;
import de.dentrassi.pm.common.MetaKey;
import de.dentrassi.pm.common.MetaKeys;
import de.dentrassi.pm.common.web.InterfaceExtender;
import de.dentrassi.pm.common.web.menu.MenuEntry;
import de.dentrassi.pm.core.CoreService;
import de.dentrassi.pm.sec.web.controller.HttpConstraints;
import de.dentrassi.pm.sec.web.controller.HttpContraintControllerInterceptor;
import de.dentrassi.pm.sec.web.controller.Secured;
import de.dentrassi.pm.sec.web.controller.SecuredControllerInterceptor;
import de.dentrassi.pm.system.SystemService;

@Controller
@ViewResolver ( "/WEB-INF/views/%s.jsp" )
@Secured
@RequestMapping ( "/config/core" )
@ControllerInterceptor ( SecuredControllerInterceptor.class )
@HttpConstraint ( rolesAllowed = "ADMIN" )
@ControllerInterceptor ( HttpContraintControllerInterceptor.class )
public class CoreController implements InterfaceExtender
{
    private CoreService coreService;

    private SystemService systemService;

    private static final Method METHOD_LIST = LinkTarget.getControllerMethod ( CoreController.class, "list" );

    private static final Method METHOD_SITE = LinkTarget.getControllerMethod ( CoreController.class, "site" );

    public void setCoreService ( final CoreService service )
    {
        this.coreService = service;
    }

    public void setSystemService ( final SystemService systemService )
    {
        this.systemService = systemService;
    }

    @Override
    public List<MenuEntry> getMainMenuEntries ( final HttpServletRequest request )
    {
        final List<MenuEntry> result = new LinkedList<> ();

        if ( HttpConstraints.isCallAllowed ( METHOD_LIST, request ) )
        {
            result.add ( new MenuEntry ( "Administration", 1000, "View properties", 1000, LinkTarget.createFromController ( METHOD_LIST ), null, null ) );
        }
        if ( HttpConstraints.isCallAllowed ( METHOD_SITE, request ) )
        {
            result.add ( new MenuEntry ( "Administration", 1000, "Site", 500, LinkTarget.createFromController ( METHOD_SITE ), null, null ) );
        }

        return result;
    }

    @RequestMapping ( value = "/list" )
    public ModelAndView list ()
    {
        final Map<String, Object> model = new HashMap<> ();

        model.put ( "properties", this.coreService.list () );

        return new ModelAndView ( "list", model );
    }

    @RequestMapping ( value = "/site" )
    public ModelAndView site ()
    {
        final Map<String, Object> model = new HashMap<> ();

        final Map<MetaKey, String> props = this.coreService.list ();

        final SiteInformation site = new SiteInformation ();
        try
        {
            MetaKeys.bind ( site, props );
        }
        catch ( final Exception e )
        {
            // use plain new object
        }

        fillModel ( model, site );

        return new ModelAndView ( "site", model );
    }

    protected void fillModel ( final Map<String, Object> model, final SiteInformation site )
    {
        model.put ( "command", site );
        model.put ( "defaultSitePrefix", this.systemService.getDefaultSitePrefix () );
    }

    @RequestMapping ( value = "/site", method = RequestMethod.POST )
    public ModelAndView sitePost ( @Valid @FormData ( "command" ) final SiteInformation site, final BindingResult result ) throws Exception
    {
        final Map<String, Object> model = new HashMap<> ();

        if ( !result.hasErrors () )
        {
            // store
            final Map<MetaKey, String> props = MetaKeys.unbind ( site );

            final Map<String, String> data = new HashMap<> ( props.size () );
            for ( final Map.Entry<MetaKey, String> entry : props.entrySet () )
            {
                data.put ( entry.getKey ().getKey (), entry.getValue () );
            }

            this.coreService.setProperties ( data );
        }

        fillModel ( model, site );

        return new ModelAndView ( "site", model );
    }
}
