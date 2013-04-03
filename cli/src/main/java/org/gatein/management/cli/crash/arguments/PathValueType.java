package org.gatein.management.cli.crash.arguments;

import org.crsh.cli.type.ValueType;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class PathValueType extends ValueType<Path>
{
   public PathValueType()
   {
      super(Path.class, PathCompleter.class);
   }

   @Override
   public <S extends Path> S parse(Class<S> type, String path) throws Exception
   {
      return type.cast(new Path(path));
   }
}
