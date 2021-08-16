package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.kafka.KafkaModule;
import com.example.demo.kafka.KafkaVO;
import com.example.demo.table.ForSale;
import com.example.demo.table.ForSaleRepository;
import com.example.demo.util.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@EnableAutoConfiguration
@Transactional
public class ForSaleService {
	
	@Autowired
	ForSaleRepository forSaleRepository;
	
	@Autowired
	KafkaModule kafkaModule;
	
	@RequestMapping(value ="/realestate/regist", method = RequestMethod.POST)
	public @ResponseBody List<ForSale> registSale(@RequestBody Map<String,String> param){
		
		System.out.println( "registSale START " );
		
		try {
			ForSale forSale = new ForSale();
			forSale.setAddr(  param.get("addr").toString() );
			forSale.setUserid(  param.get("userid").toString() );
			forSale.setPhone( param.get("phone").toString() );
			forSale.setPrice( Long.parseLong(param.get("price").toString()) );
			
			forSale.setRegdate( DateUtil.getCurrentDate() );
			forSale.setCnclyn( "N" );
			forSale.setReserveyn( "N" );
			forSale.setPaymentyn( "N" );
			
			forSaleRepository.save(forSale);
			
			//kafka 로 수수료 생성.
			KafkaVO vo = new KafkaVO();
			vo.setJOB( "chargeadm" );
			vo.setESTATEID( String.valueOf(forSale.getId()) );
			vo.setSUBJOB( "charge" );
			vo.setPRICE( String.valueOf( Long.parseLong(param.get("price").toString()) / 100 ) );
			kafkaModule.sendMessage( vo );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		return forSaleRepository.findAll();
	}
	
	@RequestMapping(value ="/realestate/inqer", method = RequestMethod.GET)
	public @ResponseBody List<ForSale> inqerSale(){
		System.out.println( "inqerSale START " );
		
		return forSaleRepository.findAll();
	}
	
	@RequestMapping(value ="/realestate/inqeraddr", method = RequestMethod.POST)
	public @ResponseBody List<ForSale> inqeraddr(@RequestBody Map<String,String> param){
		System.out.println( "inqeraddr START " );
		
		return forSaleRepository.findByAddrLike( param.get("addr").toString() );
	}
	
	@RequestMapping(value ="/realestate/inqerone", method = RequestMethod.POST)
	public @ResponseBody String inqerone(@RequestBody Map<String,String> param){
		System.out.println( "inqerone START " );
		
		String json = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Optional<ForSale> optionalSale = forSaleRepository.findById( Long.parseLong(param.get("id").toString()) );
    		ForSale forSale = optionalSale.get();
			json = objectMapper.writeValueAsString(forSale);
		} catch (Exception e ) {
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping(value ="/realestate/delete", method = RequestMethod.POST)
	public @ResponseBody String delSale(@RequestBody Map<String,String> param){
		System.out.println( "delSale START " );
		
		String json = null;

        try {
        	ObjectMapper objectMapper = new ObjectMapper();
        	
        	Optional<ForSale> optionalSale = forSaleRepository.findById( Long.parseLong(param.get("id").toString()) );
    		ForSale forSale = optionalSale.get();
    		
    		if ( "Y".equals( forSale.getReserveyn() ) || "Y".equals( forSale.getPaymentyn() ) ){
    			System.out.println( "예약 또는 결제된 상태라 삭제 할수 없습니다" );
    		} else {
    			forSale.setCnclyn( "Y" );
    			forSaleRepository.save(forSale);
    		}
    		
            json = objectMapper.writeValueAsString(forSale);
        } catch (Exception e) {
        	e.printStackTrace();
        }
		
		return json;
	}
}
