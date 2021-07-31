package com.iktakademija.e_diary.services;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.iktakademija.e_diary.entities.MarkEntity;
import com.iktakademija.e_diary.entities.ParentEntity;

@Service
public class EmailServiceImp implements EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void sendTemplateMessage(MarkEntity mark) throws Exception {
		MimeMessage mail = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mail, true);
		List<ParentEntity> parents = mark.getStudent().getParents();
		List<String> emails = new ArrayList<>();
		for (ParentEntity parentEntity : parents) {
			emails.add(parentEntity.getEmail());
		}
		String emailsTo = emails.toString();
		emailsTo = emailsTo.replace("[", "");
		emailsTo = emailsTo.replace("]", "");

		helper.setTo(InternetAddress.parse(emailsTo));
		helper.setSubject(
				"Nova ocena za ucenika " + mark.getStudent().getFirstName() + " " + mark.getStudent().getLastName());
		String text = "<html><head><style>table, th, td {border: 1px solid black;}td { padding: 5px}"
				+ "</style></head><body><table >" + "<tr>" + "<th>Ucenik</th>" + "<th>Predmet</th>"
				+ "<th>Ocena</th>" + "<th>Tip</th>" + "<th>Datum</th>" + "<th>Nastavnik</th>"
				+ "</tr>" + "<tr>" + "<td>" + mark.getStudent().getFirstName() + " "
				+ mark.getStudent().getLastName() + "</td>" + "<td>" + mark.getSubject().getSubjectName()
				+ "</td>" + "<td>" + mark.getMarkValue() + "</td>" + "<td>" + mark.getMarkDescription()
				+ "</td>" + "<td>" + mark.getCreatedAt() + "</td>" + "<td>" + mark.getTeacher().getFirstName()
				+ " " + mark.getTeacher().getLastName() + "</td>" + "</tr>" + "</table></body></html>";

		helper.setText(text, true);
		if(emails.size()>0) {
		mailSender.send(mail);
		}
	}

}
