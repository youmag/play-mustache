package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Editor extends Model{

	private static final long serialVersionUID = -6817254463746255717L;
	
	public String name;

	public Editor(String name) {
		super();
		this.name = name;
		this.save();
	}
	
}
