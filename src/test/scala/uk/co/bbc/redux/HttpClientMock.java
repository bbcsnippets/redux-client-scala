package org.apache.commons.httpclient;

import org.apache.commons.httpclient.*;
import java.io.ByteArrayInputStream;

public class HttpClientMock extends HttpClient {
   private int expectedResponseStatus;
   private String expectedResponseBody;

   public HttpClientMock (int responseStatus, String responseBody) {
      this.expectedResponseStatus = responseStatus;
      this.expectedResponseBody = responseBody;
   }

   @Override
   public int executeMethod(HttpMethod method) {
      try {
        ((HttpMethodBase)method).setResponseStream(new ByteArrayInputStream(expectedResponseBody.getBytes("UTF-8")));
        return expectedResponseStatus;
      } catch (Exception e) {
        throw new RuntimeException();
      }
   }

}