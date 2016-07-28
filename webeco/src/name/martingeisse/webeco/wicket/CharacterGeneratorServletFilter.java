/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco.wicket;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import name.martingeisse.ecotools.simulator.devices.chardisplay.CharacterGenerator;

/**
 * This filter intercepts HTTP requests to "/chargen/*" URLs and returns
 * and appropriate image.
 */
public class CharacterGeneratorServletFilter implements Filter {

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain remainingChain) throws IOException, ServletException {
		if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
			HttpServletRequest httpServletRequest = (HttpServletRequest)request;
			String path = httpServletRequest.getPathInfo();
			if (path != null && path.startsWith("/chargen/")) {
				handleCharacterGeneratorRequest((HttpServletRequest)request, (HttpServletResponse)response);
				return;
			}
		}
		remainingChain.doFilter(request, response);
	}
	
	/**
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void handleCharacterGeneratorRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		String path = request.getPathInfo();
		int pathLastSlashIndex = path.lastIndexOf('/');
		if (pathLastSlashIndex != -1) {
			path = path.substring(pathLastSlashIndex + 1);
		}
		
		if (validateUrl(path, request, response)) {
			int value = Integer.parseInt(path, 16);
			int characterCode = (value & 0xff);
			int attributeCode = ((value >> 8) & 0xff);
			int foregroundColor = getColor(attributeCode);
			int backgroundColor = getColor(attributeCode >> 4);
			byte[] characterData = CharacterGenerator.CHARACTER_DATA[characterCode];
			
			BufferedImage image = new BufferedImage(8, 16, BufferedImage.TYPE_BYTE_INDEXED);
			WritableRaster raster = image.getRaster();
			int[] transferArray = new int[1];
			for (int y=0; y<16; y++) {
				int line = characterData[y];
				for (int x=0; x<8; x++) {
					boolean pixelActive = ((line & (1 << x)) != 0);
					transferArray[0] = (pixelActive ? foregroundColor : backgroundColor);
					raster.setPixel(x, y, transferArray);
				}
			}
			
			OutputStream out = prepareSuccess(request, response);
			ImageIO.write(image, "png", out);
			finishSuccess(out);
		} else {
			emitError(response);
		}
	}
	
	private int[] colorCodeToColor = new int[] {
		0*36 + 0*6 + 0,
		0*36 + 0*6 + 3,
		0*36 + 3*6 + 0,
		0*36 + 3*6 + 3,
		3*36 + 0*6 + 0,
		3*36 + 0*6 + 3,
		3*36 + 3*6 + 0,
		4*36 + 4*6 + 4,
		2*36 + 2*6 + 2,
		0*36 + 0*6 + 5,
		0*36 + 5*6 + 0,
		0*36 + 5*6 + 5,
		5*36 + 0*6 + 0,
		5*36 + 0*6 + 5,
		5*36 + 5*6 + 0,
		5*36 + 5*6 + 5
	};
	
	private int getColor(int colorCode) {
		return colorCodeToColor[colorCode & 15];
	}
	
	/**
	 * 
	 */
	private boolean validateUrl(String path, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		return Pattern.matches("[0-9a-fA-F]{4}", path);
	}
	
	/**
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void emitError(HttpServletResponse response) throws IOException, ServletException {
		response.setStatus(404);
		response.setContentType("text/plain");
		response.getWriter().println("Resource not found.");
		response.getWriter().flush();
		response.getWriter().close();
	}
	
	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	private OutputStream prepareSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setStatus(200);
		response.setContentType("image/png");
		return response.getOutputStream();
	}
	
	/**
	 * @param s
	 */
	private void finishSuccess(OutputStream s) throws IOException, ServletException {
		s.flush();
		s.close();
	}
	
}
