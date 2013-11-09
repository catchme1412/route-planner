package com.travel.map;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MapServlet
 */
@WebServlet("/map")
public class MapServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public MapServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		Coordinate bangalore = new Coordinate(12.970214, 77.56029);
//		Coordinate salem = new Coordinate(11.6500, 78.1600);
		Coordinate bangalore1 = new Coordinate(11.6500, 78.1600);
		Coordinate salem1 = new Coordinate(9.6500, 78.1600);
		
		ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
//		coordinates.add(bangalore);
//		coordinates.add(salem);
		coordinates.add(bangalore1);
		coordinates.add(salem1);
		request.setAttribute("coordinates", coordinates);
		request.getRequestDispatcher("/googlemap.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
		Coordinate bangalore = new Coordinate(12.970214, 77.56029);
		Coordinate salem = new Coordinate(11.6500, 78.1600);
		
		ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
		coordinates.add(bangalore);
		coordinates.add(salem);
		request.setAttribute("coordinates", coordinates);
		request.getRequestDispatcher("/googlemap.jsp").forward(request, response);
	}

}
