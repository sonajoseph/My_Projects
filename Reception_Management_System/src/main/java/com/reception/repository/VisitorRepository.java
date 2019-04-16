package com.reception.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.reception.dto.VisitorDto;
import com.reception.model.Hosts;
import com.reception.model.Visitors;

@Repository
public interface VisitorRepository extends JpaRepository<Visitors, Integer> {
	
//	List<AnEntity> findByStartDateBeforeAndEndDateAfter(Date startDate, Date endDate);
//
//	default List<AnEntity> findByStartDateBeforeAndEndDateAfter(Date givenDate) {
//	    return findByStartDateBeforeAndEndDateAfter(givenDate, givenDate);
//	}
	
//	public MyDateEntity findByStartDateLessThanAndEndDateGreaterThan(Date sDate, Date eDate);

	
////	
//	@Query(value="SELECT COUNT(*) as totalvisitors FROM Visitors WHERE date BETWEEN :creationDateTime1 AND :creationDateTime2 ")
//	List<Visitors> findByDate(@Param("creationDateTime1") Date date1,@Param("creationDateTime2") Date date2);
	
	
    @Query(value="SELECT category,COUNT(*) as totalvisitors FROM Visitors WHERE date BETWEEN ?1 AND ?2 GROUP BY category")
    List<Visitors> findByDate( Date date1, Date date2);
    
//    List<Visitors> findByDate(@Param("creationDateTime1") String date1,@Param("creationDateTime2") String date2);
	
	
//	@Query(value="SELECT COUNT(*) AS totalvisitors FROM Visitors WHERE eventid=4 AND date BETWEEN ?1 AND ?2")
//	 List<Visitors> findByDate(Date date1,Date date2);
	
//	@Query(value="SELECT COUNT(*) AS totalvisitors FROM Visitors WHERE eventid=?1 AND date BETWEEN ?2 AND ?3")
//	 List<Visitors> findByDate(Integer i,Date date1,Date date2);
    
    @Query(value="SELECT events,COUNT(*) as totalvisitors FROM Visitors WHERE events IS NOT NULL AND date BETWEEN ?1 AND ?2 GROUP BY events")
    List<Visitors>findByEvents(Date date1,Date date2);
    
    
    @Query(value="SELECT host,COUNT(*) as totalvisitors FROM Visitors WHERE host IS NOT NULL AND date BETWEEN ?1 AND ?2 GROUP BY host")
	 List<Visitors>findByHosts(Date date11,Date date12);
	
	
	Visitors save(Visitors visitor);

	Visitors save(VisitorDto visitor);

}
