package com.drive.quickstart.demoapi.quickstart;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @description:
 * @author: JDev
 * @create: 2018-12-21 13:06
 **/
public class DriveQuickstart {

    //调用Google Drive API国内被墙了，需要设置代理
    private static String PROXY_IP = "10.2.1.240";
    private static int PROXY_PORT = 8123;

    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /** Authorizes the installed application to access user's protected data. */
    private static Credential authorize(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
        // load client secrets
        /*GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(DriveQuickstart.class.getResourceAsStream("\\client_secrets.json")));*/

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(DriveQuickstart.class
                        .getResourceAsStream("/client_secret_915033595285-am5d1n4dkn673gojcqkksu0jibjthfke.apps.googleusercontent.com.json")));

        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,Collections.singleton(DriveScopes.DRIVE))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }


    public static void main(String... args) throws Exception {
        // Build a new authorized API client service.
        //final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        //设置代理
        Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(PROXY_IP,PROXY_PORT));
        final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport.Builder()
                //设置日本代理服务器，导入日本环境时下面 .setProxy(proxy) 需注释掉
                .setProxy(proxy)
                .trustCertificates(GoogleUtils.getCertificateTrustStore()).build();

        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, authorize(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // 显示所有文件中的10个
        FileList result = service.files().list()
                //.setPageSize(10)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }

        //上传新文件
        List<String> parentPath = Arrays.asList("1d7jMphWcnfqDbObYhK64-WbpavnjKknZ");
        File fileMetadata = new File();
        fileMetadata.setName("upload0107.xlsx");
        fileMetadata.setParents(parentPath);

        java.io.File filePath = new java.io.File("files/upload0107.xlsx");

        FileContent mediaContent = new FileContent("application/vnd.ms-excel;charset=UTF-8", filePath);
        File file = service.files().create(fileMetadata, mediaContent)
                .setFields("id")
                //.setFields("name")
                .execute();

        System.out.println("新上传 File ID: " + file.getId());
        //System.out.println("新上传 File ID: " + file.getName());

    }

}
