package com.functions.util;

import com.microsoft.azure.functions.HttpRequestMessage;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.UploadContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FunctionsRequestContext implements RequestContext
{

    private final HttpRequestMessage requestMessage;

    public FunctionsRequestContext(HttpRequestMessage requestMessage){
        this.requestMessage = requestMessage;
    }
    /**
     * Retrieve the character encoding for the request.
     *
     * @return The character encoding for the request.
     */
    @Override
    public String getCharacterEncoding(){
        return "UTF-8";
    }

    /**
     * Retrieve the content type of the request.
     *
     * @return The content type of the request.
     */
    public String getContentType(){
        String contentType = (String) requestMessage.getHeaders().get("content-type");
        return contentType == null ? "multipart/form-data" : contentType;
    }

    /**
     * Retrieve the content length of the request.
     *
     * @return The content length of the request.
     * @deprecated 1.3 Use {@link UploadContext#contentLength()} instead
     */
    @Deprecated
    public int getContentLength() {
        String contentLength = (String) requestMessage.getHeaders().get("content-length");
        return null == contentLength ? 0 : Integer.parseInt(contentLength);
    }

    /**
     * Retrieve the input stream for the request.
     *
     * @return The input stream for the request.
     *
     * @throws IOException if a problem occurs.
     */
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(requestMessage.getBody().toString().getBytes());
    }

}
