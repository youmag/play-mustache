package play.modules.mustache;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.templates.Template;
import play.vfs.VirtualFile;

public class MustachePlugin extends PlayPlugin {
    
    private static MustacheSession session_;
    private static String root;
    private boolean hotReload;
    
    private Map<String, Long> templates = new HashMap<String, Long>();
    
    
    public static MustacheSession session(){
        return session_;
    }
    
    @Override
    public void onConfigurationRead(){
        root = Play.configuration.containsKey("mustache.dir") ? Play.configuration.getProperty("mustache.dir") : Play.applicationPath+"/app/views/mustaches";
        hotReload = Boolean.getBoolean(Play.configuration.getProperty("mustache.hotreload", "true"));
        
        session_ = new MustacheSession(root);
        try{
            session_.loadFileSystemTemplates(root);
        }catch(Exception e){
            Logger.error("Error initializing Mustache module: "+e.getMessage());
        }
        
        loadTemplateRecursively("");
        
        Logger.debug("Templates load: %s", templates.toString());
        
        Logger.info("Mustache module initialized");
    }
        
    @Override
    public Template loadTemplate(VirtualFile file) {
    	
    	if(session_ != null && !hotReload){
    		
    		checkRecursively(root, "");
    		Logger.trace("Templates load: %s", templates.toString());
    	}
    	
    	return null;
    }
    
    private void checkRecursively(String root, String path){
    	VirtualFile vRoot = VirtualFile.open(root + "/" + path);
    	
    	List<VirtualFile> children = vRoot.list();
    	for(VirtualFile child : children){
    		if(child.isDirectory()){
    			checkRecursively(vRoot.getRealFile().getAbsolutePath(), "/" + child.getName());
    		}else{
    			String name = path + "/" + child.getName();
    			if(templates.containsKey(name)){
    				if(templates.get(name) < child.lastModified()){
    					reload(name);
    					templates.put(name, child.lastModified());
    				}
    			}else{
					reload(name);
					templates.put(name, child.lastModified());    				
    			}
    		}
    	}
    }
    
    private void loadTemplateRecursively(String file){
    	VirtualFile vRoot = VirtualFile.open(root + "/" + file);
    	List<VirtualFile> children = vRoot.list();
    	for(VirtualFile child : children){
    		if(child.isDirectory()){
    			loadTemplateRecursively(child.getName());
    		}else{
    			String name = child.getRealFile().getAbsolutePath();
    			name = name.substring(root.length());
    			templates.put(name, child.lastModified());
    		}
    	}
    }
    
    private void reload(String key){
    	if(Play.mode.isDev()){
    		Logger.debug("Reload %s mustache template", key);
    		try {
				session_.clean(key);
			} catch (Exception e) {
				Logger.error("Error reloading Mustache module template %s : %s", key, e.getMessage());
			}
    	}
    }
    
    private void reloadAll(){
    	
    	Logger.info("Reload all mustache template");
    	
		if(Play.mode.isDev()){
			// Reloading template
			session_.clean();
			try{
				session_.loadFileSystemTemplates(root);
			}catch(Exception e){
				Logger.error("Error initializing Mustache module: "+e.getMessage());
			}
		}
    }    
}
