package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RestController;

import telran.logs.bugs.aop.LoggerAspect;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(LoggerAspectTest.TestController.class)
@ContextConfiguration(classes= {LoggerAspect.class, LoggerAspectTest.TestController.class})
@TestPropertySource(locations = "classpath:application.properties")
public class LoggerAspectTest {
	@Autowired
	MockMvc mock;
	@Autowired
	LoggerAspect aspect;
	@MockBean
	LoggingComponent loggingComponent;
	TestController controllerProxy;
	public static @RestController class TestController {
		
		public String getTest() throws InterruptedException {
			Thread.sleep(100);
			return "Hello";
		}
		
		public void getTestBadRequestException() throws InterruptedException {
			
			throw new IllegalArgumentException();
		}
public void getTestServerException() throws InterruptedException {
			
			throw new RuntimeException();
		}
		
	}
	@BeforeEach
	void setUp() {
		AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(new TestController());
        aspectJProxyFactory.addAspect(aspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controllerProxy = (TestController) aopProxy.getProxy();
		
        
	}
	@Test
	void loggerAspectNormal() throws Exception {
		Mockito.doAnswer(invocation -> {
        	LogDto logDto = invocation.getArgument(0, LogDto.class);
        	assertEquals("Hello", logDto.result);
        	assertTrue(logDto.responseTime >= 100);
        	assertEquals(LogType.NO_EXCEPTION, logDto.logType);
        	return null;
        }).when(loggingComponent).sendLog(Mockito.any(LogDto.class));
		controllerProxy.getTest();
	}
	@Test
	void loggerAspectException() throws Exception {
		doAnswerException(LogType.BAD_REQUEST_EXCEPTION);
		
		try {
			controllerProxy.getTestBadRequestException();
		} catch (Exception e) {
			
		}
	}
	@Test
	void loggerAspectServerException() throws Exception {
		doAnswerException(LogType.SERVER_EXCEPTION);
		
		try {
			controllerProxy.getTestServerException();
		} catch (Exception e) {
			
		}
	}
	private void doAnswerException(LogType exceptionType) {
		Mockito.doAnswer(invocation -> {
        	LogDto logDto = invocation.getArgument(0, LogDto.class);
        	assertEquals(exceptionType, logDto.logType);
        	assertEquals(0, logDto.responseTime);
        	return null;
        }).when(loggingComponent).sendLog(Mockito.any(LogDto.class));
	}
}
