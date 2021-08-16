package com.example.demo.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.table.ForSale;
import com.example.demo.table.ForSaleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class KafkaService {
	
	@Autowired
	ForSaleRepository forSaleRepository;
	
	@KafkaListener(topics = "realestate", groupId="realestate")
	public void getKafka(String message) {
		
		System.out.println( "RealEstate getKafka START " );
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, String> map = (Map<String, String>)objectMapper.readValue( message, Map.class);
			System.out.println( "kafka recerve data : " + map);
			
			if ( map.get("job")!= null && map.get("job").indexOf("realestate") >=0
					&& map.get("estateid") != null ) {
				
				ForSale forSale = forSaleRepository.getById( Long.parseLong(map.get("estateid").toString()) );
				
				if ( forSale == null ) {
					System.out.println("not found!!");
					return;
				}
				
				if ( map.get("subjob").indexOf( "cancel" ) >= 0 ) {
					//취소
					forSale.setReserveyn( "N" );
				} else if ( map.get("subjob").indexOf( "reserve" ) >= 0 ) {
					//예약
					forSale.setReserveyn( "Y" );
				} else if ( map.get("subjob").indexOf( "payment" ) >= 0 ) {
					//결재
					forSale.setPaymentyn( "Y");
				}
				forSaleRepository.save( forSale );
			} else {
				System.out.println( "kafka Skip ");
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
