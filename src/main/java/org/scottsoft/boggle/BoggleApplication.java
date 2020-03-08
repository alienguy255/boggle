package org.scottsoft.boggle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Info on deploying this application to AWS Elastic Beanstalk was referenced at:
 * https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/GettingStarted.html
 */
@SpringBootApplication
public class BoggleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoggleApplication.class, args);
	}

}
