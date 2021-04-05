package telran.logs.bugs.discovery;

import org.reactivestreams.Publisher;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;

@Component
public class LoadBalancer {
ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory;
public LoadBalancer(ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory) {
	this.loadBalancerFactory = loadBalancerFactory;
}
public String getBaseUrl(String serviceName) {
	ReactiveLoadBalancer<ServiceInstance> rlb =
			loadBalancerFactory.getInstance(serviceName);
	Publisher<Response<ServiceInstance>> publisher = rlb.choose();
	Flux<Response<ServiceInstance>> chosen = Flux.from(publisher);
	ServiceInstance instance = chosen.blockFirst().getServer();
	return instance.getUri().toString();
}
}
