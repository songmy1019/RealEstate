package com.example.demo.table;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//public class ForSaleRepository extends JpaRepository<ForSale,  Long> {
public interface ForSaleRepository extends JpaRepository <ForSale,  Long> {
	public List<ForSale> findByUserid(String UserID);
	//like검색도 가능
	public List<ForSale> findByAddrLike(String addr);
}
