<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="main-body mt--30">
	<div class="container box_1170">
		<div class="row" id="ReviewPosition">
			<div class="col-lg-6 col-md-6">
				<div class="single-post-item short">
					<div class="Review-search" id="Review_Search">
						<form class="example" action="#"
							style="margin: auto; max-width: 300px">
							<a href="../detail/detail.do?type=${type}&no=${no }&page=${page}&review=1">
								<input type="button"
								style="font-size: 30px; border: none; background: none; text-align: center;"
								placeholder="Search Posts" value="review" id="Review" />
								<input type="hidden" name=type id=reviewscroll value="${scroll}" />
							</a>
							<!-- onclick="javascript:history.back()" -->
						</form>
					</div>
				</div>
			</div>
			<div class="col-lg-6 col-md-6">
				<div class="single-post-item short">
					<div class="QnA-search" id="QnA_Seach">
						<form class="example" action="#"
							style="margin: auto; max-width: 300px">
							<input type="button"
								style="font-size: 30px; border: none; background: none; text-align: center;"
								placeholder="Search Posts" value="qna" id="QNA" /> 
								<input type="hidden" name=type id=type value="${type}" /> <input
								type="hidden" name=no id=no value="${no}" /> <input
								type="hidden" name=page id=page value="${page}" />
						</form>
					</div>
				</div>
			</div>
		</div>
		<!-- <div class="col-lg-10 post-list" id="Content-List">
	Start Post Area
	<section class="post-area"> -->
		<div id="result">
		<div class="row" id="reviewlist">
			<div class="col-lg-10">
				<div class="single-widget search-widget">
					<form class="example" action="#"
						style="margin: auto; max-width: 300px">
						<input type="text" placeholder="Search Posts" name="search2"
							id="keyword" />
						<button type="submit">
							<i class="fa fa-search"></i>
						</button>
					</form>
				</div>
			</div>
			<div class="col-lg-2" id="reviewbtn"
			<c:if test="${ empty ss_member }">style="display:none;"</c:if>>
			<a href="../detail/detail_review_insert.do?type=${type}&no=${no}"><input type="button" class="button"
				style="margin-top: 50px; background-color: #555555; color: white;"
				value="리뷰작성"/></a>
		</div>
		</div>

			<c:forEach var="vo" items="${list }">
				<div class="single-post-item">
					<h3>
						<a href="blog-details.html">${vo.title }</a>
					</h3>
					<br>
					<c:forEach var="vo2" items="${imglist }">
						<c:if test="${vo.reviewno eq vo2.reviewno}">
							<img src="${vo2.filepath }" width="180" height="180"
								style="display: inline-block" />
						</c:if>
					</c:forEach>

					<div style="width: 100%; height: 200px;">${vo.content}</div>
					
		 			<c:forEach var="vo3" items="${hashlist }">
						<c:if test="${vo.reviewno eq vo3.reviewno}">
							<a href="../home/htitemlist.do?tagcode=${vo3.tagcode }"><input type="button" value="${vo3.tagname }" style="display:inline;"></a>
						</c:if>	
					</c:forEach> 
					<div
						class="primary-btn text-uppercase mt-15 btnclick" id="r${vo.reviewno}" data-no="${vo.reviewno}">continue Reading</div>
					<div class="post-box" style="width: 100%" id='mouse_over'>
						<div class="d-flex">
							<div>
								<a href="#"> <img src="img/author/a1.png" alt="" />
								</a>
							</div>
							<div class="post-meta">
								<div class="meta-head">
									<a href="../home/profile_data.do?id=${vo.memberid}">${vo.memberid }</a>
								</div>
								<div class="meta-details">
									<ul>
										<li><a href="#"> <span class="lnr lnr-calendar-full"></span>
												<fmt:formatDate value="${vo.regdate }" pattern="yyyy-MM-dd" />
										</a></li>
										<li><a href="#"> <span class="lnr lnr-picture"></span>
												<fmt:formatDate value="${vo.expdate }" pattern="yyyy-MM-dd" />
										</a></li>
										<li><a href="#"> <span class="lnr lnr-coffee-cup"></span>
												${vo.likecount }
										</a></li>
										<li><a href="#"> <span class="lnr lnr-bubble"></span>
												${vo.grade }
										</a></li>
									</ul>
								</div>
							</div>
						</div>
					</div>
				</div>
<!-- ####################################################################################################################################### -->
<!-- ####################################################################################################################################### -->	
			<div id="replyresult${vo.reviewno}">
			
			</div>
			</c:forEach>
			<div class="text-center">
				<ul class="pagination">
					<c:if test="${startPage>1 }">
						<li><a
							href="../detail/detail.do?type=${type}&no=${no}&page=${startPage-1 }">&lt;</a></li>
					</c:if>
					<c:set var="typo" value="" />
					<c:forEach var="i" begin="${startPage }" end="${endPage }">
						<c:if test="${curpage==i }">
							<c:set var="typo" value="class=active" />
						</c:if>
						<c:if test="${curpage!=i }">
							<c:set var="typo" value="" />
						</c:if>
						<li ${typo }><a
							href="../detail/detail.do?type=${type}&no=${no}&page=${i}">${i}</a></li>
					</c:forEach>
					<c:if test="${endPage<allPage }">
						<li><a
							href="../detail/detail.do?type=${type}&no=${no}&page=${endPage+1 }">&gt;</a></li>
					</c:if>
				</ul>
			</div>
		</div>
	</div>

	