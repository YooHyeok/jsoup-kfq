package com.example.jsoup.controller;

import java.util.Objects;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jsoup.repository.Course;
import com.example.jsoup.repository.CourseReposiotry;

@RestController
public class InflearnAlllController {
	
	
	// 1페이지부터 20페이지까지 크롤링
	private final int FIRST_PAGE_INDEX=1;
	private final int LAST_PAGE_INDEX=20;
	
	@Autowired
	CourseReposiotry courseRepository;
	
	
	
	@GetMapping("/all")
	public String all() {
		Integer idx = 1;
		final String inflearnUrl = "https://www.inflearn.com/courses/it-programming?order=seq&page=";
		StringBuilder sb = new StringBuilder();
		try {
			for(int i=FIRST_PAGE_INDEX; i<=LAST_PAGE_INDEX; i++) {
				System.out.println("1차 포문");
				String url = inflearnUrl+i;
				Connection conn = Jsoup.connect(url);
				
				Document doc = conn.get();
				
				Elements imgUrlElems = doc.getElementsByClass("swiper-lazy");
				Elements titleElems = doc.select("div.card-content>div.course_title");
				Elements priceElems = doc.getElementsByClass("price");
				// etc()
				Elements instructorElems = doc.getElementsByClass("instructor");
				
				Elements innerUrlElems = doc.select("a.course_card_front");
				Elements descriptionElems = doc.select("p.course_description");
				Elements skillElems = doc.select("div.course_skills>span");
				
				for(int j=0; j<imgUrlElems.size(); j++) {
					try {
						System.out.println("2차 포문");
						String imgUrl = imgUrlElems.get(j).attr("abs:src");
						String title = titleElems.get(j).text();;
						String instructor = instructorElems.get(j).text();;
						
						String price = priceElems.get(j).text();;
						
						int realPrice=0, salePrice=0;
						if(!price.equals("무료")) {
							realPrice = toInt(getRealPrice(price).replace("\\", ""));
							salePrice = toInt(getSalePrice(price).replace("\\W", ""));
						}
						
						String description = descriptionElems.get(j).text();
						String skills = skillElems.get(j).text().replace("\\s","");
						
						String innerUrl = innerUrlElems.get(j).attr("abs:href"); //강의 링크
						Connection innerConn = Jsoup.connect(innerUrl);
						Document innerDoc = innerConn.get();
						Element ratingElems = innerDoc.selectFirst("div.dashboard-star__num");
						double rating = Objects.isNull(ratingElems) ? 0.0 : Double.parseDouble(ratingElems.text());
						rating = Math.round(rating*100)/100.0;
						
						sb.append("순서: "+ idx++ +"<br/>");
						sb.append("썸네일: "+imgUrl+"<br/>");
						sb.append("강의제목: "+title+"<br/>");
						sb.append("강의 강사: "+instructor+"<br/>");
						
						sb.append("강의 가격: "+price+"<br/>");
						if(realPrice != salePrice)sb.append("할인 가격: "+salePrice+"<br/>");
						sb.append("강의 링크: "+innerUrl+"<br/>");
						sb.append("강의 상세: "+description+"<br/>");
						sb.append("기술스택: "+skills+"<br/>");
						sb.append("평점: "+rating+"<br/>");
						sb.append("페이지 정보 : "+i+"<br/><br/>");
						System.out.println("페이지 정보 : "+i+"<br/><br/>");
						
						//생성자를 통해 데이터 주입
						courseRepository.save(
								new Course(null
										, imgUrl
										, title
										, description
										, realPrice
										, salePrice
										, instructor
										, innerUrl
										, skills
										, rating
										)
								);
					}catch(JpaSystemException e) {
						e.printStackTrace();
					}
				}
					sb.append(i+"페이지 크롤링 완료 <br/><br/>");
			}
			return "다운로드 성공";
		} catch (Exception e) {
			e.printStackTrace();
			return "다운로드 실패";
		}
//			return sb.toString();
	}
	
	/**
	 * 
	 * @param price
	 */
	private void splitAnimallsEx(String price) {
		String animals = "monkey,dog,cat";
		String[] arrAni = animals.split(","); //배열요소에 ,로 끊어서 순차적으로 반환.
		for(String ani : arrAni) {
			System.out.println(ani);
		}
	}
	
	/**
	 * 
	 * @param price
	 * @return
	 */
	private String getRealPrice(String price) {
		return price.split(" ")[0];
	}
	
	/**
	 * getSalePrice()
	 * 
	 * @param price - 가격
	 * @return 길이가 1이면 0번째요소 반환 | 길이가 2이면 1번째 요소 반환
	 */
	private String getSalePrice(String price) {
		String[] prices = price.split(" ");
		return prices.length==1? prices[0]:prices[1]; // 세일 가격이 들어있다면 length가 2 들어있지않으면 1
	}
	
	/**
	 * toInt()
	 * ₩ 문자와 , 문자를 제거 후 int로 변환
	 * @param str
	 * @return
	 */
	private int toInt(String str) {
		str = str.replaceAll("₩", "");
		str = str.replaceAll(",", "");
		return Integer.parseInt(str);
	}
}
