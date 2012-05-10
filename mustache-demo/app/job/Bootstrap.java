package job;

import models.Content;
import models.Editor;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job<Boolean>{

	public void doJob(){
		if(Content.count() == 0){
			
			Content c1 = new Content("first");
			new Content("two");
			new Content("third");
			
			Editor e = new Editor("jb");
			
			c1.editor = e;
			c1.save();
		}
	}
}
