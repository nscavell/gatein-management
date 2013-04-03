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

package org.gatein.management.cli.crash.arguments;

import org.crsh.cli.Man;
import org.crsh.cli.Option;
import org.crsh.cli.Usage;
import org.crsh.cli.descriptor.ParameterDescriptor;
import org.crsh.cli.spi.Completer;
import org.crsh.cli.spi.Completion;
import org.gatein.management.api.ContentType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 * @version $Revision$
 */
@Retention(RetentionPolicy.RUNTIME)
@Option(names = {"c", "contentType"}, completer = ContentTypeOption.ContentTypeCompleter.class)
@Usage("content type of an operation")
@Man("The content type of an operation")
public @interface ContentTypeOption
{
   public static class ContentTypeCompleter implements Completer
   {
      @Override
      public Completion complete(ParameterDescriptor parameter, String prefix) throws Exception
      {
         ContentType[] cts = ContentType.values();
         //Map<String, Boolean> completions = new HashMap<String, Boolean>(cts.length);
         Completion.Builder completions = null;
         for (ContentType ct : cts)
         {
            String value = ct.getName();
            if (value.startsWith(prefix))
            {
               if (completions == null)
               {
                  completions = Completion.builder(prefix);
               }
               completions.add(value.substring(prefix.length()), true);
            }
         }

         return (completions == null) ? Completion.create() : completions.build();
      }
   }
}
