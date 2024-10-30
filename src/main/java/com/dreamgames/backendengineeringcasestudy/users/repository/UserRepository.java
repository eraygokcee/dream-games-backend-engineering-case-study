package com.dreamgames.backendengineeringcasestudy.users.repository;

import com.dreamgames.backendengineeringcasestudy.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,String>
{

}
