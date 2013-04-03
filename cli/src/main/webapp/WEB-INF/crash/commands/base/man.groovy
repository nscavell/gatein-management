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

import org.crsh.cli.Required
import org.crsh.command.DescriptionFormat
import org.crsh.cli.Usage
import org.crsh.cli.Command
import org.crsh.cli.Argument;

class man {
  @Usage("format and display the on-line manual pages")
  @Command
  Object main(@Usage("the command") @Argument @Required String command) {
    def cmd = crash.getCommand(command);
    if (cmd != null) {
      return  cmd.describe(unmatched, DescriptionFormat.MAN);
    } else {
      return "Command $command not found";
    }
  }
}