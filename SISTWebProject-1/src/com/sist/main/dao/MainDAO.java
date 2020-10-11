package com.sist.main.dao;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.sist.mypage.model.WishListVO_u;

public class MainDAO {
	
	private static SqlSessionFactory ssf;
	static {
		try {
			Reader reader = Resources.getResourceAsReader("Config.xml");
			ssf = new SqlSessionFactoryBuilder().build(reader);
		} catch (Exception e) {
			System.out.println("MainDAO:static block:");
			e.printStackTrace();
		}
	}
	
	public static LoginVO getLoginInfo(String memberId, String pwd) {
		LoginVO vo = new LoginVO();
		SqlSession session = null;
		
		try {
			session = ssf.openSession();
			vo = session.selectOne("getLoginInfo", memberId);
			
			if(vo.getPwd().equals(pwd)) {
				vo.setState("SUCCESS");
				return vo;
			} else {
				vo.setState("NOPWD");
				return vo;
			}
		} catch (Exception e) {
			LoginVO lvo = new LoginVO();
			lvo.setState("NOID");
			return lvo;
		} finally {
			if(session!=null) session.close();
		}
	}
	
	public static void addItemToCookies(HttpServletRequest request, HttpServletResponse response, int type, int no) {
		List<String> slowdownCookies = new ArrayList<String>();
		
		Cookie[] cookies = request.getCookies();
		for(int i=0; i<cookies.length; i++){
			if(cookies[i].getName().startsWith("slowdown")) {
				if(cookies[i].getName().endsWith(type+"_"+no)) {
					cookies[i].setMaxAge(0);
					cookies[i].setPath("/");
					response.addCookie(cookies[i]);
				} else {
					slowdownCookies.add(cookies[i].getName());
				}
			}
		}
		
		if(slowdownCookies.size()==0) {
			String cookie = "slowdown"+"10"+"#"+type+"_"+no;
			Cookie addCookie = new Cookie(cookie, "");
			addCookie.setPath("/");
			addCookie.setMaxAge(60*60*24);
			response.addCookie(addCookie);
		} else {
			Collections.sort(slowdownCookies);
			String lastCookie = slowdownCookies.get(slowdownCookies.size()-1);
			int lastIdx = Integer.parseInt(lastCookie.substring(8, lastCookie.indexOf("#")));
			
			String cookie = "slowdown"+(lastIdx+1)+"#"+type+"_"+no;
			Cookie addCookie = new Cookie(cookie, "");
			addCookie.setPath("/");
			addCookie.setMaxAge(60*60*24);
			response.addCookie(addCookie);
		}		
	}
	
	public static List<HomeItemVO> getHIListFromCookies(HttpServletRequest request) {
		List<HomeItemVO> list = new ArrayList<HomeItemVO>();
		
		List<String> slowdownCookies = new ArrayList<String>();
		Cookie[] cookies = request.getCookies();
		if(cookies==null) return list;
		for(int i=0; i<cookies.length; i++){
			if(cookies[i].getName().startsWith("slowdown")) {
				slowdownCookies.add(cookies[i].getName());
			}
		}
		Collections.sort(slowdownCookies);
		
		int size = slowdownCookies.size();
		for(int i=0; i<Math.min(size, 4); i++) {
			String cookie = slowdownCookies.remove(slowdownCookies.size()-1);
			String[] type_no = cookie.substring(cookie.indexOf("#")+1).split("_");
			String type = type_no[0];
			String no = type_no[1];
			
			String table;
			String name;
			String photo;
			switch (type) {
				case "1" : {
					table = "tourplace";
					name = "tname";
					photo = "tphoto";
					break;
				}
				case "2" : {
					table = "restaurant";
					name = "rname";
					photo = "rphoto";
					break;
				}
				default : {
					table = "festival";
					name = "fname";
					photo = "fphoto";
					break;
				}
			}
			
			Map map = new HashMap();
			map.put("table", table);
			map.put("no", no);
			map.put("name", name);
			map.put("photo", photo);
			
			SqlSession session = null;
			try {
				session = ssf.openSession();
				HomeItemVO hivo = session.selectOne("getHomeItemFromCookie", map);
				hivo.setType(Integer.parseInt(type));
				list.add(hivo);
				
			} catch (Exception e) {
				System.out.println("MainDAO:getHIListFromCookies():");
				e.printStackTrace();
			} finally {
				if(session!=null) session.close();
			}
		}
		
		return list;
	}
	
