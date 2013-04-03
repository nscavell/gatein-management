/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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

package org.gatein.management.cli.crash.commands;

import groovy.lang.Closure;
import org.crsh.cli.completers.AbstractPathCompleter;
import org.crsh.cli.descriptor.ParameterDescriptor;
import org.crsh.cli.spi.Completion;
import org.crsh.command.ScriptException;
import org.gatein.management.api.ContentType;
import org.gatein.management.api.PathAddress;
import org.gatein.management.api.controller.ManagedRequest;
import org.gatein.management.api.controller.ManagedResponse;
import org.gatein.management.api.controller.ManagementController;
import org.gatein.management.api.exceptions.ResourceNotFoundException;
import org.gatein.management.api.model.ModelList;
import org.gatein.management.api.model.ModelObject;
import org.gatein.management.api.model.ModelReference;
import org.gatein.management.api.model.ModelValue;
import org.gatein.management.api.operation.OperationNames;
import org.gatein.management.api.operation.model.ReadResourceModel;
import org.gatein.management.cli.crash.arguments.Path;
import org.gatein.management.cli.crash.arguments.PathCompleter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 * @version $Revision$
 */
public class ManagementCommand extends GateInCommand implements PathCompleter
{
   @Override
   public Completion complete(ParameterDescriptor parameter, String prefix) throws Exception
   {
      if (parameter.getCompleterType() == PathCompleter.class)
      {
         try
         {
            Closure assertConnected = (Closure) getProperty("assertConnected");
            assertConnected.call();
         }
         catch (ScriptException e)
         {
            return Completion.create();
         }

         Closure closure = (Closure) getProperty("begin");
         closure.call();

         try
         {
            final ManagementController controller = (ManagementController) getProperty("controller");
            final PathAddress address = (PathAddress) getProperty("address");
            AbstractPathCompleter<PathAddress> completer = new AbstractPathCompleter<PathAddress>()
            {
               @Override
               protected String getCurrentPath() throws Exception
               {
                  return address.toString();
               }

               @Override
               protected PathAddress getPath(String path) throws Exception
               {
                  try
                  {
                     PathAddress pathAddress = getAddress(address, new Path(path));
                     ManagedRequest request = ManagedRequest.Factory.create(OperationNames.READ_RESOURCE, pathAddress, ContentType.JSON);
                     ManagedResponse response = controller.execute(request);
                     if (response.getOutcome().isSuccess())
                     {
                        return pathAddress;
                     }
                     else
                     {
                        return null;
                     }
                  }
                  catch (ResourceNotFoundException e)
                  {
                     return null;
                  }
               }

               @Override
               protected boolean exists(PathAddress path) throws Exception
               {
                  return path != null;
               }

               @Override
               protected boolean isDirectory(PathAddress path) throws Exception
               {
                  return true;
               }

               @Override
               protected boolean isFile(PathAddress path) throws Exception
               {
                  return false;
               }

               @Override
               protected Collection<PathAddress> getChilren(PathAddress path) throws Exception
               {
                  Set<String> names = getChildren(controller, path);
                  List<PathAddress> children = new ArrayList<PathAddress>(names.size());
                  for (String name : names)
                  {
                     children.add(path.append(name));
                  }

                  return children;
               }

               @Override
               protected String getName(PathAddress path) throws Exception
               {
                  return path.getLastElement();
               }
            };

            return completer.complete(parameter, prefix);
         }
         catch (Exception e)
         {
            return Completion.create();
         }
         finally
         {
            closure = (Closure) getProperty("end");
            closure.call();
         }
      }

      return Completion.create();
   }

   protected PathAddress getAddress(PathAddress currentAddress, Path path)
   {
      PathAddress pathAddress = currentAddress;
      if (path != null)
      {
         int traversal;
         if (path.isAbsolute())
         {
            pathAddress = PathAddress.pathAddress(path.value);
         }
         else if ((traversal = path.getParentDirectives()) > 0)
         {
            Path subPath = path.getSubPath(traversal);
            int size = pathAddress.size();
            if (traversal >= size)
            {
               pathAddress = PathAddress.pathAddress(subPath.value);
            }
            else
            {
               pathAddress = pathAddress.subAddress(0, size - traversal).append(subPath.value);
            }
         }
         else
         {
            pathAddress = pathAddress.append(path.value);
         }
      }

      return pathAddress;
   }

   protected Set<String> getChildren(ManagementController controller, PathAddress address)
   {
      try
      {
         ManagedRequest request = ManagedRequest.Factory.create(OperationNames.READ_RESOURCE, address, ContentType.JSON);
         ManagedResponse response = controller.execute(request);
         if (response != null && response.getOutcome().isSuccess())
         {
            return getChildren(response.getResult(), address);
         }
         else
         {
            return Collections.emptySet();
         }
      }
      catch (ResourceNotFoundException me)
      {
         return Collections.emptySet();
      }
   }

   protected Set<String> getChildren(Object result, PathAddress address)
   {
      if (result instanceof ReadResourceModel)
      {
         return ((ReadResourceModel) result).getChildren();
      }
      else if (result instanceof ModelValue)
      {
         Set<String> children = new LinkedHashSet<String>();
         if (result instanceof ModelList)
         {
            for (ModelValue mv : (ModelList) result)
            {
               addChildren(children, mv, address);
            }
         }
         else if (result instanceof ModelObject)
         {
            Set<String> names = ((ModelObject) result).getNames();
            for (String name : names)
            {
               addChildren(children, ((ModelObject) result).get(name), address);
            }
         }

         return children;
      }

      return Collections.emptySet();
   }

   private static void addChildren(Set<String> children, ModelValue value, PathAddress address)
   {
      if (value.getValueType() == ModelValue.ModelValueType.REFERENCE)
      {
         ModelReference modelRef = value.asValue(ModelReference.class);
         PathAddress ref = modelRef.getValue();
         if (ref.size() > 1 && ref.subAddress(0, ref.size() - 1).equals(address))
         {
            children.add(ref.getLastElement());
         }
      }
      else if (value.getValueType() == ModelValue.ModelValueType.LIST)
      {
         ModelList list = value.asValue(ModelList.class);
         Set<String> names = new LinkedHashSet<String>(list.size());
         // If the list is a direct child and all items in the list are model references, then we can conclude that
         // these should be added to the children
         for (ModelValue mv : list)
         {
            if (mv.getValueType() == ModelValue.ModelValueType.REFERENCE)
            {
               addChildren(names, mv, address);
            }
            else
            {
               names.clear();
               break;
            }
         }
         if (!names.isEmpty())
         {
            children.addAll(names);
         }
      }
   }
}
