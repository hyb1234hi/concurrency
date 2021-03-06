package com.ronglexie.concurrency.synccontainer;

import com.ronglexie.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 同步容器类（线程安全）：Collections.synchronizedMap
 *
 * @author ronglexie
 * @version 2018/4/22
 */
@Slf4j
@ThreadSafe
public class SafeCollectionsSyncMap {

	/**
	 * 请求总数
	 */
	private static int clientTotal = 5000;

	/**
	 * 线程并发数
	 */
	private static int threadTotal = 200;

	/**
	 * map
	 */
	private static Map<Integer,Integer> map = Collections.synchronizedMap(new Hashtable<>());

	public static void main(String[] args) throws Exception{
		ExecutorService executorService = Executors.newCachedThreadPool();
		final Semaphore semaphore = new Semaphore(threadTotal);
		final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
		for (int i = 0; i < clientTotal; i++){
			final int count = i;
			executorService.execute(() -> {
				try {
					semaphore.acquire();
					update(count);
					semaphore.release();
				} catch (InterruptedException e) {
					log.error("exception:",e);
				}
				countDownLatch.countDown();
			});
		}
		countDownLatch.await();
		executorService.shutdown();
		log.info("size:{}",map.size());
	}

	private static void update(int i){
		map.put(i,i);
	}
}
