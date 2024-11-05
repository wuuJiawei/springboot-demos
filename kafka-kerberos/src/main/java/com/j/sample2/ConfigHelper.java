package com.j.sample2;

import org.apache.kafka.common.utils.ConfigUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHelper {

    private static final String TOPIC_CONFIG_KEY = "topic";
    private static final String CONFIG_DIR_CONFIG_KEY = "config-dir";
    private static final String SOCKET_HOST_KEY = "socket.host";
    private static final String SOCKET_PORT_KEY = "socket.port";

    /**
     * 获取配置的topic
     * @return topic
     */
    public static String getTopic() {
        return getProperty(TOPIC_CONFIG_KEY);
    }

    /**
     * 获取配置的目录
     * @return 目录
     */
    public static String getConfigDir() {
        String dir = getProperty(CONFIG_DIR_CONFIG_KEY);
        if (!dir.endsWith("/")) {
            dir += "/";
        }
        return dir;
    }

    public static int getSocketPort() {
        return Integer.parseInt(getProperty(SOCKET_PORT_KEY));
    }

    public static String getSocketHost() {
        return getProperty(SOCKET_HOST_KEY);
    }

    /**
     * 从配置文件中读取相关配置
     * @param key 配置的键名
     * @return 配置的值
     */
    public static String getProperty(String key) {
        return loadConfig().getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return loadConfig().getProperty(key, defaultValue);
    }

    /**
     * 加载配置文件
     * 优先从当前工作目录加载 config.properties
     * 如果加载不到，则从类路径加载
     * @return 配置信息
     */
    private static Properties loadConfig() {
        Properties props = new Properties();

        try (InputStream fileStream = new FileInputStream("./config.properties")) {
            props.load(fileStream);
        } catch (FileNotFoundException ignored) {
            // 当前目录下没有找到 config.properties，忽略异常，继续尝试从类路径加载
        } catch (IOException e) {
            throw new RuntimeException("Error loading config.properties from current directory", e);
        }

        if (props.isEmpty()) {
            try (InputStream resourceStream = ConfigUtils.class.getResourceAsStream("/config.properties")) {
                if (resourceStream == null) {
                    throw new RuntimeException("Unable to find config.properties on classpath.");
                }
                props.load(resourceStream);
            } catch (IOException e) {
                throw new RuntimeException("Error loading config.properties from classpath", e);
            }
        }

        return props;
    }

}
