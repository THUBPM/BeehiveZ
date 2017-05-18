package cn.edu.thss.iise.beehivez.server.datamanagement.entity;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "catalog")
public class Catalog {
	@Id
	private String id;
	private String name;
	private String parent_id;
	private String type;
	private String owner_id;
	private Map<String, String> access;
	
	public Catalog(String _n, String _pid, String _t, String _oid) {
		name = _n;
		parent_id = _pid;
		type = _t;
		owner_id = _oid;
		access = new HashMap<String, String>();
	}
	
	@Override
	public String toString() {
		return String.format("Catalog[id=%s, name=%s, parent_id=%s, "
				+ "type=%s, owner_id=%s]", id, name, parent_id,
				type, owner_id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}

	public Map<String, String> getAccess() {
		return access;
	}

	public void setAccess(Map<String, String> access) {
		this.access = access;
	}
}
