package xyz.easyboot.classloader;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author wujiawei
 * @see
 * @since 2021/2/19 下午2:51
 */
public class HotClassLoader {
    
    private static HotClassLoader instance = null;
    private CustomClassLoader classLoader;
    private String classpath;
    
    private HotClassLoader(String classpath) {
        this.classpath = classpath;
    }
    
    public static HotClassLoader getInstance(String classpath) {
        if (instance == null) {
            synchronized (HotClassLoader.class) {
                if (instance == null) {
                    instance = new HotClassLoader(classpath);
                }
            }
        }
        return instance;
    }
    
    /**
     * 自定义类加载引擎
     *
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        synchronized (this) {
            classLoader = new CustomClassLoader(this.classpath);
            Class<?> findClass = classLoader.findClass(name);
            if (findClass != null) {
                return findClass;
            }
        }
        return classLoader.loadClass(name);
    }
    
    /**
     * 自定义类加载器
     */
    private static class CustomClassLoader extends ClassLoader{
        
        private String classpath = null;
        
        public CustomClassLoader(String classpath) {
            super(ClassLoader.getSystemClassLoader());
            this.classpath = classpath;
        }
    
        /**
         * Finds the class with the specified <a href="#name">binary name</a>. This method should be overridden by class
         * loader implementations that follow the delegation model for loading classes, and will be invoked by the
         * {@link #loadClass <tt>loadClass</tt>} method after checking the parent class loader for the requested class.
         * The default implementation throws a <tt>ClassNotFoundException</tt>.
         *
         * @param name The <a href="#name">binary name</a> of the class
         * @return The resulting <tt>Class</tt> object
         * @throws ClassNotFoundException If the class could not be found
         * @since 1.2
         */
        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] classByte = null;
            classByte = readClassFile(name);
    
            if (classByte == null || classByte.length == 0) {
                throw new ClassNotFoundException("ClassNotFound : " + name);
            }
    
            return this.defineClass(name, classByte, 0, classByte.length);
        }
    
        /**
         * 读取类文件
         *
         * @param name
         * @return
         */
        private byte[] readClassFile(String name) throws ClassNotFoundException {
            String fileName = name.replace(".", "/").concat(".class");
            
            File classFile = new File(this.classpath, fileName);
            if (!classFile.exists() || classFile.isDirectory()) {
                throw new ClassNotFoundException("ClassNotFound: ".concat(name));
            }
            FileInputStream fis = null;
    
            try {
                fis = new FileInputStream(classFile);
                int available = fis.available();
                int bufferSize = Math.max(Math.min(2014, available), 256);
                ByteBuffer buf = ByteBuffer.allocate(bufferSize);
                
                byte[] bytes = null;
                
                FileChannel channel = fis.getChannel();
                while (channel.read(buf) > 0) {
                    buf.flip();
                    bytes = translateArray(bytes, buf);
                    buf.clear();
                }
                return bytes;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeIOQuietly(fis);
            }
            return null;
        }
        
        private byte[] translateArray(byte[] bytes, ByteBuffer buf) {
            if (bytes == null) {
                bytes = new byte[0];
            }
            byte[] _array = null;
            if (buf.hasArray()) {
                _array = new byte[buf.limit()];
                System.arraycopy(buf.array(), 0, _array, 0, _array.length);
            } else {
                _array = new byte[0];
            }
            
            byte[] _implyArray = new byte[bytes.length + _array.length];
            System.arraycopy(bytes, 0, _implyArray, 0, bytes.length);
            System.arraycopy(_array, 0, _implyArray, bytes.length,
                    _array.length);
            bytes = _implyArray;
            return bytes;
        }
    
        /**
         * 关闭io流
         *
         * @param closeable
         */
        public static void closeIOQuietly(Closeable closeable) {
        
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