	public static List<HomeItemVO> getSearchData(int type, String keyword) {
		List<HomeItemVO> list = new ArrayList<HomeItemVO>();
		
		String table;
		String name;
		String photo;
		switch (type) {
			case 1 : {
				table = "tourplace";
				name = "tname";
				photo = "tphoto";
				break;
			}
			case 2 : {
				table = "restaurant";
				name = "rname";
				photo = "rphoto";
				break;
			}
			default : {
				table = "festival";
				name = "fname";
				photo = "fphoto";
				break;
			}
		}
		
		Map map = new HashMap();
		map.put("table", table);
		map.put("name", name);
		map.put("photo", photo);
		map.put("keyword", keyword);
		
		SqlSession session = null;
		try {
			session = ssf.openSession();
			list = session.selectList("getSearchData", map);
			
		} catch (Exception e) {
			System.out.println("MainDAO:getSearchData():");
			e.printStackTrace();
		} finally {
			if(session!=null) session.close();
		}
		
		return list;
	}
	
	public static List<HomeItemVO> getHashTagSearchData(String keyword){
		List<HomeItemVO> list = new ArrayList<HomeItemVO>();
		
		SqlSession session = null;
		try {
			session = ssf.openSession();
			List<HomeItemVO> tempList = session.selectList("getTypeNoListBySearch", keyword);
			if(tempList.isEmpty()) return list;
			
			for(HomeItemVO vo : tempList) {
				int type = vo.getType();
				int no = vo.getNo();
				
				String table;
				String name;
				String photo;
				switch (type) {
					case 1 : {
						table = "tourplace";
						name = "tname";
						photo = "tphoto";
						break;
					}
					case 2 : {
						table = "restaurant";
						name = "rname";
						photo = "rphoto";
						break;
					}
					default : {
						table = "festival";
						name = "fname";
						photo = "fphoto";
						break;
					}
				}
				
				Map map = new HashMap();
				map.put("table", table);
				map.put("name", name);
				map.put("photo", photo);
				map.put("no", no);
				
				vo = session.selectOne("getHIVOData", map);
				vo.setType(type);
				list.add(vo);

			}
			
		} catch (Exception e) {
			System.out.println("MainDAO:getHashTagSearchData():");
			e.printStackTrace();
		} finally {
			if(session!=null) session.close();
		}
		
		return list;
	}
	
	public static List<HashTagVO> getHTListRegDESC(){
		List<HashTagVO> list = new ArrayList<HashTagVO>();
		
		SqlSession ss = null;
		try {
			ss = ssf.openSession();
			list = ss.selectList("getHTListRegDESC");
			
		} catch (Exception e) {
			System.out.println("MainDAO:getHTListRegDESC():");
			e.printStackTrace();
		} finally {
			if(ss!=null) ss.close();
		}
		
		return list;
	}
	
	public static List<HashTagVO> getPopularHTList(){
		List<HashTagVO> list = new ArrayList<HashTagVO>();
		
		SqlSession ss = null;
		try {
			ss = ssf.openSession();
			list = ss.selectList("getPopularHTList");
			
		} catch (Exception e) {
			System.out.println("MainDAO:getPopularHTList():");
			e.printStackTrace();
		} finally {
			if(ss!=null) ss.close();
		}
		
		return list;
	}
	
