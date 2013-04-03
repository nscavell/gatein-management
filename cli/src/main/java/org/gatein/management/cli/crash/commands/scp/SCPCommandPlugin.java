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

package org.gatein.management.cli.crash.commands.scp;

import org.apache.sshd.server.Command;
import org.crsh.cli.impl.descriptor.CommandDescriptorImpl;
import org.crsh.cli.impl.invocation.InvocationMatch;
import org.crsh.cli.impl.invocation.InvocationMatcher;
import org.crsh.cli.impl.invocation.Resolver;
import org.crsh.cli.impl.lang.CommandFactory;
import org.crsh.ssh.term.FailCommand;
import org.crsh.ssh.term.scp.CommandPlugin;
import org.crsh.ssh.term.scp.SCPAction;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 * @version $Revision$
 */
public class SCPCommandPlugin extends CommandPlugin
{
   @Override
   public Command createCommand(String command)
   {
      if (command.startsWith("scp "))
      {
         SCPAction action = parseScpCommand(command.substring(4));
         if (action == null) return new FailCommand("Unrecognized command " + command);

         if (Boolean.TRUE.equals(action.isSource()))
         {
            return new SourceCommand(action, getContext());
         }
         else if (Boolean.TRUE.equals(action.isSink()))
         {
            return new SinkCommand(action, getContext());
         }
      }

      return new FailCommand("Cannot execute command " + command);
   }

   private SCPAction parseScpCommand(String command)
   {
      try
      {
         SCPAction action = new SCPAction();
         CommandDescriptorImpl<SCPAction> descriptor = CommandFactory.DEFAULT.create(SCPAction.class);
         InvocationMatcher<SCPAction> analyzer = descriptor.invoker("main");
         InvocationMatch<SCPAction> match = analyzer.match(command);
         match.invoke(Resolver.EMPTY, action);

         return action;
      }
      catch (Exception e)
      {
         return null;
      }
   }
}
