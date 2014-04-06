package com.example.cs6300todolist.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ToDoItem {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	@Persistent
	private Long userId;
	@Persistent
	private String name;
	@Persistent
	private String note;
	@Persistent
	private Long dueTime;
	@Persistent
	private Boolean checked;
	@Persistent
	private Boolean noDueTime;
	@Persistent
	private Long priority;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getDueTime() {
		return dueTime;
	}

	public void setDueTime(Long dueTime) {
		this.dueTime = dueTime;
	}

	public Boolean isChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public Boolean isNoDueTime() {
		return noDueTime;
	}

	public void setNoDueTime(Boolean noDueTime) {
		this.noDueTime = noDueTime;
	}

	public Long getPriority() {
		return priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
	}
}
