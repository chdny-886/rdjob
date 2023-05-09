package com.rdjob.core.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/26 18:14
 */
@Slf4j
public class FileUtil {
    private static final int MAX_TIMEOUT = 30000; //超时时间
    private static final int MAX_TOTAL = 10; //最大连接数
    private static final int ROUTE_MAX_TOTAL = 3; //每个路由基础的连接数
    private static final int MAX_RETRY = 5; //重试次数
    private static PoolingHttpClientConnectionManager connMgr; //连接池
    private static HttpRequestRetryHandler retryHandler; //重试机制

    static {
        cfgPoolMgr();
        cfgRetryHandler();
    }

    /**
     * @return
     * @description 连接池配置
     */
    private static void cfgPoolMgr() {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainsf)
                .register("https", sslsf)
                .build();

        //连接池管理器
        connMgr = new PoolingHttpClientConnectionManager(registry);
        //最大连接数
        connMgr.setMaxTotal(MAX_TOTAL);
        //每个路由基础的连接数
        connMgr.setDefaultMaxPerRoute(ROUTE_MAX_TOTAL);
    }

    /**
     * @description 设置重试机制
     */
    private static void cfgRetryHandler() {
        retryHandler = (e, excCount, ctx) -> {
            //超过最大重试次数，就放弃
            if (excCount > MAX_RETRY) {
                return false;
            }
            //服务器丢掉了链接，就重试
            if (e instanceof NoHttpResponseException) {
                return true;
            }
            //不重试SSL握手异常
            if (e instanceof SSLHandshakeException) {
                return false;
            }
            //中断
            if (e instanceof InterruptedIOException) {
                return false;
            }
            //目标服务器不可达
            if (e instanceof UnknownHostException) {
                return false;
            }
            //连接超时
            if (e instanceof ConnectTimeoutException) {
                return false;
            }
            //SSL异常
            if (e instanceof SSLException) {
                return false;
            }

            HttpClientContext clientCtx = HttpClientContext.adapt(ctx);
            HttpRequest req = clientCtx.getRequest();
            //如果是幂等请求，就再次尝试
            if (!(req instanceof HttpEntityEnclosingRequest)) {
                return true;
            }
            return false;
        };
    }

    public static MultipartFile fileToMultipartFile(File file) {
        String fieldName = "file";
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem(fieldName, "multipart/form-data", true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new CommonsMultipartFile(item);
    }

    @SneakyThrows
    public static File bytes2TempFile(byte[] byteArray, String suffix) {
        InputStream in = new ByteArrayInputStream(byteArray);
        String tempFileName = System.currentTimeMillis() + "";
        FileOutputStream fos = null;
        if (StringUtils.isEmpty(suffix)) {
            suffix = ".tmp";
        } else {
            String[] str = suffix.split("\\.");
            if (str.length == 2) {
                String name = str[0];
                if (!StringUtils.isEmpty(name)) {
                    tempFileName = str[0];
                }
                suffix = "." + str[1];
            } else {
                suffix = "." + str[0];
            }
        }
        try {
            File tempFile = createTempFile(tempFileName, suffix);
            fos = new FileOutputStream(tempFile);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            return tempFile;
        } catch (Exception e) {
            log.error("bytes2TempFile:", e);
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error("bytes2TempFile:", e);
                }
            }
        }
        throw new IOException("创建失败");
    }

    public static String getFileSuffix(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    public static File downloadFile(String url) {
        return downloadFile(RequestType.GET, url, null, null);
    }

    public static File downloadFile(String url, String fileName) {
        return downloadFile(RequestType.GET, url, fileName, null);
    }

    @SneakyThrows
    public static File downloadFile(RequestType reqType, String url, String suffix, Map<String, String> headers) {
        //添加参数 参数是json字符串
        HttpRequestBase reqBase = reqType.getHttpType(url);
        CloseableHttpClient httpClient = getHttpClient();
        //设置请求url
        config(reqBase);

        //设置请求头
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                reqBase.setHeader(entry.getKey(), entry.getValue());
            }
        }
        //响应对象
        CloseableHttpResponse res = null;
        try {
            //执行请求
            res = httpClient.execute(reqBase);

            //获取请求响应对象和响应entity
            HttpEntity httpEntity = res.getEntity();
            if (httpEntity != null) {
                byte[] bytes = EntityUtils.toByteArray(httpEntity);
                if (StringUtils.isEmpty(suffix)) {
                    suffix = getFileSuffix(url);
                }
                return FileUtil.bytes2TempFile(bytes, suffix);
                //log.info("下载成功 {}", tempFile.getAbsolutePath());
            }
        } catch (NoHttpResponseException e) {
            throw new DefineException("服务器丢失了", e);
        } catch (SSLHandshakeException e) {
            String msg = MessageFormat.format("SSL握手异常", e);
            DefineException ex = new DefineException(msg);
            ex.initCause(e);
            throw ex;
        } catch (UnknownHostException e) {
            DefineException ex = new DefineException("服务器找不到", e);
            ex.initCause(e);
            throw ex;
        } catch (ConnectTimeoutException e) {
            DefineException ex = new DefineException("连接超时", e);
            ex.initCause(e);
            throw ex;
        } catch (SSLException e) {
            DefineException ex = new DefineException("SSL异常", e);
            ex.initCause(e);
            throw ex;
        } catch (ClientProtocolException e) {
            DefineException ex = new DefineException("请求头异常", e);
            ex.initCause(e);
            throw ex;
        } catch (IOException e) {
            DefineException ex = new DefineException("网络请求失败", e);
            ex.initCause(e);
            throw ex;
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (IOException e) {
                    DefineException ex = new DefineException("--->>关闭请求响应失败", e);
                    ex.initCause(e);
                    throw ex;
                }
            }
        }
        throw new IOException();
    }

    private static CloseableHttpClient getHttpClient() {
        return HttpClients.custom()
                .setConnectionManager(connMgr)
                .setRetryHandler(retryHandler)
                .build();
    }


    /**
     * @param httpReqBase
     * @description 请求头和超时时间配置
     */
    private static void config(HttpRequestBase httpReqBase) {
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(MAX_TIMEOUT)
                .setConnectTimeout(MAX_TIMEOUT)
                .setSocketTimeout(MAX_TIMEOUT)
                .build();
        httpReqBase.setConfig(requestConfig);
    }


    public static File createTempFile(String fileName, String suffix) {
        if (StringUtils.isEmpty(fileName)) {
            fileName = System.currentTimeMillis() + "";
        }
        if (StringUtils.isEmpty(suffix)) {
            suffix = ".tmp";
        }
        File tmpdir = new File(System.getProperty("java.io.tmpdir"));
        return new File(tmpdir, fileName + suffix);
    }
}
