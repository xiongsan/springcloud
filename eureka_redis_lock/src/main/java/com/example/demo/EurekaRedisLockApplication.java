package com.example.demo;

import com.example.demo.Service.AcquiredLockWorker;
import com.example.demo.Service.SecondKill;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class EurekaRedisLockApplication {


	public static void main(String[] args) {
		SpringApplication.run(EurekaRedisLockApplication.class, args);
	}

	@Value("${server.port}")
	String port;

	@Autowired
	private RedisLocker distributedLocker;

	@Autowired
	private SecondKill secondKill;

	@RequestMapping(value = "/redlock")
	public String testRedlock() throws Exception{

		CountDownLatch startSignal = new CountDownLatch(1);
		CountDownLatch doneSignal = new CountDownLatch(1000);
		for (int i = 0; i <=499; ++i) { // create and start threads
			new Thread(new Worker(startSignal, doneSignal,"a",i)).start();
		}
		for (int i = 500; i <=999; ++i) { // create and start threads
			new Thread(new Worker(startSignal, doneSignal,"b",i)).start();
		}
		long startTime = System.currentTimeMillis();
		startSignal.countDown(); // let all threads proceed,fair
		doneSignal.await();
		System.out.println("All processors done. Shutdown connection");
		System.out.println("耗时："+(System.currentTimeMillis()-startTime));
		System.out.println("a 商品剩余："+SecondKill.map.get("a"));
		System.out.println("b 商品剩余："+SecondKill.map.get("b"));
		System.out.println("获得a商品的用户数量："+secondKill.getKillA().size());
		System.out.println("获得b商品的用户数量："+secondKill.getKillB().size());
		System.out.println("获得a商品的用户："+secondKill.getKillA());
		System.out.println("获得b商品的用户："+secondKill.getKillB());
		return "redlocki I am from port:" +port;
	}

	class Worker implements Runnable {
		private final CountDownLatch startSignal;
		private final CountDownLatch doneSignal;
		private final String commodityId;
		private final int userId;

		Worker(CountDownLatch startSignal, CountDownLatch doneSignal,String commodityId,int userId) {
			this.startSignal = startSignal;
			this.doneSignal = doneSignal;
			this.commodityId = commodityId;
			this.userId = userId;
		}

		public void run() {
			try {
				startSignal.await();
//				distributedLocker.lock("test", () -> {doTask(commodityId, userId);return null;});
				doTask(commodityId, userId);
			}catch (Exception e){

			}
		}

		void doTask(String commodityId,int userId) {
//			System.out.println(Thread.currentThread().getName() + " start");
//			Random random = new Random();
//			int _int = random.nextInt(200);
//			System.out.println(Thread.currentThread().getName() + " sleep " + _int + "millis");
//			try {
//				Thread.sleep(_int);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			System.out.println(Thread.currentThread().getName() + " end");

			secondKill.reduce(commodityId,userId);

			doneSignal.countDown();
		}
	}

}
