package com.example.jsoup.controller;


import java.util.Objects;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JsoupController {
	/**
	 * thumnail() - 썸네일
	 * @return
	 */
	@GetMapping("/thumnail")
	public String thumnail() {
		final String inflearnUrl = "https://www.inflearn.com/courses/it-programming";
		Connection conn = Jsoup.connect(inflearnUrl); // jsoup을 통해 크롤링할 url을 연결 객체에 저장
		// StringBuilder 문자열 연산이 많고 단일쓰레드이거나 동기화를 고려하지 않아도 되는 경우
		// String  문자열 연산이 적고 멀티쓰레드 환경일 경우
		StringBuilder sb = new StringBuilder();
		String s = "";
		try {
			// Documnet 객체 생성
			/* DOM이란?
			   : DOM(Document Object Model) 은 BOM중 하나입니다.
			   BOM(Browse Object Model)이라는 브라우저 객체 모델의 최상위 객체는 window라는 객체가 있고,
			   DOM은 이 window객체의 또하나의 객체이기도 하다.
			 */
			Document document = conn.get(); // 커넥션 으로 부터 브라우저의 최상위 Dom객체에  저장 - jsoup.nodes.Document
			
			// Elements 객체 생성
			/* HTML의 엘리먼트를 추상화한 객체 */
			// Elements 클래스는 ArrayList를 상속받는다.
			Elements imgUrlElements = document.getElementsByClass("swiper-lazy"); //클래스명으로 얻어온 데이터를 Elements라는 List에 저장.
			for(Element element : imgUrlElements) {
//				s += element.toString(); //String 으로 구현
//				sb.append(element); // <div>태그를 포함한다.
				sb.append(element.attr("abs:src")+"<br/>"); // abs : 절대값 src의 절대값 value를 가져온다.
				sb.append(element.attr("abs:alt")+"<br/>"); // abs : 절대값 alt의 절대값 value를 가져온다.
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		return s;//String 으로 구현
		return sb.toString();
	}
	
	/**
	 * courseTitle() 제목
	 * @return
	 */
	@GetMapping("/title")
	public String courseTitle() {
		final String inflearnUrl = "https://www.inflearn.com/courses/it-programming";
		Connection conn = Jsoup.connect(inflearnUrl);
		StringBuilder sb = new StringBuilder();
		try {
			Document document = conn.get();
			Elements elCourseTitles = document.getElementsByClass("course_title"); // 마우스hover시 랜더링되는 <p>태그에서도 동일한 클래스명으로 사용되기 때문에 두번 출력한다.
			Elements elCourseTitles2 = document.select("div.card-content>div.course_title");
			
			for(Element element : elCourseTitles2) {
//				sb.append(element); // <div>태그를 통째로 가져온다.
				System.out.println("1");
				sb.append(element.text()+"<br/>"); // <div>태그사이의 text노드 값만 가져온다.
				System.out.println("2");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString(); 
	}
	
	/**
	 * score() - 별점~
	 * @return
	 */
	@GetMapping("/score")
	public String score() {
		final String inflearnUrl = "https://www.inflearn.com/courses/it-programming";
		Connection conn = Jsoup.connect(inflearnUrl);
		StringBuilder sb = new StringBuilder();
		try {
			Document document = conn.get();
			Elements elements = document.select("a.course_card_front"); //
			for(Element element : elements) {
				String scoreUrl = element.attr("abs:href");
				try {
					Connection innerConnection = Jsoup.connect(scoreUrl);
					document = innerConnection.get();
//					elements = document.select("span.cd-header__info--star>strong");
//					elements = document.select("div.cd-review__dashboard>div>div.dashboard-star__num");
					//리턴타입이 배열이지만 하나만 이므로
//					Element ratingElement = document.select("div.cd-review__dashboard>div>div.dashboard-star__num").get(0);
//					Element ratingElement = document.select("div>div.dashboard-star__num").get(0);
					Element ratingElement = document.selectFirst("div.dashboard-star__num");
					System.out.println(ratingElement);
					double rating = Objects.isNull(ratingElement) ? 0.0 : Double.parseDouble(ratingElement.text());
					rating = Math.round(rating*100)/100.0;
//						sb.append(ratingElement.text());
					sb.append(element.attr("abs:href")+"평점 : "+rating+"<br/>");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sb.toString(); 
	}
	
	/**
	 * etc() - 강의자, 부가설명, 기술스택
	 * @return
	 */
	@GetMapping("/etc")
	public String etc() {
		final String inflearnUrl = "https://www.inflearn.com/courses/it-programming";
		Connection conn = Jsoup.connect(inflearnUrl);
		StringBuilder sb = new StringBuilder();
		try {
			Document document = conn.get();
			Elements instructorElements = document.getElementsByClass("instructor");
			Elements descriptionElements = document.select("p.course_description");
			Elements skillElements = document.select("div.course_skills>span");
			
			for(int i=0; i<instructorElements.size(); i++) {
				String instructor = instructorElements.get(i).text();
				String description = descriptionElements.get(i).text();
				String skills = skillElements.get(i).text().replace("\\s", "");

				sb.append("강의자: "+instructor+"<br/> 강의 부가 설명: "+description+"<br/> 기술스택: "+skills+"<br/><br/>");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	
	
	/**
	 * price() - 금액
	 * @return
	 */
	@GetMapping("/price")
	public String price() {
		final String inflearnUrl = "https://www.inflearn.com/courses/it-programming";
		Connection conn = Jsoup.connect(inflearnUrl);
		StringBuilder sb = new StringBuilder();
		try {
			Document document = conn.get();
			Elements elprice = document.select("div.card-content>div.price");
			for(Element element : elprice) {
				
//				sb.append(element); // <div>태그를 통째로 가져온다.
//				sb.append(element.text()+"<br/>"); // <div>태그사이의 text노드 값만 가져온다.

				String price = element.text();
				String realPrice = getRealPrice(price);
				String salePrice = getSalePrice(price); //sale가격 추출
				
				int nrealPrice = toInt(realPrice);
				int nsalePrice = toInt(salePrice);
				
				sb.append("가격"+nrealPrice);
				if(nrealPrice!= nsalePrice)
					sb.append("&nbsp; 할인가격"+nsalePrice);
				sb.append("<br/>");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sb.toString(); 
	}
	
	private void splitAnimallsEx(String price) {
		String animals = "monkey,dog,cat";
		String[] arrAni = animals.split(","); //배열요소에 ,로 끊어서 순차적으로 반환.
		for(String ani : arrAni) {
			System.out.println(ani);
		}
	}
	
	private String getRealPrice(String price) {
		System.out.println("getRealPrice() 호출");
		System.out.println("getRealPrice 파라미터 : "+price);
		System.out.println(" ");
		return price.split(" ")[0];
	}
	
	/**
	 * getSalePrice()
	 * 
	 * @param price - 가격
	 * @return 길이가 1이면 0번째요소 반환 | 길이가 2이면 1번째 요소 반환
	 */
	private String getSalePrice(String price) {
		System.out.println("getSalePrice() 호출");
		System.out.println("getSalePrice 파라미터 : "+price);
		System.out.println(" ");
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
		System.out.println("toInt() 호출");
		System.out.println("toInt 파라미터 : "+str);
		
		str = str.replaceAll("₩", "");
		System.out.println(str +" 에서 ₩문자 제거");
		str = str.replaceAll(",", "");
		System.out.println(str+" 에서str ,문자 제거");
		System.out.println("최종 str : "+str);
		System.out.println(" ");
		
		return Integer.parseInt(str);
	}
	
}
