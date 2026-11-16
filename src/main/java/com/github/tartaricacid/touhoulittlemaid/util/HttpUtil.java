package com.github.tartaricacid.touhoulittlemaid.util;

import net.minecraft.network.chat.TranslatableComponent;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.util.ProgressListener;
import org.apache.commons.io.FileUtils;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import javax.annotation.Nullable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class HttpUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder()
            .setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER))
            .setNameFormat("TLM Downloader %d").build();
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(THREAD_FACTORY);
    private static final ListeningExecutorService DOWNLOAD_EXECUTOR = MoreExecutors.listeningDecorator(EXECUTOR_SERVICE);

    private HttpUtil() {
    }

    public static CompletableFuture<Void> downloadTo(File saveFile, URL packUrl, Map<String, String> requestProperties, int maxSize, @Nullable ProgressListener listener, Proxy proxy) {
        return CompletableFuture.runAsync(() -> {
            HttpURLConnection connection = null;
            java.nio.file.Path staging = null;
            try {
                if (listener != null) listener.progressStart(new TranslatableComponent("resourcepack.downloading"));
                connection = (HttpURLConnection) packUrl.openConnection(proxy);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(30000);
                requestProperties.forEach(connection::setRequestProperty);
                long size = connection.getContentLengthLong();
                if (maxSize > 0 && size > maxSize) throw new IOException("Download exceeds size limit");
                var destination = saveFile.toPath().toAbsolutePath();
                Files.createDirectories(destination.getParent());
                staging = Files.createTempFile(destination.getParent(), "download-", ".tmp");
                try (var input = connection.getInputStream(); var output = Files.newOutputStream(staging)) {
                    long total = 0;
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = input.read(buffer)) != -1) {
                        if (Thread.currentThread().isInterrupted()) throw new InterruptedIOException();
                        total += count;
                        if (maxSize > 0 && total > maxSize) throw new IOException("Download exceeds size limit");
                        output.write(buffer, 0, count);
                        if (size > 0 && listener != null) listener.progressStagePercentage((int) (100 * total / size));
                    }
                    if (size >= 0 && total != size) throw new IOException("Incomplete download");
                }
                Files.move(staging, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new java.util.concurrent.CompletionException(e);
            } finally {
                if (connection != null) connection.disconnect();
                if (staging != null) FileUtils.deleteQuietly(staging.toFile());
                if (listener != null) listener.stop();
            }
        }, DOWNLOAD_EXECUTOR);
    }
}
