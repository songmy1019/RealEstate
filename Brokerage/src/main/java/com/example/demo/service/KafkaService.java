package com.example.demo.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.table.TradRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class KafkaService {
	
	@Autowired
	TradRepository tradRepository;
	
	@KafkaListener(topics = "realestate", groupId="brokerage")
	public void getKafka(String message) {
		
		System.out.println( "Brokerage getKafka START " );
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> map = (Map<String, Object>)objectMapper.readValue( message, Map.class);
			System.out.println( "kafka recerve data : " + map);
			
			if ( map.get("JOB") != null && map.get("JOB").toString().indexOf("brokerage") >=0
					&& map.get("ESTATEID") != null ) {
				System.out.println( "All Skip - 작업 없음.");
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
