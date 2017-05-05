----------------------------------------------
--sql script for PostgreSQL
--edited by JinTao on 2009.9.3
----------------------------------------------

-- ----------------------------
-- Drop Table, ignore the failure at the first time
-- ----------------------------
DROP TABLE indexinfo;
DROP TABLE oplog;
DROP TABLE petrinet;
DROP TABLE process;
DROP TABLE process_catalog;

-- ----------------------------
-- Table structure for process_catalog
-- ----------------------------
CREATE TABLE process_catalog
(
	catalog_id BIGINT auto_increment,
	name varchar(255) unique NOT NULL,
	parent_id bigint not null,
	primary key (catalog_id),
	FOREIGN KEY (parent_id) REFERENCES process_catalog(catalog_id) ON DELETE CASCADE
);

-- ----------------------------
-- Table structure for process
-- ----------------------------
CREATE TABLE process
(
	process_id BIGINT auto_increment,
	name varchar(255) NOT NULL,
	description varchar(255),
	type char(10) not null,
	definition longtext not null,
	catalog_id bigint not null,
	petrinet_id bigint,
	addtime timestamp not null,
	primary key (process_id),
	FOREIGN KEY (catalog_id) REFERENCES process_catalog (catalog_id)  ON DELETE CASCADE
);

-- ----------------------------
-- Table structure for petrinet
-- ----------------------------
CREATE TABLE petrinet
(
	petrinet_id BIGINT auto_increment,
	process_id bigint not null,
	pnml longtext not null,
	nplace int not null,
	ntransition int not null,
	narc int not null,
	ndegree int not null,
	primary key (petrinet_id),
	FOREIGN KEY (process_id) REFERENCES process(process_id)  ON DELETE CASCADE
);

-- ----------------------------
-- Table structure for indexinfo
-- ----------------------------
CREATE TABLE indexinfo
(
	index_id BIGINT auto_increment,
	javaclassname varchar(255) unique not null,
	description varchar(255),
	state char(10) not null,
	primary key (index_id)
);

-- ----------------------------
-- Table structure for oplog
-- ----------------------------
CREATE TABLE oplog
(
	event_id BIGINT auto_increment,
	optime timestamp not null,
	indexname varchar(255) not null,
	optype char(10) not null,
	operand varchar(255),
	timecost bigint not null,
	nplace int not null,
	ntransition int not null,
	narc int not null,
	ndegree int not null,
	npetri bigint not null,
	resultsize int,
	primary key (event_id)
);


-- ----------------------------
-- Added by Zhougz 2010.04.03
-- For saving relationship matrix to database
-- ...BEGIN...
-- ----------------------------
-- DELETE FROM rltmatrix;
-- DROP TABLE rltmatrix;

-- CREATE TABLE rltmatrix
-- (
--	process_id bigint not null,
--	transitionnum bigint not null,
--	tran2idxmap blob not null,
--	matrix blob not null,
--	primary key (process_id),
--	FOREIGN KEY (process_id) REFERENCES process(process_id)  ON DELETE CASCADE
-- );

-- insert into indexinfo (javaclassname, state) values ('cn.edu.thss.iise.beehivez.server.index.rltMatrix.RltMatrixIndexNo1', 'unused');
-- insert into indexinfo (javaclassname, state) values ('cn.edu.thss.iise.beehivez.server.index.rltMatrix.RltMatrixIndexNo2', 'unused');
-- insert into indexinfo (javaclassname, state) values ('cn.edu.thss.iise.beehivez.server.index.rltMatrix.RltMatrixIndexNo3', 'unused');

-- ----------------------------
-- Added by Zhougz 2010.04.03
-- For saving relationship matrix to database
-- ...END...
-- ----------------------------




-- ----------------------------
-- Records 
-- ----------------------------
insert into process_catalog (catalog_id, name, parent_id) values (1, 'Business Process Model Repository', 1);
insert into process_catalog (name, parent_id) values ('petri nets', 1);
insert into process_catalog (name, parent_id) values ('yawl models', 1);

insert into indexinfo (javaclassname, state) values ('cn.edu.thss.iise.beehivez.server.index.petrinetindex.pathindex.LengthOnePathIndex', 'unused');
insert into indexinfo (javaclassname, state) values ('cn.edu.thss.iise.beehivez.server.index.petrinetindex.pathindex.LengthTwoClosurePathIndex', 'unused');
insert into indexinfo (javaclassname, state) values ('cn.edu.thss.iise.beehivez.server.index.petrinetindex.nullindex.NullIndex', 'unused');
insert into indexinfo (javaclassname, state) values ('cn.edu.thss.iise.beehivez.server.index.petrinetindex.tarluceneindex.TARLuceneIndex', 'unused');
insert into indexinfo (javaclassname, state) values ('cn.edu.thss.iise.beehivez.server.index.petrinetindex.tarluceneindex.NoTARSSimilarIndex', 'unused');
insert into indexinfo (javaclassname, state) values ('cn.edu.thss.iise.beehivez.server.index.petrinetindex.relationindex.TaskRelationIndex', 'unused');
insert into indexinfo (javaclassname, state) values ('cn.edu.thss.iise.beehivez.server.index.yawlindex.yawltasksluceneindex.YAWLTasksLuceneIndex', 'unused');
insert into indexinfo (javaclassname, state) values ('cn.edu.thss.iise.beehivez.server.index.yawlindex.NullYAWLIndex', 'unused');
insert into indexinfo (javaclassname, state) values ('cn.edu.thss.iise.beehivez.server.index.petrinetindex.taskedgeindex.NoTaskEdgeIndex', 'unused');
insert into indexinfo (javaclassname, state) values ('cn.edu.thss.iise.beehivez.server.index.petrinetindex.taskedgeindex.TaskEdgeLuceneIndex', 'unused');