package com.example.demo.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.table.Charge;
import com.example.demo.table.ChargeRepository;
import com.example.demo.util.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class KafkaService {
	
	@Autowired
	ChargeRepository chargeRepository;
	
	@KafkaListener(topics = "realestate", groupId="chargeadm")
	public void getKafka(String message) {
		
		System.out.println( "ChargeAdm getKafka START " );
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, String> map = (Map<String, String>)objectMapper.readValue( message, Map.class);
			System.out.println( "kafka recerve data : " + map);
			
			if ( map.get("job")!= null && map.get("job").indexOf("chargeadm") >=0
					&& map.get("estateid") != null ) {
				
				Charge charge = new Charge();
				System.out.println( "** estateid : " + map.get("estateid").toString());
				charge.setEstateid( map.get("estateid").toString() );
				charge.setPrice( Long.parseLong( map.get("price").toString()) );
				charge.setRegdate( DateUtil.getCurrentDate() );
				
				if ( map.get("subjob").indexOf( "cancel" ) >= 0 ) 
					charge.setCnclyn( "Y" );
				else
					charge.setCnclyn( "N" );
				
				chargeRepository.save(charge);
			} else {
				System.out.println( "kafka Skip ");
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
