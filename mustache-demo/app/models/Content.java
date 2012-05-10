package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Content extends Model{

	private static final long serialVersionUID = -2247835631168554430L;
	public String name;
	
	@ManyToOne
	public Editor editor;

	public Content(String name) {
		super();
		this.name = name;
		this.save();
	}

	@Override
	public String toString() {
		return "Content [name=" + name + ", editor=" + editor + "]";
	}
}
