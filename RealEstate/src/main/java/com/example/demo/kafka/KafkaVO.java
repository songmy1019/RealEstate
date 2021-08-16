package com.example.demo.kafka;

import lombok.Data;

@Data
public class KafkaVO {
	private String JOB;
	private String ESTATEID; //부동산 매물 id  
	private String SUBJOB; //상세 작업구분 ( 취소:cancel 예약:reserve 결재:payment 수수료:charge)
	private String PRICE; //가격
}
