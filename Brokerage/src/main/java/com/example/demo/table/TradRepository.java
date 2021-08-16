package com.example.demo.table;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//public class ForSaleRepository extends JpaRepository<ForSale,  Long> {
public interface TradRepository extends JpaRepository <Trad,  Long> {
	//public List<Trad> findByUserid(String UserID);
	//like검색도 가능
	//public List<Trad> findByAddrLike(String addr);
}
