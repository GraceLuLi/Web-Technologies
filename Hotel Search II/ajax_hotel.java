
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import java.net.URL;
import java.net.URLConnection;
import org.json.*;


public class ajax_hotel extends HttpServlet {

	 public void doGet(HttpServletRequest request,HttpServletResponse response)
	    throws IOException, ServletException {
        String cityname = request.getParameter("city");
        cityname = cityname.replaceAll("\\s{1,}","+");
        String hotelname = request.getParameter("hotel");
		String urlstring = "http://cs-server.usc.edu:19595/cgi-bin/search_hw8.pl?city="+cityname+"&hotel="+hotelname;
		//System.out.println(urlstring);
		URL url = new URL(urlstring);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setAllowUserInteraction(false);
		InputStream urlStream = url.openStream();// urlStream store the return
													// XML file

		String xmlStr = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(urlStream));
		String str = null;
		while ((str = br.readLine()) != null) {
			xmlStr += str;
		}

		// JDOM parse the XML file
		Document hoteldoc = null;
		DocumentBuilder dBuilder;
		// catch the error
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			// SAXBuilder hotelBuilder=new SAXBuilder();
			InputStream parsedXmlStr = new ByteArrayInputStream(
					xmlStr.getBytes());
			dBuilder = dbf.newDocumentBuilder();
			hoteldoc = dBuilder.parse(parsedXmlStr);

		} catch (Exception f) {
			System.err.println(f);
		}

		JSONArray jsonArr = new JSONArray();
		JSONObject hotelJson = new JSONObject();
		JSONObject hotelsJson = new JSONObject();

		NodeList hotellist = hoteldoc.getElementsByTagName("hotel");

		// List
		// hotellist=hoteldoc.getRootElement().getChild("hotels").getChildren();
		for (int i = 0; i < hotellist.getLength(); i++) {
			Node eachHotel = hotellist.item(i);

			NamedNodeMap Attri = eachHotel.getAttributes();

			JSONObject jsonNode = new JSONObject();
			jsonNode.put("image_url", Attri.item(0).getNodeValue());

			jsonNode.put("location", Attri.item(1).getNodeValue());
			jsonNode.put("name", Attri.item(2).getNodeValue());
			jsonNode.put("no_of_reviews", Attri.item(3).getNodeValue());
			jsonNode.put("no_of_stars", Attri.item(4).getNodeValue());
			jsonNode.put("review_url", Attri.item(5).getNodeValue());


			jsonArr.put(jsonNode);
		}

		hotelJson.put("hotel", jsonArr);
		hotelsJson.put("hotels", hotelJson);
        
		PrintWriter out = response.getWriter();
		out.println(hotelsJson.toString());
	}
    
}