	public static List<HomeItemVO> getHIListByTagcode_page(HttpServletRequest request, int tagcode, int page) {
		List<HomeItemVO> list = new ArrayList<HomeItemVO>();
		
		int rowSize = 12;
		int end = rowSize*page;
		int start = end - (rowSize-1);
		
		Map map1 = new HashMap();
		map1.put("tagcode", tagcode);
		map1.put("start", start);
		map1.put("end", end);
		
		SqlSession session = null;
		try {
			session = ssf.openSession();
			List<HomeItemVO> tempList = session.selectList("getTypeNoListByTagcode_page", map1);
			if(tempList.isEmpty()) return list;
			
			for(HomeItemVO vo : tempList) {
				int type = vo.getType();
				int no = vo.getNo();
				
				String table;
				String name;
				String photo;
				switch (type) {
					case 1 : {
						table = "tourplace";
						name = "tname";
						photo = "tphoto";
						break;
					}
					case 2 : {
						table = "restaurant";
						name = "rname";
						photo = "rphoto";
						break;
					}
					default : {
						table = "festival";
						name = "fname";
						photo = "fphoto";
						break;
					}
				}
				
				Map map = new HashMap();
				map.put("table", table);
				map.put("name", name);
				map.put("photo", photo);
				map.put("no", no);

				vo = session.selectOne("getHIVOData", map);
				vo.setType(type);
				
				HomeItemVO tempvo = session.selectOne("getGradeReviewCntByTypeNo", vo);
				List<HashTagVO> htlist = session.selectList("getHTListByTypeNo", vo);
				vo.setGrade(tempvo.getGrade());
				vo.setReviewCnt(tempvo.getReviewCnt());
				vo.setHashtags(htlist.subList(0, Math.min(5, htlist.size())));
				
				list.add(vo);

			}
			
		} catch (Exception e) {
			System.out.println("MainDAO:getHIListByTagcode_page():");
			e.printStackTrace();
		} finally {
			if(session!=null) session.close();
		}
		
		LoginVO lvo = (LoginVO) request.getSession().getAttribute("ss_member");
		List<WishListVO_u> wishlist = new ArrayList<WishListVO_u>();
		try {
			wishlist = getWishListsByMemberId(lvo.getMemberId());
		} catch (Exception e1) {}
		
		for(HomeItemVO thivo : list) {
			for(WishListVO_u wlvo : wishlist) {
				if(thivo.getNo()==wlvo.getNo() && thivo.getType()==wlvo.getType()) {
					thivo.setWish(true);
					break;
				}
			}
		}
		
		return list;
	}
	
	public static HashTagVO getHashTagData(int tagcode) {
		HashTagVO vo = new HashTagVO();
		
		SqlSession ss = null;
		try {
			ss = ssf.openSession();
			vo = ss.selectOne("getHashTagData", tagcode);
			
		} catch (Exception e) {
			System.out.println("MainDAO:getHashTagData():");
			e.printStackTrace();
		} finally {
			if(ss!=null) ss.close();
		}
		
		return vo;
	}
	
	public static int getHTItemListTotalPage(int tagcode) {
		int total = 0;
		
		SqlSession ss = null;
		try {
			ss = ssf.openSession();
			total = ss.selectOne("getHTItemListTotalPage", tagcode);
			
		} catch (Exception e) {
			System.out.println("MainDAO:getHTItemListTotalPage():");
			e.printStackTrace();
		} finally {
			if(ss!=null) ss.close();
		}
		
		return total;
	}
	
