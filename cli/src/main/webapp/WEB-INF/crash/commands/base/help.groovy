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

import org.crsh.command.DescriptionFormat
import org.crsh.cli.Usage
import org.crsh.cli.Command
import org.crsh.text.ui.UIBuilder

class help
{

  /** . */
  private static final String TAB = "  ";

  @Usage("provides basic help")
  @Command
  Object main() {
    def names = [];
    def descs = [];
    int len = 0;
    crash.context.listResourceId(org.crsh.plugin.ResourceKind.COMMAND).each() {
      String name ->
      try {
        def cmd = crash.getCommand(name);
        if (cmd != null) {
          def desc = cmd.describe(name, DescriptionFormat.DESCRIBE) ?: "";
          names.add(name);
          descs.add(desc);
          len = Math.max(len, name.length());
        }
      } catch (Exception ignore) {
        throw new org.crsh.command.ScriptException(ignore);
      }
    }
    
    def builder = new UIBuilder()

    //
    builder.label("Try one of these commands with the -h or --help switch:\n");

    builder.table(rightCellPadding: 1) {
      row(bold: true, fg: black, bg: white) {
        label("NAME"); label("DESCRIPTION")
      }
      for (int i = 0;i < names.size();i++) {
        row() {
            label(foreground: red, names[i]); label(descs[i])
        }
      }
    }
    
    return builder;
  }
}