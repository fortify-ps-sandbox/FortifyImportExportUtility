package com.fortify.impexp;

import java.io.IOException;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import com.fortify.util.spring.boot.container.PopulateContainerDirs;
import com.fortify.util.spring.boot.env.ModifyablePropertySource;
import com.fortify.util.spring.boot.env.ModifyablePropertySourceScope.ModifyablePropertySourceScopeBeanFactoryPostProcessor;

@SpringBootApplication(scanBasePackages="com.fortify")
public class FortifyImportExportUtility {
	/**
	 * Start the application
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//Constants.updateSystemProperties();
		PopulateContainerDirs.populateContainerDirs();
		new SpringApplicationBuilder(FortifyImportExportUtility.class)
			.environment(ModifyablePropertySource.createEnvironment())
			.build().run(args);
	}
	
	@Bean
	public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
		return new ModifyablePropertySourceScopeBeanFactoryPostProcessor();
	}
}
