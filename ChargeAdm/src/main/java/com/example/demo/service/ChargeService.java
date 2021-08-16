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

import com.example.demo.table.Charge;
import com.example.demo.table.ChargeRepository;
import com.example.demo.util.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@EnableAutoConfiguration
@Transactional
public class ChargeService {
	
	@Autowired
	ChargeRepository chargeRepository;
	
	@RequestMapping(value ="/chargeadm/regist", method = RequestMethod.POST)
	public @ResponseBody List<Charge> registCharge(@RequestBody Map<String,String> param){
		
		System.out.println( "registCharge START " );
		
		Charge charge = new Charge();
		charge.setEstateid(  param.get("estateid").toString() );
		charge.setPrice( Long.parseLong(param.get("price").toString()) );
		
		charge.setRegdate( DateUtil.getCurrentDate() );
		charge.setCnclyn( "N" );
		
		chargeRepository.save(charge);
		
		return chargeRepository.findAll();
	}
	
	@RequestMapping(value ="/chargeadm/inqer", method = RequestMethod.GET)
	public @ResponseBody List<Charge> inqerCharge(){
		System.out.println( "inqerCharge START" );
		
		return chargeRepository.findAll();
	}
	
	@RequestMapping(value ="/chargeadm/inqerid", method = RequestMethod.POST)
	public @ResponseBody String inqerid(@RequestBody Map<String,String> param){
		System.out.println( "inqerid START " );
		
		String json = null;
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
        	
        	Optional<Charge> optionalCharge = chargeRepository.findById( Long.parseLong(param.get("id").toString()) );
        	Charge charge = optionalCharge.get();
    		json = objectMapper.writeValueAsString( charge);
        } catch (Exception e) {
        	e.printStackTrace();
        }
		
		return json;
	}
	
	@RequestMapping(value ="/chargeadm/delete", method = RequestMethod.POST)
	public @ResponseBody String delCharge(@RequestBody Map<String,String> param){
		System.out.println( "delCharge START " );
		
		String json = null;
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
        	
			Optional<Charge> optionalCharge = chargeRepository.findById( Long.parseLong(param.get("id").toString()) );
        	Charge charge = optionalCharge.get();
    		
        	charge.setCnclyn( "Y" );
        	chargeRepository.save(charge);
    		
    		json = objectMapper.writeValueAsString( charge);
        } catch (Exception e) {
        	e.printStackTrace();
        }
		
		return json;
	}
}
