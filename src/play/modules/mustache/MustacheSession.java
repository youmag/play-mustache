package play.modules.mustache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import play.Logger;
import play.vfs.VirtualFile;

import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheCompiler;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.Scope;
import com.sampullara.util.FutureWriter;

public class MustacheSession {
    
    private ThreadLocal<MustacheCompiler> compiler_ = new ThreadLocal<MustacheCompiler>();
    private String root_ = null;
    private Map<String, Mustache> loaded_ = new HashMap<String, Mustache>();
    private Map<String, String> raw_ = new HashMap<String, String>();
    
    public MustacheCompiler compiler() {
        if(compiler_.get() == null) compiler_.set(new MustacheCompiler(new File(root_)));
        return compiler_.get();
    }

    public MustacheSession(String directory){
        this.root_ = directory;
    }
    
    public Map<String, String> getRawTemplates(){
        return raw_;
    }
    
    public void addFromString(String key, String tmpl) throws MustacheException {
        loaded_.put(key, compiler().parse(tmpl));
        raw_.put(key, tmpl);
    }
    
    public void addFromFile(String key, String path) throws MustacheException, IOException {
        String tmpl = readFile(path);
        addFromString(key, tmpl);
    }
    
    public String toHtml(String key, Object context) throws MustacheException, IOException {
        if(!loaded_.containsKey(key)){
            addFromFile(key, root_ + key);
        }
        Mustache m = loaded_.get(key);
        StringWriter sw = new StringWriter();
        FutureWriter writer = new FutureWriter(sw);
        m.execute(writer, new Scope(context));
        writer.flush();
        return sw.toString();
    }
    
    public void clean(String key) throws MustacheException, IOException{
    	Logger.trace("Clean %s", key);
    	
    	// remove
    	loaded_.remove(key);
    	raw_.remove(key);

    	// add
    	addFromFile(key, root_ + "/" + key);
    }
    
    public void clean(){
    	loaded_.clear();
    	raw_.clear();
    }
    
    private String readFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
          FileChannel fc = stream.getChannel();
          MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
          /* Instead of using default, pass in a decoder. */
          return Charset.defaultCharset().decode(bb).toString();
        }
        finally {
          stream.close();
        }
    }

    public void loadFileSystemTemplates(String root)  throws MustacheException, IOException {
        File dir = VirtualFile.open(root).getRealFile();
        this.addFilesRecursively(dir, root);
    }

    private void addFilesRecursively(File file, String root) throws MustacheException, IOException {
        File[] children = file.listFiles();
        if(children != null){
            for(File child : children){
                if(child.isDirectory()){
                    addFilesRecursively(child, root);
                }else{
                    String path = child.getAbsolutePath();
                    String key = path.replace(root, "");
                    this.addFromFile(key, path);
                }
            }
        }
    }
    
}
