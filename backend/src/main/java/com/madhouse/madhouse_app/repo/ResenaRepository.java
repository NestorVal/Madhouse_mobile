package com.madhouse.madhouse_app.repo;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository; 
import com.madhouse.madhouse_app.model.Resena;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Integer> {
    
}
