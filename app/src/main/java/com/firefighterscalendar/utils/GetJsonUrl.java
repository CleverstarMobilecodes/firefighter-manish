package com.firefighterscalendar.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetJsonUrl {
    private File mBetaFile;

    public String getJSONResponseFromUrl(String strUrl, HashMap<String, String> mKeyField, HashMap<String, File> mKeyFile) {
        try {
            MultipartUtility multipart = new MultipartUtility(strUrl, "utf-8");
            if (mKeyField != null) {
                for (HashMap.Entry<String, String> entry : mKeyField.entrySet()) {
                    multipart.addFormField(entry.getKey().trim(), entry.getValue().trim());
                }
            }
            if (mKeyFile != null) {
                for (HashMap.Entry<String, File> entry : mKeyFile.entrySet()) {
                    multipart.addFilePart(entry.getKey().trim(), entry.getValue());
                }
            }
            List<String> response = multipart.finish();
            return response.toString();
        } catch (UnknownHostException ue) {
            Log.v("log_tag", "<< response UnknownHostException >>" + ue);
            return null;
        } catch (IOException ex) {
            Log.v("log_tag", "<< response IOException >>" + ex);
            return null;
        } catch (Exception e) {
            Log.v("log_tag", "<< response Exception >>" + e);
            return null;
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "utf-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "utf-8"));
        }

        return result.toString();
    }

    /**
     * This utility class provides an abstraction layer for sending multipart
     * HTTP POST requests to a web server.
     *
     * @author www.codejava.net
     */
    public class MultipartUtility {
        private final String boundary;
        private static final String LINE_FEED = "\r\n";
        private HttpURLConnection httpConn;
        private String charset;
        private OutputStream outputStream;
        private PrintWriter writer;

        /**
         * This constructor initializes a new HTTP POST request with content
         * type is set to multipart/form-data
         *
         * @param requestURL
         * @param charset
         * @throws IOException
         */
        public MultipartUtility(String requestURL, String charset) throws IOException {
            this.charset = charset;

            // creates a unique boundary based on time stamp
            boundary = "===" + System.currentTimeMillis() + "===";

            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
            httpConn.setRequestProperty("Test", "Bonjour");
            outputStream = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
        }

        /**
         * Adds a form field to the request
         *
         * @param name  field name
         * @param value field value
         */
        public void addFormField(String name, String value) {
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
            writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(value).append(LINE_FEED);
            writer.toString().trim();
            writer.flush();
        }

        /**
         * Adds a upload file section to the request
         *
         * @param fieldName  name attribute in <input type="file" name="..." />
         * @param uploadFile a File to be uploaded
         * @throws IOException
         */
        public void addFilePart(String fieldName, File uploadFile) throws IOException {
            String fileName = uploadFile.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            writer.append(LINE_FEED);
            writer.flush();
        }

        /**
         * Adds a header field to the request.
         *
         * @param name  - name of the header field
         * @param value - value of the header field
         */
        public void addHeaderField(String name, String value) {
            writer.append(name + ": " + value).append(LINE_FEED);
            writer.flush();
        }

        /**
         * Completes the request and receives response from the server.
         *
         * @return a list of Strings as response in case the server returned
         * status OK, otherwise an exception is thrown.
         * @throws IOException
         */
        public List<String> finish() throws IOException {
            List<String> response = new ArrayList<String>();

            //writer.append(LINE_FEED)
            writer.append("--" + boundary + "--");
            writer.toString().replaceAll("\\r\\n", "");
            writer.close();

            // checks server's status code first
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    response.add(line);
                }
                reader.close();
                httpConn.disconnect();
            } else {
                throw new IOException("Server returned non-OK status: " + status);
            }

            return response;
        }
    }

    public File imgurltoFile(String imgurl) {
        try {
            File rootSdDirectory = Environment.getExternalStorageDirectory();
            mBetaFile = new File(rootSdDirectory, "BetaFile.jpg");
            if (mBetaFile.exists()) {
                mBetaFile.delete();
            }
            mBetaFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(mBetaFile);
            URL url = new URL(imgurl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();
            InputStream in = connection.getInputStream();

            byte[] buffer = new byte[1024];
            int size = 0;
            while ((size = in.read(buffer)) > 0) {
                fos.write(buffer, 0, size);
            }
            fos.close();
            return mBetaFile;
        } catch (Exception e) {
            System.out.print(e);
        }
        return mBetaFile;
    }
}
