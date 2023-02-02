package com.example.jsoup.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
@Entity

public class Course {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; //DB 에는 null이 들어간다. int에서는 null을 허용하지 않는다.
	
	@Column(name = "thumbnail") //(괄호 내용 생략 가능) 
	private String thumnail; //imgUrl
	
	@Column
	private String title;
	
	@Column
	private String content; //description
	
	@Column
	private Integer realprice;
	
	@Column
	private Integer saleprice;
	
	@Column
	private String instructor;
	
	@Column
	private String link;
	
	@Column
	private String skills;
	
	@Column
	private Double rating;
	
}
