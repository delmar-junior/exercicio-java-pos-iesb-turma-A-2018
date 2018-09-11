package com.turma2018.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.turma2018.domain.Task;

/**
 * Servlet implementation class TasksServlet
 */
@WebServlet(name="task", urlPatterns="/tasks/*")
public class TasksServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private static final List<Task> taskList = new ArrayList<Task>();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TasksServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json; charset=UTF=8");
		String pathInfo = request.getPathInfo();
		
		if (pathInfo == null || pathInfo.equals("/")) {
			doGetAll(request, response);
		} else {
			doGetById(request, response, pathInfo);
		}
	}
	
	protected void doGetAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Gson gson = new Gson();
		String json = gson.toJson(taskList);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(json);
	}
	
	protected void doGetById(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws ServletException, IOException {
		
		String[] splits = pathInfo.split("/");
		if (splits.length != 2) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		String taskId = splits[1];
		Task task = findTaskId(taskId);
		if (task == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		} else {
			Gson gson = new Gson();
			String json = gson.toJson(task);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(json);
		}
	}
	
	private Task findTaskId(String taskid) {
		Optional<Task> result = taskList.stream()
				.filter(t -> t.getId().equals(taskid))
				.findFirst();
		
		if (!result.isPresent()) {
			return null;
		}
		return result.get();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json; charset=UTF=8");
		Gson gson = new Gson();
		Task task = gson.fromJson(request.getReader(), Task.class);
		taskList.add(task);
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getWriter().write(gson.toJson(task));
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json; charset=UTF=8");
		String pathInfo = request.getPathInfo();
		
		if (pathInfo == null || pathInfo.equals("/")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		String[] splits = pathInfo.split("/");
		if (splits.length != 2) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		String taskId = splits[1];
		
		Gson gson = new Gson();
		Task taskNew = gson.fromJson(request.getReader(), Task.class);
		
		boolean isFind = false;
		for(Task task : taskList) {
			if(task.getId().equals(taskId)) {
				task.setTitle(taskNew.getTitle());
				task.setResume(taskNew.getResume());
				task.setIsDone(taskNew.getIsDone());
				isFind = true;
			}
		}
		
		if (!isFind) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}	
			
		String json = gson.toJson(taskNew);
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getWriter().write(json);
		
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json; charset=UTF=8");
		String pathInfo = request.getPathInfo();
		
		if (pathInfo == null || pathInfo.equals("/")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		String[] splits = pathInfo.split("/");
		if (splits.length != 2) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		String taskId = splits[1];
		Iterator<Task> it = taskList.iterator();
		while(it.hasNext()) {
			Task task = it.next();
			if(task.getId().equals(taskId)) {
				it.remove();
				Gson gson = new Gson();
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().write(gson.toJson(task));
			}
		}
	}

}
