package play.modules.mustache;


import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.templates.Template;
import play.vfs.VirtualFile;

public class MustachePlugin extends PlayPlugin {
    
    private static MustacheSession session_;
    private static String root;
    
    
    public static MustacheSession session(){
        return session_;
    }
    
    @Override
    public void onConfigurationRead(){
        root = Play.configuration.containsKey("mustache.dir") ? Play.configuration.getProperty("mustache.dir") : Play.applicationPath+"/app/views/mustaches";
        session_ = new MustacheSession(root);
        try{
            session_.loadFileSystemTemplates(root);
        }catch(Exception e){
            Logger.error("Error initializing Mustache module: "+e.getMessage());
        }
        Logger.info("Mustache module initialized");
    }
        
    @Override
    public Template loadTemplate(VirtualFile file) {
    	if(session_ != null){
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
    	return null;
    }
    
}