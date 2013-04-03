package org.gatein.management.cli.crash.arguments;

import org.crsh.cli.Argument;
import org.crsh.cli.Usage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Argument(name = "path")
@Usage("management path")
public @interface PathArgument
{
}
