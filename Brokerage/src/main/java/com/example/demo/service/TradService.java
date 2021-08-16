package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.kafka.KafkaModule;
import com.example.demo.kafka.KafkaVO;
import com.example.demo.table.Trad;
import com.example.demo.table.TradRepository;
import com.example.demo.util.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@EnableAutoConfiguration
@Transactional
public class TradService {
	
	@Autowired
	private TradRepository tradRepository;
	
	@Autowired
	private KafkaModule kafkaModule;
	
	@Value("${masurl.realestate}")
	private String realestate;
	
	@RequestMapping(value ="/brokerage/regist", method = RequestMethod.POST)
	public @ResponseBody List<Trad> registSale(@RequestBody Map<String,String> param){
		
		System.out.println( "registSale START " );
		List<Trad> rv = new ArrayList<Trad>();
		
		try {
			Trad trad = new Trad();
			
			if ( param.get("estateid") == null ) {
				System.out.println( "부동산 번호를 확인 하세요!!" );
	        	return null;
			}
			
			System.out.println( "매물관리 확인 url : " + realestate );
			
			//등록전 번호 및 상태 확인
			Map<String,String> parameters = new HashMap<String,String>();
			parameters .put( "id" , param.get("estateid").toString());
			
			String url = "http://" + realestate + "/realestate/inqerone" ;
	        ResponseEntity<String> res = new RestTemplate().postForEntity(url, parameters, String.class);
	        
	        if ( res.getStatusCodeValue() != 200 ) {
	        	System.out.println( "getBody : " + res.getBody());
	        	return null;
	        }
	        String jsion = res.getBody();
	        ObjectMapper mapper = new ObjectMapper();
	        Map<String, String> estateMap = mapper.readValue(jsion, Map.class);
	        
	        if ( "Y".equals(estateMap.get( "cnclyn" ) )) {
	        	System.out.println( "삭제된 매물 입니다.!! ");
	        	return null;
	        } else if ( "Y".equals(estateMap.get( "reserveyn" ) )) {
	        	System.out.println( "계약된 매물 입니다.!! ");
	        	return null;
	        } else if ( "Y".equals(estateMap.get( "paymentyn" ) )) {
	        	System.out.println( "매매 완료된 매물 입니다.!! ");
	        	return null;
	        }
	        	
	        //System.out.println( "getBody : " + res.getBody());
	        //System.out.println( "getStatusCodeValue : " + res.getStatusCodeValue());
			
			trad.setEstateid( param.get("estateid").toString() );
			trad.setPhone( param.get("phone").toString() );
			trad.setPrice( Long.parseLong(param.get("price").toString()) );
			
			trad.setRegdate( DateUtil.getCurrentDate() );
			trad.setCnclyn( "N" );
			trad.setPaymentyn( "N" );
			
			tradRepository.save(trad);
			
			//kafka 로 상태 변경..
			KafkaVO vo = new KafkaVO();
			vo.setJOB( "realestate" );
			vo.setESTATEID( param.get("estateid").toString() );
			vo.setSUBJOB( "reserve" );
			vo.setPRICE( "0" );
			kafkaModule.sendMessage( vo );
			
			return tradRepository.findAll();
		} catch (Exception e ) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value ="/brokerage/inqer", method = RequestMethod.GET)
	public @ResponseBody List<Trad> inqerSale(){
		System.out.println( "inqerSale START " );
		
		return tradRepository.findAll();
	}
	
	@RequestMapping(value ="/brokerage/inqerid", method = RequestMethod.POST)
	public @ResponseBody String inqerid(@RequestBody Map<String,String> param){
		System.out.println( "inqerid START " );
		
		String json = null;
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
        	
        	Optional<Trad> optionalTrad = tradRepository.findById( Long.parseLong(param.get("id").toString()) );
        	Trad trad = optionalTrad.get();
    		json = objectMapper.writeValueAsString(trad);
        } catch (Exception e) {
        	e.printStackTrace();
        }
		
		return json;
	}
	
	@RequestMapping(value ="/brokerage/payment", method = RequestMethod.POST)
	public @ResponseBody String payment(@RequestBody Map<String,String> param){
		System.out.println( "payment START " );
		
		String json = null;
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
        	
        	Optional<Trad> optionalTrad = tradRepository.findById( Long.parseLong(param.get("id").toString()) );
        	Trad trad = optionalTrad.get();
        	
        	if (  "Y".equals( trad.getPaymentyn() ) ){
    			System.out.println( "이미 결제된 상태입니다" );
    			json = "이미 결제된 상태입니다";
        	} else if (  "Y".equals( trad.getCnclyn() ) ){
        		System.out.println( "이미 삭제된 상태입니다" );
    			json = "이미 삭제된 상태입니다";
    		} else {
    			trad.setPaymentyn("Y");
    			tradRepository.save(trad);
    		}
        	
        	//상태 변경 및 수수료 추가..
    		KafkaVO vo = new KafkaVO();
    		vo.setJOB( "realestate|chargeadm" ); //상태변경 및 수수료
    		vo.setESTATEID( trad.getEstateid() );
    		vo.setSUBJOB( "payment" );
    		vo.setPRICE( String.valueOf(trad.getPrice() / 100) );
    		kafkaModule.sendMessage( vo );
        	
    		json = objectMapper.writeValueAsString(trad);
        } catch (Exception e) {
        	e.printStackTrace();
        }
		
		return json;
	}
	
	@RequestMapping(value ="/brokerage/delete", method = RequestMethod.POST)
	public @ResponseBody String delSale(@RequestBody Map<String,String> param){
		System.out.println( "delSale START " );
		
		String json = null;
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
        	
        	Optional<Trad> optionalTrad = tradRepository.findById( Long.parseLong(param.get("id").toString()) );
        	Trad trad = optionalTrad.get();
    		
    		if (  "Y".equals( trad.getPaymentyn() ) ){
    			System.out.println( "결제된 상태라 삭제 할수 없습니다" );
    			json = "결제된 상태라 삭제 할수 없습니다";
    		} else {
    			trad.setCnclyn( "Y" );
    			tradRepository.save(trad);
    		}
    		
    		//예약 상태를 원래 상태로 되돌려야 함.
    		KafkaVO vo = new KafkaVO();
    		vo.setJOB( "realestate|chargeadm" ); //상태변경 및 수수료
    		vo.setESTATEID( param.get("estateid").toString() );
    		vo.setSUBJOB( "cancel" );
    		vo.setPRICE( String.valueOf(trad.getPrice() / 100) );
    		kafkaModule.sendMessage( vo );
    		
            json = objectMapper.writeValueAsString(trad);
        } catch (Exception e) {
        	e.printStackTrace();
        }
		
		return json;
	}
}
