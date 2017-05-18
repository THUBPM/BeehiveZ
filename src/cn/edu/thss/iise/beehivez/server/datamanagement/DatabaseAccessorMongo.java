package cn.edu.thss.iise.beehivez.server.datamanagement;

import java.util.List;

import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.context.support.GenericXmlApplicationContext;

import cn.edu.thss.iise.beehivez.server.datamanagement.entity.Catalog;
import cn.edu.thss.iise.beehivez.server.datamanagement.pojo.ProcesscatalogObject;

public class DatabaseAccessorMongo {
	ApplicationContext ctx = new GenericXmlApplicationContext("SpringConfig.xml");
	MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
	
	public String getDbName() {
		return "springdatatest";
	}
	
	public long addProcessCatalog(ProcesscatalogObject pco) {
		int rt = -1;
		Catalog catalog = new Catalog(pco.getName(), pco.getParent_id() + "", "public", "111");
		mongoOperation.save(catalog);
		return rt;
	}
}
