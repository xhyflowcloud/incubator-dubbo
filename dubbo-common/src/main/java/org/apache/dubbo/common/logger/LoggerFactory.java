package org.apache.dubbo.common.logger;

import org.apache.dubbo.common.logger.slf4j.Slf4jLoggerAdapter;
import org.apache.dubbo.common.logger.support.FailsafeLogger;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Logger factory
 */
public class LoggerFactory {

    private static final ConcurrentHashMap<String, FailsafeLogger> LOGGERS = new ConcurrentHashMap<String, FailsafeLogger>();
    private static volatile LoggerAdapter LOGGER_ADAPTER;

    //search common-used logging framework
    static {
        String logger = System.getProperty("dubbo.application.logger");
        if("slf4j".equals(logger)){
            setLoggerAdapter(new Slf4jLoggerAdapter());
        }else if ("jcl".equals(logger)) {
            setLoggerAdapter(new JclLoggerAdapter());
        } else if ("log4j".equals(logger)) {
            setLoggerAdapter(new Log4jLoggerAdapter());
        } else if ("jdk".equals(logger)) {
            setLoggerAdapter(new JdkLoggerAdapter());
        } else {
            try {
                setLoggerAdapter(new Log4jLoggerAdapter());
            } catch (Throwable e1) {
                try {
                    setLoggerAdapter(new Slf4jLoggerAdapter());
                } catch (Throwable e2) {
                    try {
                        setLoggerAdapter(new JclLoggerAdapter());
                    } catch (Throwable e3) {
                        setLoggerAdapter(new JdkLoggerAdapter());
                    }
                }
            }
        }
    }

    private LoggerFactory(){}

    /**
     * Set logger provider
     *
     * @param loggerAdapter logger provider
     */
    public static void setLoggerAdapter(LoggerAdapter loggerAdapter){
       if(loggerAdapter != null){
           Logger logger = loggerAdapter.getLogger(LoggerFactory.class.getName());
           logger.info("using logger: " + loggerAdapter.getClass().getName());
           LoggerFactory.LOGGER_ADAPTER = loggerAdapter;
           for(Map.Entry<String, FailsafeLogger> entry : LOGGERS.entrySet()){
               entry.getValue().setLogger(LOGGER_ADAPTER.getLogger(entry.getKey()));
           }
       }
    }

    public static void setLoggerAdapter(String loggerAdapter) {
        if (loggerAdapter != null && loggerAdapter.length() > 0) {
            setLoggerAdapter(ExtensionLoader.getExtensionLoader(LoggerAdapter.class).getExtension(loggerAdapter));
        }
    }

    /**
     * Get logger provider
     *
     * @param key the returned logger will be named after clazz
     * @return logger
     */
    public static Logger getLogger(Class<?> key){
        FailsafeLogger logger = LOGGERS.get(key.getName());
        if(logger == null){
            LOGGER_ADAPTER.putIfAbsent(key.getName(), new FailsafeLogger(LOGGER_ADAPTER.getLogger(key)));
            logger = LOGGERS.get(key.getName());
        }
        return logger;
    }

    /**
     * Get logging level
     *
     * @return logging level
     */
    public static Level getLevel() {
        return LOGGER_ADAPTER.getLevel();
    }

    /**
     * Set the current logging level
     *
     * @param level logging level
     */
    public static void setLevel(Level level) {
        LOGGER_ADAPTER.setLevel(level);
    }

    /**
     * Get the current logging file
     *
     * @return current logging file
     */
    public static File getFile() {
        return LOGGER_ADAPTER.getFile();
    }
}
