package com.example.demo.table;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//public class ForSaleRepository extends JpaRepository<ForSale,  Long> {
public interface ChargeRepository extends JpaRepository <Charge,  Long> {
	//public List<Charge> findByUserid(String UserID);
	//like검색도 가능
	//public List<Charge> findByAddrLike(String addr);
}
