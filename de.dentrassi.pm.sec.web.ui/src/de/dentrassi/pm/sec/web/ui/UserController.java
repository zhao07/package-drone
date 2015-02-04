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
package de.dentrassi.pm.sec.web.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import de.dentrassi.osgi.web.Controller;
import de.dentrassi.osgi.web.LinkTarget;
import de.dentrassi.osgi.web.ModelAndView;
import de.dentrassi.osgi.web.RequestMapping;
import de.dentrassi.osgi.web.RequestMethod;
import de.dentrassi.osgi.web.ViewResolver;
import de.dentrassi.osgi.web.controller.ControllerInterceptor;
import de.dentrassi.osgi.web.controller.binding.BindingResult;
import de.dentrassi.osgi.web.controller.binding.PathVariable;
import de.dentrassi.osgi.web.controller.binding.RequestParameter;
import de.dentrassi.osgi.web.controller.form.FormData;
import de.dentrassi.pm.common.web.CommonController;
import de.dentrassi.pm.common.web.InterfaceExtender;
import de.dentrassi.pm.common.web.Modifier;
import de.dentrassi.pm.common.web.menu.MenuEntry;
import de.dentrassi.pm.sec.CreateUser;
import de.dentrassi.pm.sec.DatabaseDetails;
import de.dentrassi.pm.sec.DatabaseUserInformation;
import de.dentrassi.pm.sec.UserStorage;
import de.dentrassi.pm.sec.web.controller.Secured;
import de.dentrassi.pm.sec.web.controller.SecuredControllerInterceptor;
import de.dentrassi.pm.sec.web.filter.SecurityFilter;
import de.dentrassi.pm.storage.web.breadcrumbs.Breadcrumbs;
import de.dentrassi.pm.storage.web.breadcrumbs.Breadcrumbs.Entry;

@Controller
@ViewResolver ( "/WEB-INF/views/%s.jsp" )
@RequestMapping ( "/user" )
@Secured
@ControllerInterceptor ( SecuredControllerInterceptor.class )
public class UserController extends AbstractUserCreationController implements InterfaceExtender
{

    @Override
    public List<MenuEntry> getMainMenuEntries ( final HttpServletRequest request )
    {
        final List<MenuEntry> result = new LinkedList<> ();

        if ( SecurityFilter.isLoggedIn ( request ) )
        {
            result.add ( new MenuEntry ( "Administration", 10_000, "Users", 1_000, LinkTarget.createFromController ( UserController.class, "list" ), null, null ) );
        }

        return result;
    }

    @Override
    public List<MenuEntry> getActions ( final HttpServletRequest request, final Object object )
    {
        if ( UserStorage.ACTION_TAG_USERS.equals ( object ) )
        {
            final List<MenuEntry> result = new LinkedList<MenuEntry> ();

            if ( SecurityFilter.isLoggedIn ( request ) )
            {
                result.add ( new MenuEntry ( "Add user", 100, LinkTarget.createFromController ( UserController.class, "addUser" ), Modifier.PRIMARY, null ) );
            }

            return result;
        }
        else if ( object instanceof DatabaseUserInformation )
        {
            final DatabaseDetails details = ( (DatabaseUserInformation)object ).getDetails ( DatabaseDetails.class );
            if ( details != null )
            {
                final List<MenuEntry> result = new LinkedList<MenuEntry> ();

                final Map<String, Object> model = new HashMap<> ( 1 );
                final String userId = ( (DatabaseUserInformation)object ).getId ();
                model.put ( "userId", userId );

                final boolean you = userId.equals ( request.getRemoteUser () );

                if ( SecurityFilter.isLoggedIn ( request ) )
                {
                    result.add ( new MenuEntry ( "Edit user", 100, LinkTarget.createFromController ( UserController.class, "editUser" ).expand ( model ), Modifier.PRIMARY, null ) );

                    if ( !you )
                    {
                        if ( details.isLocked () )
                        {
                            result.add ( new MenuEntry ( "Unlock user", 200, LinkTarget.createFromController ( UserController.class, "unlockUser" ).expand ( model ), Modifier.SUCCESS, null ) );
                        }
                        else
                        {
                            result.add ( new MenuEntry ( "Lock user", 200, LinkTarget.createFromController ( UserController.class, "lockUser" ).expand ( model ), Modifier.WARNING, null ) );
                        }

                        if ( !details.isDeleted () )
                        {
                            result.add ( new MenuEntry ( "Delete user", 300, LinkTarget.createFromController ( UserController.class, "deleteUser" ).expand ( model ), Modifier.DANGER, "trash" ) );
                        }
                    }
                }

                return result;
            }
        }
        return null;
    }

