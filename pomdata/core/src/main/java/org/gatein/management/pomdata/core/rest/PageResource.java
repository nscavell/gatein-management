/*
 * JBoss, a division of Red Hat
 * Copyright 2010, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.gatein.management.pomdata.core.rest;

import org.exoplatform.portal.pom.data.PageData;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
import org.gatein.management.ManagementException;
import org.gatein.management.pomdata.api.PageManagementService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 * @version $Revision$
 */
//TODO: Add debugging
public class PageResource extends AbstractExoContainerResource<PageManagementService>
{
   private static final Logger log = LoggerFactory.getLogger(SiteResource.class);

   public PageResource(String containerName)
   {
      super(containerName);
   }

   @Override
   public Logger getLogger()
   {
      return log;
   }

   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML})
   public GenericEntity<List<PageData>> getPages(@Context UriInfo uriInfo,
                                                 @QueryParam("ownerType") String ownerType,
                                                 @QueryParam("ownerId") String ownerId)
   {
      ownerType = checkOwnerType(ownerType);
      checkOwnerId(ownerId);

      List<PageData> pages = null;
      try
      {
         pages = getService().getPages(ownerType, ownerId);
      }
      catch (ManagementException e)
      {
         handleManagementException(e, uriInfo);
      }
      catch (Throwable t)
      {
         handleUnknownError(t, uriInfo);
      }

      if (pages == null || pages.isEmpty()) pages = null;
      checkNullResult(pages, uriInfo);

      return new GenericEntity<List<PageData>>(pages){};
   }

   @GET
   @Path("/{page-name}")
   @Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML})
   public PageData getPage(@Context UriInfo uriInfo,
                           @QueryParam("ownerType") String ownerType,
                           @QueryParam("ownerId") String ownerId,
                           @PathParam("page-name") String pageName)
   {
      ownerType = checkOwnerType(ownerType);
      checkOwnerId(ownerId);

      PageData data = null;
      try
      {
         data = getService().getPage(ownerType, ownerId, pageName);
      }
      catch (ManagementException e)
      {
         handleManagementException(e, uriInfo);
      }
      catch (Throwable t)
      {
         handleUnknownError(t, uriInfo);
      }

      checkNullResult(data, uriInfo);
      return data;
   }

   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML})
   public PageData createPage(@Context UriInfo uriInfo,
                          @QueryParam("ownerType") String ownerType,
                          @QueryParam("ownerId") String ownerId,
                          @QueryParam("name") String pageName,
                          @QueryParam("title") String title)
   {
      ownerType = checkOwnerType(ownerType);
      checkOwnerId(ownerId);

      PageData page = null;
      try
      {
         page = getService().createPage(ownerType, ownerId, pageName, title);
      }
      catch (ManagementException e)
      {
         handleManagementException(e, uriInfo);
      }
      catch (Throwable t)
      {
         handleUnknownError(t, uriInfo);
      }

      checkNullResult(page, uriInfo);
      return page;
   }

   @PUT
   @Path("/{page-name}")
   @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML})
   public Response updatePage(@Context UriInfo uriInfo,
                              @QueryParam("ownerType") String ownerType,
                              @QueryParam("ownerId") String ownerId,
                              @PathParam("page-name") String pageName,
                              PageData page)
   {
      ownerType = checkOwnerType(ownerType);
      checkOwnerId(ownerId);

      page = updateData(ownerType, ownerId, pageName, page);

      if (!page.getName().equals(pageName)) throw new WebApplicationException(new Exception("URI page name is not same as page name in data."), Response.Status.BAD_REQUEST);

      if (log.isDebugEnabled())
      {
         log.debug("Updating page " + pageName + " with ownerType " + ownerType + " and ownerId " + ownerId);
      }
      try
      {
         getService().updatePage(page);
         return Response.ok().build();
      }
      catch (ManagementException e)
      {
         handleManagementException(e, uriInfo);
      }
      catch (Throwable t)
      {
         handleUnknownError(t, uriInfo);
      }

      return Response.serverError().build();
   }

   @DELETE
   @Path("/{page-name}")
   @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML})
   public Response deletePage(@Context UriInfo uriInfo,
                              @QueryParam("ownerType") String ownerType,
                              @QueryParam("ownerId") String ownerId,
                              @PathParam("page-name") String pageName)
   {
      ownerType = checkOwnerType(ownerType);
      checkOwnerId(ownerId);

      try
      {
         getService().deletePage(ownerType, ownerId, pageName);
      }
      catch (ManagementException e)
      {
         handleManagementException(e, uriInfo);
      }
      catch (Throwable t)
      {
         handleUnknownError(t, uriInfo);
      }

      return Response.ok().build();
   }

   private PageData updateData(String ownerType, String ownerId, String pageName, PageData originalPage)
   {
      return new PageData(originalPage.getStorageId(), originalPage.getId(), pageName, originalPage.getIcon(),
         originalPage.getTemplate(), originalPage.getFactoryId(), originalPage.getTitle(), originalPage.getDescription(),
         originalPage.getWidth(), originalPage.getHeight(), originalPage.getAccessPermissions(), originalPage.getChildren(),
         ownerType, ownerId, originalPage.getEditPermission(), originalPage.isShowMaxWindow());
   }
}
