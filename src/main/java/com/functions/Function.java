package com.functions;

import com.functions.util.FunctionsRequestContext;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Azure Functions with HTTP Trigger on a Form upload.
 */
public class Function
{
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
        @HttpTrigger(
            name = "req",
            methods = {HttpMethod.GET, HttpMethod.POST},
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context) throws FileUploadException, IOException
    {
        context.getLogger().info("Java HTTP trigger processed a request.");
        System.out.println("\nHeaders:");
        request.getHeaders().entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        });

        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        File repo = new File(".");
        factory.setRepository(repo);

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        // My custom wrapper for Azure functions HttpRequestMessage
        FunctionsRequestContext requestContext = new FunctionsRequestContext(request);

        // Parse the request
        List<FileItem> items = new ArrayList<>();
        try
        {
            items = upload.parseRequest(requestContext);
        }
        catch (FileUploadException e)
        {
            System.out.println("Caught a file upload exception");
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            Arrays.stream(e.getStackTrace()).iterator().forEachRemaining(el -> {
                System.out.println(el);
            });
            System.out.println(e);
        }

        items.forEach(item -> {
            if (item.isFormField())
            {
                processFormField(item);
            }
            else
            {
                processUploadedFile(item);
            }
        });

        List<String> formFieldValues = items.stream().filter(item -> item.isFormField())
            .map(item -> item.getString()).collect(Collectors.toList());

        return request.createResponseBuilder(HttpStatus.OK).body("You input - " + formFieldValues + "\n And you successfully uploaded!").build();
    }

    public void processFormField(FileItem item){
        System.out.println("Processed a form field with getFieldName(): <" + item.getFieldName() + ">");
        System.out.println("getString(): <" + item.getString() + ">");
    }

    public void processUploadedFile(FileItem item){
        System.out.println("Processed a uploaded file with getName(): <" + item.getName() + ">");
        System.out.println("get().length: <" + item.get().length + ">");
    }
}