    @RequestMapping ( method = RequestMethod.GET )
    public ModelAndView list ( @RequestParameter ( required = false, value = "position" ) Integer position )
    {
        final ModelAndView result = new ModelAndView ( "user/list" );

        if ( position == null )
        {
            position = 0;
        }

        final List<DatabaseUserInformation> list = this.storage.list ( position, 25 + 1 );

        final boolean prev = position > 0;
        boolean next;

        if ( list.size () > 25 )
        {
            // check if we have more
            next = true;
            list.remove ( list.size () - 1 );
        }
        else
        {
            next = false;
        }

        result.put ( "users", list );

        result.put ( "prev", prev );
        result.put ( "next", next );
        result.put ( "position", position );
        result.put ( "pageSize", 25 );

        return result;
    }

    @RequestMapping ( value = "/add", method = RequestMethod.GET )
    public ModelAndView addUser ()
    {
        final ModelAndView model = new ModelAndView ( "user/add" );

        model.put ( "command", new CreateUser () );

        return model;
    }

    @RequestMapping ( value = "/add", method = RequestMethod.POST )
    public ModelAndView addUserPost ( @Valid @FormData ( "command" ) final CreateUser data, final BindingResult result )
    {
        if ( result.hasErrors () )
        {
            final Map<String, Object> model = new HashMap<> ( 1 );
            model.put ( "command", data );
            return new ModelAndView ( "user/add", model );
        }

        final DatabaseUserInformation newUser = this.storage.createUser ( data, true );

        return new ModelAndView ( String.format ( "redirect:/user/%s/view", newUser.getId () ) );
    }

    @RequestMapping ( value = "/{userId}/view", method = RequestMethod.GET )
    public ModelAndView viewUser ( @PathVariable ( "userId" ) final String userId, final HttpServletRequest request )
    {
        final DatabaseUserInformation user = this.storage.getUserDetails ( userId );

        if ( user == null || user.getDetails ( DatabaseDetails.class ) == null )
        {
            return CommonController.createNotFound ( "user", userId );
        }

        final ModelAndView model = new ModelAndView ( "user/view" );
        model.put ( "user", user );
        model.put ( "you", userId.equals ( request.getRemoteUser () ) );
        return model;
    }

    protected void addBreadcrumbs ( final String action, final String userId, final Map<String, Object> model )
    {
        model.put ( "breadcrumbs", new Breadcrumbs ( new Entry ( "Home", "/" ), Breadcrumbs.create ( "Users", UserController.class, "list" ), Breadcrumbs.create ( "User", UserController.class, "viewUser", "userId", userId ), new Entry ( action ) ) );
    }

    @RequestMapping ( value = "/{userId}/edit", method = RequestMethod.GET )
    public ModelAndView editUser ( @PathVariable ( "userId" ) final String userId )
    {
        final DatabaseUserInformation user = this.storage.getUserDetails ( userId );

        if ( user == null || user.getDetails ( DatabaseDetails.class ) == null )
        {
            return CommonController.createNotFound ( "user", userId );
        }

        final Map<String, Object> model = new HashMap<> ( 2 );

        model.put ( "user", user );
        model.put ( "command", user.getDetails ( DatabaseDetails.class ) );

        addBreadcrumbs ( "Edit", userId, model );

        return new ModelAndView ( "user/edit", model );
    }

    @RequestMapping ( value = "/{userId}/edit", method = RequestMethod.POST )
    public ModelAndView editUserPost ( @PathVariable ( "userId" ) final String userId, @Valid @FormData ( "command" ) final DatabaseDetails data, final BindingResult result, final HttpSession session )
    {
        final DatabaseUserInformation user = this.storage.getUserDetails ( userId );

        if ( user == null || user.getDetails ( DatabaseDetails.class ) == null )
        {
            return CommonController.createNotFound ( "user", userId );
        }

        if ( result.hasErrors () )
        {
            final Map<String, Object> model = new HashMap<> ( 2 );
            model.put ( "command", data );
            model.put ( "user", user );

            addBreadcrumbs ( "Edit", userId, model );

            return new ModelAndView ( "user/edit", model );
        }

        this.storage.updateUser ( userId, data );

        // TODO: only reload if it was our own profile
        SecurityFilter.markReloadDetails ( session );

        return new ModelAndView ( String.format ( "redirect:/user/%s/view", userId ) );
    }

    @RequestMapping ( "/{userId}/lock" )
    public ModelAndView lockUser ( @PathVariable ( "userId" ) final String userId )
    {
        this.storage.lockUser ( userId );
        return new ModelAndView ( "redirect:/user/" + userId + "/view" );
    }

    @RequestMapping ( "/{userId}/unlock" )
    public ModelAndView unlockUser ( @PathVariable ( "userId" ) final String userId )
    {
        this.storage.unlockUser ( userId );
        return new ModelAndView ( "redirect:/user/" + userId + "/view" );
    }

    @RequestMapping ( "/{userId}/delete" )
    public ModelAndView deleteUser ( @PathVariable ( "userId" ) final String userId )
    {
        this.storage.deleteUser ( userId );
        return new ModelAndView ( "redirect:/user/" + userId + "/view" );
    }
}
