<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="http://code.jquery.com/jquery.js"></script>
<script type="text/javascript">
	var u=0;
	var i=0;
	$(function(){
		$('.upBtn').click(function(){
			$('.reply_update').hide();
			$('.reply_insert').hide();
			var no = $(this).attr('data-no');
			console.log(no);
			if(u==0){
				$('#m'+no).show();
				u=1;
			}else{
				$('#m'+no).hide();
				u=0;
			}
		});
		$('.inBtn').click(function(){
			$('.reply_update').hide();
			$('.reply_insert').hide();
			var no = $(this).attr('data-no');
			console.log(no);
			if(i==0){
				$('#i'+no).show();
				i=1;
			}else{
				$('#i'+no).hide();
				i=0;
			}
		});
	});
</script>
<title>Insert title here</title>
</head>
<body>
<table class="table">
		<c:forEach var="rvo" items="${list }">
			<tr>
				<td class="text-left">
					<c:if test="${rvo.group_tab>0}">
						<c:forEach var="i" begin="1" end="${rvo.group_tab }">
							&nbsp;&nbsp;
						</c:forEach>
						<img src="../img/detail/icon_reply.gif"/>
					</c:if>
					${rvo.memberId}&nbsp;<span style="color:#999;">(${rvo.dbday})</span>
				</td>
				<td class="text-right">
					<c:if test="${rvo.msg!='관리자가 삭제한 댓글입니다'}">
						<c:if test="${ss_member.memberId==rvo.memberId }">
							<span class="btn btn-xs btn-primary upBtn" data-no="${rvo.no}">수정</span>
							<a href="../detail/detail_review_reply_delete.do?rrno=${rvo.no}&no=${no}&type=${type}" class="btn btn-xs btn-danger">삭제</a>
						</c:if>
							<span class="btn btn-xs btn-success inBtn" data-no="${rvo.no}">댓글</span>
					</c:if>
				</td>
			</tr>			
			<tr>
				<td colspan="2" class="text-left" valign="top">
					<c:if test="${rvo.group_tab>0}">
						<c:forEach var="i" begin="1" end="${rvo.group_tab }">
							&nbsp;&nbsp;
						</c:forEach>
					</c:if>	
					<pre style="white=space: pre-wrap;">${rvo.msg }</pre>
				</td>
			</tr>
<%-- 			<tr id="m${rvo.no}" style="display:none" class="reply_update">
 --%>	
 			<tr id="m${rvo.no}" style="display:none" class="reply_update">
 				<td colspan="2">
					<form method="post" action="../detail/detail__review_reply_update.do">
						<input type=hidden name=rrno value="${rvo.no}">
						<input type=hidden name=no value="${no}">
						<input type=hidden name=type value="${type}">
						<textarea rows="5" cols="110" name="msg" style="float:left">${rvo.msg }</textarea>
						<input type="submit" class="btn btn-sm btn-primary" style="height:100px; float:left" value="수정하기">
					</form>
				</td>
			</tr>	
			
			<tr id="i${rvo.no}" style="display:none" class="reply_insert">
				<td colspan="2">
					<form method="post" action="../detail/detail__review_reply_reply_insert.do">
						<input type=hidden name=rvno value="${rvno}">
						<input type=hidden name=pno value="${rvo.no}">
						<input type=hidden name=no value="${no}">
						<input type=hidden name=type value="${type}">
						<textarea rows="5" cols="110" name="msg" style="float:left"></textarea>
						<input type="submit" class="btn btn-sm btn-primary" style="height:100px; float:left" value="댓글쓰기">
					</form>
				</td>
			</tr>				
		</c:forEach>
	</table>
	<table class="table">
		<tr>
			<td>
				<form method="post" action="../detail/detail_review_reply_insert.do">
					<input type=hidden name=rvno value="${rvno}">
					<input type=hidden name=no value="${no}">
					<input type=hidden name=type value="${type}">
					<textarea rows="5" cols="110" name="msg" style="float:left"></textarea>
					<input type="submit" class="btn btn-sm btn-primary" style="height:100px; float:left" value="새로 댓글쓰기">
				</form>
			</td>
		</tr>
	</table>
</body>
</html>