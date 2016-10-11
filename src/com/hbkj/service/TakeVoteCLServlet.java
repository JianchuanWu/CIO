package com.hbkj.service;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hbkj.model.AdviceBO;
import com.hbkj.model.VoteBO;

/**
 * Servlet implementation class TakeVoteCLServlet
 */
@WebServlet("/TakeVoteCLServlet")
public class TakeVoteCLServlet extends HttpServlet implements Servlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		//��ȡ�û�id
		String s_userId = (String)request.getSession().getAttribute("userId");
		int userId = Integer.parseInt(s_userId);
		//��ȡ������ͬ�Ĳ������
		String[] s_sectorAdviceId = request.getParameterValues("sectorAdviceId");
		//��ȡ������ͬ��С�����
		String s_groupAdviceId = request.getParameter("groupAdviceId");
		
		VoteBO VBO = new VoteBO();
		AdviceBO ABO = new AdviceBO();
		int agreeCount = 0;
		int adviceId = 0;
		boolean flag = false;
		//�Բ��������count���м�һ�����������ĳ������Ѿ�����ޣ���ֱ������
		if(s_sectorAdviceId != null){
			for(int i = 0; i < s_sectorAdviceId.length; i++){
				adviceId = Integer.parseInt(s_sectorAdviceId[i]);
				try {
					VBO.addVote(userId, adviceId);
					agreeCount = ABO.getCountByAdviceId(adviceId);
					flag = ABO.updateAdviceCount(adviceId, agreeCount+1);
				} catch (Exception e) {
					System.out.println("���Ը�����Ѿ�������Լ��Ŀ���");
					continue;
				}
			}
		}
		
		
		if(s_groupAdviceId!=null){
			adviceId = Integer.parseInt(s_groupAdviceId);
			//���û�жԸ����ڵ�������й���������vote����Ӽ�¼��������¼�¼
			if(!VBO.alreadyVoteInGroup(userId)){
				VBO.addVote(userId, adviceId);
				agreeCount = ABO.getCountByAdviceId(adviceId);
				flag = ABO.updateAdviceCount(adviceId, agreeCount+1);
				if(agreeCount+1 == 2){
					ABO.updateAdviceLevel(adviceId,"1");
					ABO.clearVote(userId);
					ABO.clearAdvice(userId);
					ABO.clearAdviceCount(adviceId);
				}
			}else{
				//��֮ǰ����������count���м�һ
				int old_adviceId = VBO.getAdviceIdByUserId(userId);
				agreeCount = ABO.getCountByAdviceId(old_adviceId);
				ABO.updateAdviceCount(old_adviceId, agreeCount-1);
				//���±��˵��޵������id���Ը������count��һ
				VBO.updateVote(userId, adviceId);
				agreeCount = ABO.getCountByAdviceId(adviceId);
				flag = ABO.updateAdviceCount(adviceId, agreeCount+1);
				if(agreeCount+1 == 2){
					ABO.updateAdviceLevel(adviceId,"1");
					ABO.clearVote(userId);
					ABO.clearAdvice(userId);
					ABO.clearAdviceCount(adviceId);
				}
			}
		}
		if(flag){
			request.getRequestDispatcher("WEB-INF/voteSuccess.jsp").forward(request, response);
		}else{
			request.getRequestDispatcher("WEB-INF/err.jsp").forward(request, response);			
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