	public static Map<HashTagVO, List<HomeItemVO>> getHTItemsListsByCookie(HttpServletRequest request, HttpServletResponse response){
		Map<HashTagVO, List<HomeItemVO>> lists = new HashMap<>();
		
		SqlSession ss = null;
		try {
			ss = ssf.openSession();
			List<HashTagVO> allHTList = new ArrayList<HashTagVO>();
			List<HashTagVO> selectedHTList = new ArrayList<HashTagVO>();
			
			Cookie[] cookies = request.getCookies();
			Cookie htcookie = null;
			if(cookies==null) return lists;
			for(int i=0; i<cookies.length; i++){
				if(cookies[i].getName().equals("sdhashtagcodes")) {
					htcookie = cookies[i];
					break;
				}
			}
			
			if(htcookie==null) {
				allHTList = ss.selectList("getHTAllList");
			} else {
				String[] hashtagcodes = htcookie.getValue().split("\\.");
				for(int i=0; i<hashtagcodes.length; i++){
					allHTList.add(getHashTagData(Integer.parseInt(hashtagcodes[i])));
				}
			}
			
			int allSize = allHTList.size();
			int[] selectedIdx = {-1,-1,-1,-1,-1};
			
			ml : 
			for(int i=0; i<5; i++) {
				int rand = Math.abs(new Random().nextInt())%allSize;
				for(int j=0; j<i; j++){
					if(allHTList.get(selectedIdx[j]).getTagcode()==allHTList.get(rand).getTagcode()) {
						i--;
						continue ml;
					}
				}
				selectedIdx[i] = rand;
			}
			
			for(int i=0; i<selectedIdx.length; i++){
				selectedHTList.add(allHTList.get(selectedIdx[i]));
			}
			
			for(int i=0; i<5; i++){
				HashTagVO vo = selectedHTList.get(i);
				int tagcode = vo.getTagcode();
				
				List<HomeItemVO> temp = getHIListByTagcode_page(request, tagcode, 1);
				temp = temp.subList(0, Math.min(4, temp.size()));
				
				lists.put(vo, temp);
			}
			
		} catch (Exception e) {
			System.out.println("MainDAO:getHTItemsListsByCookie():");
			e.printStackTrace();
		} finally {
			if(ss!=null) ss.close();
		}
		
		return lists;
	}
	
	public static void addHashTagCodeToCookie(HttpServletRequest request, HttpServletResponse response, int type, int no) {
		SqlSession ss = null;
		
		try {
			ss = ssf.openSession();
			
			Map map = new HashMap();
			map.put("type", type);
			map.put("no", no);
			List<Integer> tagcodes = ss.selectList("getTagcodeListByTypeNo", map);
			
			Cookie[] cookies = request.getCookies();
			Cookie cookie = new Cookie("sdhashtagcodes", "");
			for(int i=0; i<cookies.length; i++){
				if(cookies[i].getName().equals(cookie.getName())){
					cookie.setValue(cookies[i].getValue());
					break;
				}
			}
			
			String value = cookie.getValue();
			for(Integer tagcode : tagcodes) {
				value += "."+tagcode;
			}
			if(value.startsWith(".")) value = value.substring(1);
			cookie.setValue(value);
			cookie.setPath("/");
			cookie.setMaxAge(60*60*24);
			response.addCookie(cookie);
			
		} catch (Exception e) {
			System.out.println("MainDAO:addHashTagCodeToCookie():");
			e.printStackTrace();
		} finally {
			if(ss!=null) ss.close();
		}
		
	}
	
	public static void insertIntoWishlist(String id, String type, String no) {
		SqlSession ss = null;
		Map map = new HashMap();
		map.put("pId", id);
		map.put("pType", Integer.parseInt(type));
		map.put("pNo", Integer.parseInt(no));
		
		try {
			ss = ssf.openSession();
			ss.update("insertIntoWishlist", map);
			
		} catch (Exception e) {
			System.out.println("MainDAO:insertIntoWishlist():");
			e.printStackTrace();
		} finally {
			if(ss!=null) ss.close();
		}
	}
	
	public static void deleteFromWishlist(String id, String type, String no) {
		SqlSession ss = null;
		Map map = new HashMap();
		map.put("pId", id);
		map.put("pType", Integer.parseInt(type));
		map.put("pNo", Integer.parseInt(no));
		
		try {
			ss = ssf.openSession(true);
			ss.delete("deleteFromWishlist", map);
			
		} catch (Exception e) {
			System.out.println("MainDAO:deleteFromWishlist():");
			e.printStackTrace();
		} finally {
			if(ss!=null) ss.close();
		}
	}
	
	public static List<WishListVO_u> getWishListsByMemberId(String id) {
		SqlSession ss = null;
		List<WishListVO_u> list = new ArrayList<WishListVO_u>();
		
		try {
			ss = ssf.openSession();
			list = ss.selectList("getWishListsByMemberId", id);
			
		} catch (Exception e) {
			System.out.println("MainDAO:getWishListsByMemberId():");
			e.printStackTrace();
		} finally {
			if(ss!=null) ss.close();
		}
		
		return list;
	}
	
