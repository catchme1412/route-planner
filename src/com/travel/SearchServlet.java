package com.travel;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neo4j.examples.astarrouting.AStarRouting;
import org.neo4j.examples.astarrouting.RailwayStation;
import org.neo4j.examples.astarrouting.SearchCriteria;
import org.neo4j.graphdb.Path;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/route")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	AStarRouting graph = new AStarRouting();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SearchCriteria searchCriteria = getSearchCriteria(request);
		
		Path path = graph.shortestPath(searchCriteria);
		response.getWriter().print(path);
		
	}

	private SearchCriteria getSearchCriteria(HttpServletRequest request) {
		String source = request.getParameter("source");
		String destination = request.getParameter("destination");
		String priority = request.getParameter("priority");
		SearchCriteria searchCriteria = new SearchCriteria();
		
		searchCriteria.setSource(graph.getNode(source));
		searchCriteria.setDestination(graph.getNode(destination));
		searchCriteria.setPriority(priority);
		return searchCriteria;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
