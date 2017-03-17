package ecs189.querying.github;

import ecs189.querying.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent on 10/1/2017.
 */
public class GithubQuerier {

    private static final String BASE_URL = "https://api.github.com/users/";

    public static String eventsAsHTML(String user) throws IOException, ParseException {
        List<JSONObject> response = getEvents(user);
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        int i = 0;
        for (JSONObject event : response) {
            // Get event type
            String type = event.getString("type");
            // Get created_at date, and format it in a more pleasant style
            String creationDate = event.getString("created_at");
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = inFormat.parse(creationDate);
            String formatted = outFormat.format(date);
            // Add type of event as header
            sb.append("<h3 class=\"type\">");
            sb.append(type);
            sb.append("</h3>");
            // Add formatted date
            sb.append(" on ");
            sb.append(formatted);
            sb.append("<br />");

            JSONObject payloads = event.getJSONObject("payload");

            JSONArray commits = payloads.getJSONArray("commits");

            sb.append("<table class=\"table\">");
            sb.append("<thead>");
            sb.append("<tr>" + "<th>SHA</th>");
            sb.append("<th>Message</th> </tr>");
            sb.append("</thead>");


            sb.append("<tbody>");


            for ( int j = 0 ; j < commits.length(); j++) {
                sb.append("<tr>");
                sb.append("<td class = \"col-md-2\">");
                sb.append(commits.getJSONObject(j).getString("sha").substring(0,8));
                sb.append("</td> ");
                sb.append("<td class = \"col-md-2\">");
                sb.append(commits.getJSONObject(j).getString("message"));
                sb.append("</td>");
                sb.append("</tr>");
            }

            sb.append("</tbody>");
            sb.append("</table>");
            // Add collapsible JSON textbox (don't worry about this for the homework; it's just a nice CSS thing I like)
            sb.append("<a data-toggle=\"collapse\" href=\"#event-" + i + "\">JSON</a>");
            sb.append("<div id=event-" + i + " class=\"collapse\" style=\"height: auto;\"> <pre>");
            sb.append(event.toString());
            sb.append("</pre> </div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static List<JSONObject> getEvents(String user) throws IOException {
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        String url = BASE_URL + user + "/events";
        System.out.println(url);
        JSONObject json = Util.queryAPI(new URL(url));
        System.out.println(json);
        JSONArray events = json.getJSONArray("root");
        int count = 0;
        for (int i = 0; i < events.length() && count < 10; i++) {
            if(events.getJSONObject(i).getString("type").equals("PushEvent")){
                eventList.add(events.getJSONObject(i));
                count ++;}
        }
        return eventList;
    }
}