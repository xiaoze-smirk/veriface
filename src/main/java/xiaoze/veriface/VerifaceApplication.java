package xiaoze.veriface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"xiaoze.veriface.*"})
public class VerifaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VerifaceApplication.class, args);
	}
}
