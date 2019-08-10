CREATE TABLE URLEntity (

 oid 				BIGINT(20) 		NOT NULL AUTO_INCREMENT,
 originalURL 		VARCHAR(200) 	NOT NULL,
 alias 				VARCHAR(20) 	NOT NULL,
 timesRequested 	BIGINT(20) 		NOT NULL,
 lastAccess 		DATE 			NOT NULL,
 
 PRIMARY KEY (oid)
);
