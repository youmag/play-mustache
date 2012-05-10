package controllers;

import java.util.List;

import models.Content;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
    	
    	List<Content> contents = Content.findAll();
    	Content content = contents.get(0);	
        render(contents, content);
    }
    
    public static void show(long id){
    	renderText("Ok: " + id);
    }
    
    public static void json(){
    	List<Content> contents = Content.findAll();
    	renderJSON(contents);
    }
}