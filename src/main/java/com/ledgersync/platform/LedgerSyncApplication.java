package com.ledgersync.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LedgerSyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(LedgerSyncApplication.class, args);
	}

	@org.springframework.context.annotation.Bean
	public liquibase.integration.spring.SpringLiquibase liquibase(javax.sql.DataSource dataSource) {
		liquibase.integration.spring.SpringLiquibase liquibase = new liquibase.integration.spring.SpringLiquibase();
		liquibase.setDataSource(dataSource);
		liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
		return liquibase;
	}

}
