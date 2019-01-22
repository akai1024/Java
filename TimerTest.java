package com.example.demo;

import java.util.Timer;
import java.util.TimerTask;

public class TimerTest {

	public static void main(String[] args) {
		System.out.println(new java.util.Date());
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			private int times = 5;

			@Override
			public void run() {
				System.out.println(new java.util.Date());
				times--;

				if (times <= 0) {
					cancel();
				}
			}
		};

		timer.schedule(task, 2000, 1000);
		TimerTask task2 = new TimerTask() {
			@Override
			public void run() {
				System.out.println("t2 " + new java.util.Date());
			}
		};
		timer.schedule(task2, 2000);
	}
}
