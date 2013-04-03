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

package crash.commands.base

import org.crsh.cli.completers.EnumCompleter
import org.crsh.command.CRaSHCommand
import org.crsh.cli.Usage
import org.crsh.cli.Command
import org.crsh.command.InvocationContext
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Retention
import org.crsh.cli.Man
import org.crsh.cli.Option

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
@Usage("memory management commands")
class memory extends CRaSHCommand {

  @Usage("show free memory")
  @Command
  public void free(
    InvocationContext<Map.Entry> context,
    @UnitOpt Unit unit,
    @DecimalOpt Integer decimal) {
    if (unit == null) {
      unit = Unit.B;
    }
    context.writer.println(unit.compute(Runtime.getRuntime().freeMemory(), decimal) + unit.human)
  }

  @Usage("show total memory")
  @Command
  public void total(
    InvocationContext<Map.Entry> context,
    @UnitOpt Unit unit,
    @DecimalOpt Integer decimal) {
    if (unit == null) {
      unit = Unit.B;
    }
    context.writer.println(unit.compute(Runtime.getRuntime().totalMemory(), decimal) + unit.human)
  }

}

enum Unit { B(1, "b"), K(1024, "Kb"), M(1024 * 1024, "Mb"), G(1024 * 1024 * 1024, "Gb")
  final long unit;
  final String human;

  Unit(long unit, String human) {
    this.unit = unit;
    this.human = human;
  }

  public String compute(long space, Integer decimal) {
    if (decimal == null) {
      decimal = 0
    }
    return new BigDecimal(space / unit).setScale(decimal, BigDecimal.ROUND_HALF_UP).toPlainString();
  }

  public String getHuman() {
    return this.human;
  }
}

@Retention(RetentionPolicy.RUNTIME)
@Usage("the unit of the memory space size")
@Man("The unit of the memory space size {(B)yte, (O)ctet, (M)egaOctet, (G)igaOctet}")
@Option(names=["u","unit"],completer=EnumCompleter.class)
@interface UnitOpt { }

@Retention(RetentionPolicy.RUNTIME)
@Usage("number of decimal")
@Man("The number of decimal (default 0)")
@Option(names=["d","decimal"])
@interface DecimalOpt { }
