package com.example.Netflix.Content.GenreBridge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreBridgeRepository extends JpaRepository<GenreBridge, Long>
{
}
