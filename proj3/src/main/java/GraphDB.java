import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */

    private Map<Long, Node> nodes = new HashMap<>();
    private Map<Long, Edge> ways = new HashMap<Long, Edge>();
    //Map<Long, Set<Edge> neighbors = new HashMap<>();

    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        Map<Long, Node> resnode = new HashMap<>();   //为了不删除已经不存在键也是拼了
        for (Long id:nodes.keySet()){
            if (!nodes.get(id).adj.isEmpty()){
                resnode.put(id,nodes.get(id));
            }
        }
        nodes = resnode;
        // TODO: Your code here.
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        return nodes.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        return nodes.get(v).adj.keySet();
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        double shortest = Double.POSITIVE_INFINITY;
        long res = -1;
        for(Long id:nodes.keySet()){
            double dis = distance(lon,lat,nodes.get(id).lon,nodes.get(id).lat);
            if(dis < shortest){
                res = id;
                shortest = dis;
            }
        }
        return res;
    }

    /**
     * Gets the longitude of a vertex.
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        return nodes.get(v).lon;
    }

    /**
     * Gets the latitude of a vertex.
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        return nodes.get(v).lat;
    }


    static class Node {
        long id;
        double lon;
        double lat;
        String name;
        HashMap<Long,String> adj;
        Map<String, String> extraInfo;

        Node(long id, double lon,double lat) {
            this.id = id;
            this.lon = lon;
            this.lat = lat;
            this.adj = new HashMap<>();
            this.extraInfo = new HashMap<>();
        }
        void setName(String name){
            this.name = name;
        }
        void setAdj(long nodeid,String wayname){
            this.adj.put(nodeid,wayname);
        }
        void setExtraInfo(String k, String v){
            this.extraInfo.put(k,v);
        }
    }

    static class Edge{
        long from;
        long to;
        double weight;
        boolean isValid;
        double maxspeed;
        String name;
        Edge(long from, long to,double weight){
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
        void setName(String name){
            this.name = name;
        }
        void setValid(boolean isValid){
            this.isValid = isValid;
        }
    }

    void addNode(Node node){
        nodes.put(node.id,node);
    }
    void addEdge(Long id,Long from, Long to,String wayName){
        Node fromNode = nodes.get(from);
        Node toNode = nodes.get(to);
        ways.put(id, new Edge(from,to,distance(fromNode.lon,fromNode.lat,toNode.lon,toNode.lat)));
        fromNode.setAdj(to,wayName);
        toNode.setAdj(from,wayName);
    }
    void deleteNode(Node node){
        nodes.remove(node.id);
    }



    String getEdgeName(Long from,Long to){
        return nodes.get(from).adj.get(to);
    }
}
