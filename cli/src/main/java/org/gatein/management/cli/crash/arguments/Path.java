package org.gatein.management.cli.crash.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class Path
{
   public static final Path ROOT = new Path("/");

   public final String value;
   private final String[] segments;

   public Path(String value)
   {
      if (value == null) throw new NullPointerException("value cannot be null");
      this.segments = resolve(value);
      this.value = join(segments);
   }

   public boolean isAbsolute()
   {
      return value.length() > 0 && value.charAt(0) == '/';
   }

   public int getParentDirectives()
   {
      int index = 0;
      for (String s : segments)
      {
         if ("..".equals(s))
         {
            index++;
         }
      }

      return index;
   }

   public Path getSubPath(int start)
   {
      return getSubPath(start, segments.length);
   }

   public Path getSubPath(int start, int end)
   {
      return new Path(join(Arrays.copyOfRange(segments, start, end)));
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Path path1 = (Path) o;

      if (!value.equals(path1.value)) return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      return value.hashCode();
   }

   @Override
   public String toString()
   {
      return "Path[" + value + "]";
   }

   private static String[] resolve(String path)
   {
      if (!path.contains("/")) return new String[]{path};

      String[] split = path.split("/", -1); // We want to persevere leading and trailing /
      String[] array = new String[split.length];
      int ptr = 0;
      for (int i = 0; i < split.length; i++)
      {
         String s = split[i];
         if ("".equals(s) && ptr != 0 && i < split.length - 1) // Ignore multiple /'s i.e. //foo//bar = /foo/bar
         {
            continue;
         }
         else if (".".equals(s)) // Ignore current directory directive
         {
            continue;
         }
         else if ("..".equals(s))
         {
            if (ptr > 0)
            {
               String prev = array[ptr - 1];
               if (ptr == 1 && "".equals(prev)) // absolute URL, ignore parent directives (..) going above root /
               {
                  continue;
               }
               else if (!"..".equals(prev))
               {
                  ptr--;
                  array[ptr] = null;
                  continue;
               }
            }
         }

         array[ptr++] = s;

         if (ptr == 0 && !"".equals(array[ptr])) ptr++;
      }

      List<String> segments = new ArrayList<String>(array.length);
      for (String s : array)
      {
         if (s != null) segments.add(s);
      }

      return segments.toArray(new String[segments.size()]);
   }

   private static String join(String[] array)
   {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < array.length; i++)
      {
         if (array[i] != null)
         {
            sb.append(array[i]);
         }

         int next = i + 1;
         if (next < array.length && array[next] != null)
         {
            sb.append("/");
         }
      }

      String s = sb.toString();
      return (".".equals(s) ? "" : s);
   }
}
