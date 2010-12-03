package net.pizey.hudson.trigger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

/**
 * A servlet to trigger Hudson builds, having authorised them.
 * 
 * @author timp
 * @since 28-11-2010
 */
public class Trigger extends HttpServlet {

  private static final long serialVersionUID = -2696656437199962046L;
  private static String user;
  private static String password;
  private static HashMap<String, ArrayList<String>> tokenUrls = new HashMap<String, ArrayList<String>>();

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    Properties p;
    try {
      p = fromResource(this.getClass(), this.getClass().getName() + ".properties");
    } catch (IOException e) {
      throw new ServletException(e);
    }
    for (Object keyO : p.keySet()) {
      String key = (String) keyO;
      if (key.equals("user"))
        user = p.getProperty("user");
      else if (key.equals("password"))
        password = p.getProperty("password");
      else {
        String urlList = (String) p.get(key);
        ArrayList<String> urls = new ArrayList<String>();
        String[] split = urlList.split(",");
        for (int i = 0; i < split.length; i++) {
          urls.add(split[i]);
        }
        tokenUrls.put(key, urls);
      }
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();
    String token = req.getParameter("token");
    String form = "<form method='POST'>" +
                  "<input type='text' name='token' value='" + token + "'>" +
                  "<input type='submit'>" +
                  "</form>";
    printAsPage(out, "Trigger", form);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String token = req.getParameter("token");
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();
    ArrayList<String> urls = tokenUrls.get(token);
    String bodyHtml = "<table>";
    if (urls == null) {
      System.err.println("Trigger: No build targets found for " + token);
      bodyHtml += "<tr><td>0</td><td>No build targets found</td></tr>";
    } else {
      for (String url : urls) {
        url = appendToken(url, token);
        int responseStatus = makePostRequest(url);
        bodyHtml += "<tr><td>" + responseStatus + "</td><td>" + url + "</td></tr>";
      }
    }
    bodyHtml += "</table>";
    printAsPage(out, "Triggered", bodyHtml);
  }

  private String appendToken(String url, String token) {
    String newUrl = url + (url.indexOf('?') > -1 ? "&" : "?") + "token=" + token;
    return newUrl;
  }

  private void printAsPage(PrintWriter out, String title, String bodyHtml) {
    out.println("<html>");
    out.println(" <head>");
    out.println("  <title>");
    out.println(title);
    out.println("  </title>");
    out.println(" </head>");
    out.println(" <body>");
    out.println("<h1>" + title + "</h1>");

    out.println(bodyHtml);
    out.println(" </body>");
    out.println("</html>");
  }

  public static int makePostRequest(String url) throws IOException {
    System.err.println(url);
    HttpURLConnection connection = getConnection(url);

    authorize(connection, user, password);
    connection.setRequestMethod("POST");
    connection.setDoOutput(true);

    return connection.getResponseCode();
  }

  protected static HttpURLConnection getConnection(String url) throws IOException {
    URL u = new URL(url);
    HttpURLConnection connection = (HttpURLConnection) u.openConnection();
    connection.addRequestProperty("Accept", "text/plain *; q=.2, */*; q=.2");
    return connection;
  }

  private static void authorize(HttpURLConnection connection, String username,
      String password) {
    String encodedAuthorisationValue =
        StringUtils.newStringUtf8(
            new Base64().encode((username + ":" + password).getBytes())).replaceAll("\n", "");
    connection.setRequestProperty("Authorization", "Basic "
        + encodedAuthorisationValue);
  }

  /**
   * Get a {@link Properties} object from a {@link Class}.
   */
  public static Properties fromResource(Class<?> clazz, String name)
      throws IOException {
    InputStream is = clazz.getResourceAsStream(name);

    if (is == null)
      throw new FileNotFoundException(name + ": is it in CLASSPATH?");

    Properties them = new Properties();
    them.load(is);

    return them;
  }

}
