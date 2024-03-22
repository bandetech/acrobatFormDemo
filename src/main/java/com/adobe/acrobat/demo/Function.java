package com.adobe.acrobat.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
       
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Check request body
        if(!request.getBody().isPresent()){
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Document not found.")
                .build();
        } else {

            final String body = request.getBody().get();
            CheckSheet sheet = new CheckSheet();
            try{
                // Parse Form XML data and store CheckSheet object.
                convertXmlDataToCheckSheet(body, sheet);
                System.out.println("Sheet Converted...");
                InputStream inputStream = Function.class.getResourceAsStream("/Confirmed.pdf");

                byte[] pdfContent = inputStream.readAllBytes();
                System.out.println("Read Confirm.pdf...");
                
                return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"Confirmed.pdf\"")
                .body(pdfContent)
                .build();
            }catch(Exception ex){
                System.out.println("Error while parsing xml string :"+ex.getMessage());
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to load the PDF file: " + ex.getMessage())
                .build();
            }
            
        }
    }

    private static void convertXmlDataToCheckSheet(String xmlStr, CheckSheet sheet) throws JsonMappingException, JsonProcessingException{
        XmlMapper xmlMapper = new XmlMapper();
        sheet = xmlMapper.readValue(xmlStr, CheckSheet.class);
        System.out.println(sheet.getNo10().toString());
    }
}
