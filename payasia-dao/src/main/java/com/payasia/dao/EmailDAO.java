package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.Email;

public interface EmailDAO {

	Email saveReturn(Email email);

	void update(Email email);

	void delete(Email email);

	Email findById(long emailId);

	List<Email> findAllEmailsBySentDate();

}
