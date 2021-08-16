package com.example.demo.table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table (name="ForSale_table")
@Entity 
public class ForSale {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id; // 일련번호

	@Column (name = "ADDR")
	private String addr; //주소  
	@Column (name = "USERID")
	private String userid; //UserID
	@Column (name = "PHONE")
	private String phone; //전화번호
	@Column (name = "PRICE")
	private long price; //가격
	@Column (name = "REGDATE")
	private String regdate; //등록일자
	@Column (name = "CNCLYN")
	private String cnclyn; //취소여부
	@Column (name = "RESERVEYN")
	private String reserveyn; //예약여부
	@Column (name = "PAYMENTYN")
	private String paymentyn; //결재여부
	
}
