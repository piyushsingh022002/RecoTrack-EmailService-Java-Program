package com.reco.emailservice.repository;

import com.reco.emailservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
	User findByid(String id);
}