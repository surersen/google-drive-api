package com.drive.quickstart.demoapi.quickstart;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.List;

/**
 * @description:
 * @author: JDev
 * @create: 2019-01-07 08:29
 **/
@Controller
@RequestMapping("/excel")
public class DownloadController {

    //调用Google Drive API国内被墙了，需要设置代理
    private static String PROXY_IP = "10.2.1.240";
    private static int PROXY_PORT = 8123;

    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";


    @RequestMapping("/download")
    @ResponseBody
    public void fileDownload(HttpServletResponse response) throws Exception {

        //设置代理
        Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(PROXY_IP,PROXY_PORT));
        final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport.Builder()
                //设置日本代理服务器，导入日本环境时下面 .setProxy(proxy) 需注释掉
                .setProxy(proxy)
                .trustCertificates(GoogleUtils.getCertificateTrustStore()).build();

        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, authorize(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        //文件下载
        String fileId = "1ejswlcpwQcdIhI-1LFbhUjuqNZM-SJwo";
        OutputStream outputStream = new ByteArrayOutputStream();

        String filename = "upload0107.xlsx";
        HttpResponse httpResponse = service.files().get(fileId).executeMedia();
        InputStream  inputStream = httpResponse.getContent();
        BufferedInputStream in = new BufferedInputStream(inputStream);

        OutputStream os = response.getOutputStream();
        response.reset();
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(filename.replaceAll(" ", "").getBytes("utf-8"), "iso8859-1"));
        response.setContentType("application/octet-stream");
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len = in.read(buffer)) != -1) {
            os.write(buffer, 0, len);
            os.flush();
        }
        os.close();
        in.close();

    }

    /** Authorizes the installed application to access user's protected data. */
    public  Credential authorize(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
        // load client secrets
        /*GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(DriveQuickstart.class.getResourceAsStream("\\client_secrets.json")));*/

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(DriveQuickstart.class
                        .getResourceAsStream("/client_secret_915033595285-am5d1n4dkn673gojcqkksu0jibjthfke.apps.googleusercontent.com.json")));

        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections.singleton(DriveScopes.DRIVE))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

}
