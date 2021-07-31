package com.iktakademija.e_diary.services;

import com.iktakademija.e_diary.entities.MarkEntity;


public interface EmailService {
	public void sendTemplateMessage(  MarkEntity mark) throws	Exception;


}