	public static void setReviewnoHashtagnames(int reviewno, String hashtags) {
		hashtags.trim().replaceAll("[\\s]+", " ");
		String[] tagnames = hashtags.split(" ");
		List<String> taglist = new ArrayList<String>();
		for(int i=0; i<tagnames.length; i++) {
			String tagname = tagnames[i];
			if(tagname.startsWith("#")) {
				tagname = tagname.replaceAll("[^가-힣]", "");
				if(tagname.length()!=0) taglist.add(tagname);
			}
		}
		
		SqlSession ss = null;
		Map map = new HashMap();
		
		try {
			ss = ssf.openSession();
			
			for(String tag : taglist) {
				map.put("pReviewno", reviewno);
				map.put("pTagname", tag);
				ss.update("setReviewnoHashtagname", map);
				map.clear();
			}
			
		} catch (Exception e) {
			System.out.println("MainDAO:setReviewnoHashtagnames():");
			e.printStackTrace();
		} finally {
			if(ss!=null) ss.close();
		}
	}
	
	public static HomeItemVO getGradeReviewCntByTypeNo(HomeItemVO vo) {
		SqlSession ss = null;
		
		try {
			ss = ssf.openSession();
			vo = ss.selectOne("getGradeReviewCntByTypeNo", vo);
			
		} catch (Exception e) {
			System.out.println("MainDAO:getGradeReviewCntByTypeNo():");
			e.printStackTrace();
		} finally {
			if(ss!=null) ss.close();
		}
		
		return vo;
	}
	
	/*public static void setWishlistRandomly(){
		
		SqlSession ss = null;
		
		try {
			ss = ssf.openSession();
			
			List<HomeItemVO> allTypeNoList = new ArrayList<HomeItemVO>();
					
			List<HomeItemVO> temp = ss.selectList("getAllNoNameFromTourplace");
			for(HomeItemVO vo : temp) {
				vo.setType(1);
				allTypeNoList.add(vo);
			}
			temp = ss.selectList("getAllNoNameFromRestaurant");
			for(HomeItemVO vo : temp) {
				vo.setType(2);
				allTypeNoList.add(vo);
			}
			temp = ss.selectList("getAllNoNameFromFestival");
			for(HomeItemVO vo : temp) {
				vo.setType(3);
				allTypeNoList.add(vo);
			}
			
			List<LoginVO> allIdList = ss.selectList("getAllMemberId");
			for(LoginVO vo : allIdList) {
				System.out.println(vo.getMemberId()+" \t====================================");
				
				List<HomeItemVO> wishlist = new ArrayList<HomeItemVO>();
				int cycle = Math.abs(new Random().nextInt())%15;
				System.out.println("cycle : "+cycle);
				target:
				for(int i=0; i<cycle; i++) {
					HomeItemVO thivo = allTypeNoList.get(Math.abs(new Random().nextInt())%allTypeNoList.size());
					System.out.println(i+"|"+thivo.getType()+"|"+thivo.getNo()+"|"+thivo.getName());
					if(wishlist.size()==0) {
						wishlist.add(thivo);
						continue target;
					}
					for(int j=0; j<wishlist.size(); j++) {
						HomeItemVO wlvo = wishlist.get(j);
						if(wlvo.getNo()==thivo.getNo() && wlvo.getType()==thivo.getType()) {
							i--;
							break;
						} else {
							wishlist.add(thivo);
						}
					}
				}
				for(HomeItemVO wlvo : wishlist) {
					System.out.println(wlvo.getType()+"|"+wlvo.getNo()+"|"+wlvo.getName());
				}
				System.out.println("====================================================");
			}
			
		} catch (Exception e) {
			System.out.println("MainDAO:setWishlistRandomly():");
			e.printStackTrace();
		} finally {
			if(ss!=null) ss.close();
		}
		
	}*/

}





































