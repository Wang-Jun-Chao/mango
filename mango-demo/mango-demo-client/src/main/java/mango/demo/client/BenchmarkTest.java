package mango.demo.client;

import mango.demo.service.DemoService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Ricky Fung
 */
public class BenchmarkTest {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:mango-client.xml");

        DemoService demoService = (DemoService) applicationContext.getBean("demoService");
        System.out.println(demoService.echo("Hello World"));

    }
}
