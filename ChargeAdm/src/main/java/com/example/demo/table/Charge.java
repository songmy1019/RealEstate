package com.example.demo.table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table (name="Charge_table")
@Entity 
public class Charge {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id; // 일련번호

	@Column (name = "ESTATEID")
	private String estateid; //부동산 매물 id  
	@Column (name = "PRICE")
	private long price; //가격
	@Column (name = "REGDATE")
	private String regdate; //등록일자
	@Column (name = "CNCLYN")
	private String cnclyn; //취소여부

}
