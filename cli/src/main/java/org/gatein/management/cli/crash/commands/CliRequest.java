package org.gatein.management.cli.crash.commands;

import org.gatein.management.api.ContentType;
import org.gatein.management.api.PathAddress;
import org.gatein.management.api.controller.ExternalManagedRequest;
import org.gatein.management.api.controller.ManagedRequest;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class CliRequest implements ExternalManagedRequest
{
   private final String  user;
   private final ManagedRequest request;
   private Locale locale;

   public CliRequest(final String user, ManagedRequest request)
   {
      this.user =user;
      this.request = request;
      this.locale = Locale.getDefault();
   }

   @Override
   public String getRemoteUser()
   {
      return user;
   }

   @Override
   public Object getRequest()
   {
      return null;
   }

   @Override
   public RoleResolver getRoleResolver()
   {
      return null;
   }

   @Override
   public String getOperationName()
   {
      return request.getOperationName();
   }

   @Override
   public PathAddress getAddress()
   {
      return request.getAddress();
   }

   @Override
   public Map<String, List<String>> getAttributes()
   {
      return request.getAttributes();
   }

   @Override
   public Locale getLocale()
   {
      return locale;
   }

   @Override
   public void setLocale(Locale locale)
   {
      this.locale = locale;
   }

   @Override
   public InputStream getDataStream()
   {
      return request.getDataStream();
   }

   @Override
   public ContentType getContentType()
   {
      return request.getContentType();
   }
}
