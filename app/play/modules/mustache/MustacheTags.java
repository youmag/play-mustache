package play.modules.mustache;

import groovy.lang.Closure;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;
import play.templates.JavaExtensions;

import com.sampullara.mustache.MustacheException;

@FastTags.Namespace("mustache")
public class MustacheTags extends FastTags {
    
    public static void _template(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) throws MustacheException {
        String key = args.get("arg").toString();
        MustacheSession session = MustachePlugin.session();
        session.addFromString(key, JavaExtensions.toString(body));
    }
    
    public static void _print(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) throws MustacheException, IOException {
        String key = args.get("arg").toString();
        Object context = args.get("context");
        MustacheSession session = MustachePlugin.session();
        out.print(session.toHtml(key, context));
    }
    
    public static void _script(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) throws MustacheException, IOException {
        StringBuilder templates = new StringBuilder("window.__MUSTACHE_TEMPLATES={");
                
        Iterator<Entry<String,String>> it = MustachePlugin.session().getRawTemplates().entrySet().iterator();
        
        while(it.hasNext()){
            Entry<String,String> pairs = it.next();
            templates.append("\""+JavaExtensions.escapeJavaScript(pairs.getKey().toString())+"\":\""+JavaExtensions.escapeJavaScript(pairs.getValue().toString())+"\"");
            if(it.hasNext()) templates.append(",");
        }
        templates.append("}");
        out.println("<script type=\"text/javascript\">"+templates.toString()+"</script>");
    }
    
    public static void _meta(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) throws MustacheException, IOException {
        StringBuilder templates = new StringBuilder("{\"templates\":{");
        Iterator<Entry<String,String>> it = MustachePlugin.session().getRawTemplates().entrySet().iterator();
        
        while(it.hasNext()){
            Entry<String,String> pairs = it.next();
            templates.append("\""+JavaExtensions.escapeJavaScript(pairs.getKey().toString())+"\":\""+JavaExtensions.escapeJavaScript(pairs.getValue().toString())+"\"");
            if(it.hasNext()) templates.append(",");
        }
        templates.append("}}");
        out.println("<meta id=\"play-mustache-templates\" name=\"play-mustache-templates\" content=\""+JavaExtensions.escapeHtml(templates.toString())+"\">");
    }
    
}
